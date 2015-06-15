package dove.util.treelib;

import com.sun.istack.internal.NotNull;
import dove.util.concurrent.access.AccessOp;
import dove.util.concurrent.access.AccessScheduler;
import dove.util.concurrent.access.AccessTask;
import dove.util.concurrent.access.ExceptionWrapper;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * provides a tree-structure for
 * collecting and storing data
 * <p>
 * Notes:
 * <ul>
 * <li>
 * this class CAN be threadsafe, if specified.
 * if this tree is not made threadsafe, accessing this tree
 * within multiple threads will result in severe errors and
 * a possible damage/loss of data contained in this tree
 * <p>
 * even if this tree is made threadsafe, the results will highly vary upon
 * the timing in executing the single tasks
 * </li>
 * <li>
 * Typesafety is always guaranteed due to
 * clazz.
 * </li>
 * <li>
 * If you intend to override this class, the following methods
 * are usefull to override in order to create a more specific behaviour
 * getStringRep()
 * nodeCopy()
 * <p>
 * aswell, all methods provided MUST provide the option to be threadsafe
 * <p>
 * further more, all structural operations (adding/re-/moving) nodes,
 * have to take treeglobal objects into account, like the TreeScheduler,
 * the IDGiver, etc.
 * <p>
 * WARNING:
 * if a subclass of Tree uses an operation provided by this class,
 * the method named _*method_name* MUST ALWAYS be used to avoid deadlocks
 * in the scheduler
 * <p>
 * example:
 * <code>
 * ...
 * //some method overriding a method from tree
 * public void someOp()
 * {
 * ...
 * super.otherOp() //NEVER!!! if threadsafety is enabled, this leads to a deadlock
 * ...
 * super._otherOp() //CORRECT no deadlock
 * ...
 * }
 * ...
 * </code>
 * </li>
 * <li>
 * naming: due to the issue that some methods are aswell
 * used internally by other methods, every method exists twice
 * once as *full_name* and once as _*full_name*. the full_name method
 * provides threadsafety and is public, while _full_name methods dont
 * support any threadsafety and must only be used internally
 * </li>
 * </ul>
 * <p>
 * this tree is an implementation of multitree
 * <p>
 * List of logical rules this tree follows:
 * <ul>
 * <li>
 * 1. The list of children is a set
 * every Tree.content occures maximum once per list
 * of children
 * </li>
 * <li>
 * 2. To ensure a correct structure
 * (no circles, etc.), every node is only allowed to
 * have exactly one parent, multiple parents are prohibited
 * </li>
 * <li>
 * 3. To furthermore ensure structural stability, the contents of the nodes
 * may not be changed, once they are part of the tree (remove-operations excluded)
 * </li>
 * <li>
 * 4. The root doesn't hold any content and is ignored in all methods
 * using a path-variable, like insert at path-end, hasPath, etc.
 * </li>
 * <li>
 * 5. Every T t may only once be part of a list of peers.
 * <code>
 * Tree<T> tree = ?;
 * Tree<T> parent = tree.parent;
 * parent.remove(tree);
 * return parent.children.stream().findAny(t ->
 * t.content.equals(tree.content).isPresent();
 * </code>
 * must always return false.
 * </li>
 * <li>
 * 6. All algorithms that run through the tree in some
 * way run through the tree inorder (bottom top - right to left)
 * </li>
 * <li>
 * 7. if any instance of tree is an argument for a method, the stability of
 * this tree will be maintained independant of the result of the method
 * </li>
 * </ul>
 *
 * @param <T> typeparameter for
 *            the content of this treeelement
 */

@SuppressWarnings("unchecked")
public class Tree<T>
        implements Comparable<Tree<T>>,
        Serializable,
        Iterable,
        Cloneable {
    /**
     * serialVersionUID
     */
    public static final long serialVersionUID = -329877773592931935L;

    /**
     * the default exceptionwrapper
     * <p>
     * used for all treeoperations throwing treebuildexceptions
     */
    private static final ExceptionWrapper DEFAULT_WRAPPER =
            new ExceptionWrapper(new Class[]{TreeBuildException.class}) {
        @Override
        protected Exception handleExpectedException() {
            return new TreeBuildException(getException().getMessage(), getException());
        }

        @Override
        protected Exception handleUnexpectedException() {
            getException().printStackTrace();

            return null;
        }
    };

    /**
     * no exceptionwrapper
     * <p>
     * handles all exceptions as unexpected
     */
    private static final ExceptionWrapper NO_EXCEPTION_WRAPPER = new ExceptionWrapper(new Class[0]) {
        @Override
        protected Exception handleExpectedException() {
            return null;
        }

        @Override
        protected Exception handleUnexpectedException() {
            return getException();
        }
    };
    /**
     * the content-element of this node
     */
    public    T                   content;
    /**
     * the children of this treeelement
     * any collection can be used with the restrictions specified by
     * the rules mentioned above
     */
    protected Collection<Tree<T>> children;
    /**
     * the parent of this treeelement
     * every node can only be inserted once
     */
    private   Tree<T>             parent;
    /**
     * the globalvarhelper for this tree
     */
    private   TreeGlobalVarHelper globalVar;

    /**
     * the ID of this node
     * <p>
     * this ID is unique inside of this tree
     * getRoot() as root of the above mentioned tree
     */
    private int id = InternalIDHelper.NO_ID_SET;

    /**
     * content flag
     * this flag shows wether the content is set or there exists
     * no valid content for this node
     */
    private boolean contentSet;

    /**
     * the class of the content
     */
    private Class<T> clazz;

    /**
     * creates a new instance of Tree
     * with t as content
     *
     * @param t content of the new Treeelement
     */
    public Tree(@NotNull Class<T> clazz, T t) {
        this(clazz);

        content = t;

        contentSet = true;

        globalVar = new TreeGlobalVarHelper();
    }

    /**
     * creates a new treenode without content
     */
    public Tree(@NotNull Class<T> clazz) {
        children = new ArrayList<>();

        contentSet = false;

        this.clazz = clazz;

        globalVar = new TreeGlobalVarHelper();
    }

    //////////////////////////////////////////////////////////
    // content operations
    //////////////////////////////////////////////////////////

    /**
     * glues the subtrees together to a new Tree
     * basically this method generates a tree from given subtrees,
     * which only contains the listed subtrees and their parents down
     * to the root
     * <p>
     * warning: all Trees in t must be have the same root
     * <p>
     * this method must be that complex to ensure classsafety with
     * classes extending Tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param t     a list of subtree, which should be glued together to a new
     *              tree
     * @param clazz the class of the typeargument used when calling this method
     * @return the specified trees glued together to one tree
     * @throws IllegalArgumentException if the list is empty
     */
    public static <T> Tree<T> glue(List<Tree<T>> t, Class<T> clazz) {
        if (!t.isEmpty())
            return t.get(0).runOpExceptionSuppressed(() -> _glue(t, clazz), AccessTask.TaskOpType.WRITE);
        else
            return new Tree<>(clazz);
    }

    /**
     * glues the subtrees together to a new Tree
     * basically this method generates a tree from given subtrees,
     * which only contains the listed subtrees and their parents down
     * to the root
     * <p>
     * warning: all Trees in t must be have the same root
     * <p>
     * this method must be that complex to ensure classsafety with
     * classes extending Tree
     *
     * @param t     a list of subtree, which should be glued together to a new
     *              tree
     * @param clazz the class of the typeargument used when calling this method
     * @return the specified trees glued together to one tree
     * @throws IllegalArgumentException if the list is empty
     */
    protected static <T> Tree<T> _glue(List<Tree<T>> t, Class<T> clazz)
            throws IllegalArgumentException {
        if (t.isEmpty())
            return new Tree(clazz);

        //create a new list with the specified subtrees to operate on
        //this new list contains the copys of the trees in t, which will
        //be content of the actualy final tree
        List<Tree<T>> opOn = new ArrayList<>();
        opOn.addAll(t);
        opOn.replaceAll(Tree::_copy);

        //go down from each node to its root and create a copy
        //of its parents down to the root
        //and save the roots in fullPathList
        List<Tree<T>> fullPathList = new ArrayList<>();
        for (int i = 0; i < t.size(); i++) {
            //if one of the specified subtrees is already the root,
            //this node must contain all other roots, and therefore
            //is the valid result of this method
            if (t.get(i).isRoot())
                return opOn.get(i);

            Tree<T> currentParent = t.get(i).parent;
            Tree<T> currentParentTemp;
            Tree<T> prevParent = opOn.get(i);

            while (currentParent != null) {
                currentParentTemp = currentParent.nodeCopy();

                try {
                    currentParentTemp._add(prevParent);
                }
                catch (TreeBuildException ignored) {
                } //never thrown (original tree was valid, therefore
                //the resulting tree must aswell be valid
                prevParent = currentParentTemp;

                currentParent = currentParent.parent;
            }

            fullPathList.add(prevParent);
        }

        //merge all paths together to one single tree
        Tree<T> result = fullPathList.get(0);
        for (int i = 1; i < fullPathList.size(); i++)
            try {
                result._merge(fullPathList.get(i));
            }
            catch (TreeBuildException ignored) {
            } //never thrown (original tree was valid

        return result;
    }

    /**
     * creates a list with the same content in the same order
     * like in collection
     *
     * @param collection the collections that should be converted
     * @param <T>        the type of collections content
     * @return a list with the same content like collection
     */
    private static <T> List<T> convertToList(Collection<T> collection) {
        List<T> result = new ArrayList<>();

        for (T aCollection : collection) result.add(aCollection);

        return result;
    }

    /**
     * checks if o1 and ob2 fullfill either the condition that
     * o1 == o2 is true or both ob1 and ob2 are not null and equal each
     * other.
     * <p>
     * basically this method is an enhanced equals-method
     *
     * @param o1 first object to compare
     * @param o2 second object to compare
     * @return true, if one of the above listed conditions is true
     */
    protected static boolean equal(Object o1, Object o2) {
        return (o1 == o2 || (o1 != null && o2 != null && o1.equals(o2)));
    }

    /**
     * inserts the child as a new
     * node to the tree with this node as parent
     * <p>
     * public interface(see threadsafetynotes)
     *
     * @param child element to insert
     * @throws TreeBuildException if any structural rules are broken (see classdef)
     */
    public Tree<T> add(T child)
            throws TreeBuildException {
        return runOp(() -> _add(child), AccessTask.TaskOpType.WRITE);
    }

    /**
     * inserts the child as a new
     * node to the tree with this node as parent
     *
     * @param child element to insert
     * @throws TreeBuildException if any structural rules are broken (see classdef)
     */
    protected Tree<T> _add(T child)
            throws TreeBuildException {
        return _add(new Tree<>(clazz, child));
    }

    /**
     * inserts child as node of the tree
     * with this treeelement as parent
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param child the node to insert
     * @throws TreeBuildException if any structural rules are broken (see classdef)
     */
    public Tree<T> add(Tree<T> child)
            throws TreeBuildException {
        return runOp(() -> _add(child), AccessTask.TaskOpType.WRITE);
    }

    /**
     * inserts child as node of the tree
     * with this treeelement as parent
     *
     * @param child the node to insert
     * @throws TreeBuildException if any structural rules are broken (see classdef)
     */
    protected Tree<T> _add(Tree<T> child)
            throws TreeBuildException {
        //any node is only allowed to be inserted once
        //(see structural rules)
        if (child.parent != null)
            throw new TreeBuildException("Element is already element of the tree");

        children.add(child);

        child.parent = this;

        globalVar.addTree(child);

        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.ADD, this);

        return child;
    }

    /**
     * if the path isn't yet a part of the tree, all
     * nodes of the path, that aren't existent, will be
     * created and inserted in the tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param path the path that should be completed
     */
    public void completePath(T[] path) {
        runOpExceptionSuppressed(() -> {
            _completePath(path);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * if the path isn't yet a part of the tree, all
     * nodes of the path, that aren't existent, will be
     * created and inserted in the tree
     *
     * @param path the path that should be completed
     */
    protected void _completePath(T[] path) {
        Tree<T> child = this;

        for (T node : path) {
            boolean found = false;

            for (Tree<T> t : child.children) {
                if (equal(t.content, node)) {
                    child = t;

                    found = true;

                    break;
                }
            }

            if (!found) {
                try {
                    child._add(node);
                }
                catch (TreeBuildException ignored) {
                    //this exception will never be thrown, since
                    //there exists no child with content 'node'
                    //(proven by check in the for-loop : child.children)
                }
            }
        }

        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.ADD, this);
    }

    /**
     * inserts the node at the end of the specified path
     * any nodes, that don't exist are automatically created
     * <p>
     * the path starts directly after the root-element and
     * ends one level before the node
     * (node.content is no element of the path)
     * <p>
     * every member of path must be instanceof T
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param path the path from the root to the node (exclusive both)
     * @throws TreeBuildException if any of the structural rules of
     *                            this type of tree is broken
     * @params node the node to insert
     */
    public void insertAtPathEnd(T[] path, Tree<T> node) {
        runOpExceptionSuppressed(() -> {
            _insertAtPathEnd(path, node);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * inserts the node at the end of the specified path
     * any nodes, that don't exist are automatically created
     * <p>
     * the path starts directly after the root-element and
     * ends one level before the node
     * (node.content is no element of the path)
     * <p>
     * every member of path must be instanceof T
     *
     * @param path the path from the root to the node (exclusive both)
     * @throws TreeBuildException if any of the structural rules of
     *                            this type of tree is broken
     * @params node the node to insert
     */
    protected void _insertAtPathEnd(T[] path, Tree<T> node)
            throws TreeBuildException {
        if (node.getParent() != null)
            throw new TreeBuildException("Invalid tree state - this node already is content of the tree");

        Tree<T> curNode = _getRoot();

        for (T t : path) {
            //search for a child of this node
            //so that child.content.equals(nextPathElement)
            Optional<Tree<T>> search = curNode.children.stream().
                    filter((Tree<T> child) -> (equal(child.content, t))).
                    findFirst();

            if (search.isPresent()) {
                //node found that matches the path
                curNode = search.get();
            }
            else {
                //pathelement doesn't exists
                //needs to be created and inserted
                Tree<T> newNode = new Tree<>(clazz, t);

                curNode._add(newNode);

                curNode = newNode;
            }
        }

        //parentnode of the node to insert
        //is curNode
        if (children.stream().
                filter((Tree<T> child) -> (child.content.equals(node.content))).
                findFirst().isPresent())
            throw new TreeBuildException("Node already exists");
        //a node with this content already is content of this tree

        //insert new node
        curNode._add(node);

        //notify listeners about modelchange
        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.ADD, this);
    }

    /**
     * returns the content of this node
     * if it is set
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return the content of this node
     * @throws java.util.NoSuchElementException if the content is not set
     */
    public T getContent() {
        return runOpExceptionSuppressed(this::_getContent, AccessTask.TaskOpType.READ);
    }

    /**
     * sets the content for this node
     * and sets the contentSetFlag
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param nt the new content
     */
    public void setContent(T nt) {
        runOpExceptionSuppressed(() -> {
            _setContent(nt);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * returns the content of this node
     * if it is set
     *
     * @return the content of this node
     * @throws java.util.NoSuchElementException if the content is not set
     */
    protected T _getContent() {
        if (!contentSet)
            throw new NoSuchElementException("no content set");

        return content;
    }

    public Collection<Tree<T>> getChildren() {
        return runOpExceptionSuppressed(this::_getChildren, AccessTask.TaskOpType.READ);
    }

    /**
     * creates a read-only copy of children to grant
     * access to the treestrucutre without the danger of
     * damaging the structural integrity of this tree
     *
     * @return a read-only copy of children
     * @see java.util.Collections#unmodifiableCollection
     */
    protected Collection<Tree<T>> _getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    /**
     * public interface (see threadsafetynotes)
     *
     * @return true if the content is currently a valid value
     */
    public boolean contentSet() {
        return runOpExceptionSuppressed(this::_contentSet, AccessTask.TaskOpType.READ);
    }

    /**
     * @return true if the content is currently a valid value
     */
    protected boolean _contentSet() {
        return contentSet;
    }

    /**
     * sets the content for this node
     * and sets the contentSetFlag
     *
     * @param nt the new content
     */
    protected void _setContent(T nt) {
        contentSet = true;

        content = nt;

        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.VALUE_UPDATED, this);
    }

    /**
     * removes all nodes from the tree
     * which have a content equal to childVal
     * and are children of this node
     * <p>
     * afterwards the treestructure is updated
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param childVal value to remove
     */

    public void remove(T childVal) {
        runOpExceptionSuppressed(() -> {
            _remove(childVal);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * removes all nodes from the tree
     * which have a content equal to childVal
     * and are children of this node
     * <p>
     * afterwards the treestructure is updated
     *
     * @param childVal value to remove
     */
    protected void _remove(T childVal) {
        Stream<Tree<T>> matchingChildStream = children.stream().
                filter((Tree<T> child) -> (equal(child.content, childVal)));

        class CheckAllEmpty
                extends GoThroughManager<Tree<T>> {
            private boolean noSet = true;

            @Override
            public void accept(Tree<T> tTree) {
                noSet &= !tTree.contentSet;
            }

            @Override
            public boolean breakOff() {
                return !noSet;
            }
        }

        matchingChildStream.forEach(n -> {
            n.contentSet = false;

            CheckAllEmpty c = new CheckAllEmpty();
            goThroughTree(n, c);

            if (c.noSet) {
                children.remove(n);

                n.parent = null;

                globalVar.removeTree(n);
            }
        });

        if (matchingChildStream.findAny().isPresent())
            globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.REMOVE, this);
    }

    /**
     * removes the given node from te tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param child the node to remove
     * @return true if successful
     */
    public boolean removeSubtree(Tree<T> child) {
        return runOpExceptionSuppressed(() -> _removeSubtree(child), AccessTask.TaskOpType.WRITE);
    }

    /**
     * removes the given node from te tree
     *
     * @param child the node to remove
     * @return true if successful
     */
    protected boolean _removeSubtree(Tree<T> child) {
        boolean temp = children.remove(child);

        if (temp) {
            child.parent = null;

            globalVar.removeTree(child);

            globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.REMOVE, this);
        }

        return temp;
    }

    /////////////////////////////////////////////////////
    //treeop
    /////////////////////////////////////////////////////

    /**
     * clears this subtree
     * <p>
     * during this operation, all known relationships between the nodes
     * of this subtree will be released
     * <p>
     * public interface (see threadsafetynotes)
     */
    public void clear() {
        runOpExceptionSuppressed(() -> {
            _clear();
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * clears this subtree
     * <p>
     * during this operation, all known relationships between the nodes
     * of this subtree will be released
     */
    protected void _clear() {
        ArrayList<Tree<T>> nodes = new ArrayList<>();


        nodes.addAll(children);
        while (!nodes.isEmpty()) {
            Tree<T> temp = nodes.get(0);

            nodes.addAll(temp.children);

            temp.getParent()._removeSubtree(temp);
            temp.parent = null;
        }

        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.CLEAR, this);
    }

    /**
     * merges this tree with the given tree, starting from the root
     * of toMerge and the node which calls merge
     * (only copies of nodes of toMerge will be inserted into this tree,
     * to maintain the stability of toMerge)
     * <p>
     * rules:
     * <ol>
     * <li>
     * the content of toMerge MUST be equal with the content of the callernode (this)
     * </li>
     * <li>
     * this node MUST NOT be a subtree of the tree to which the callernode belongs
     * </li>
     * </ol>
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param toMerge the tree that is merged with this tree
     * @throws TreeBuildException if any of the above rules are broken.
     */
    public void merge(Tree<T> toMerge)
            throws TreeBuildException {
        runOp(() -> {
            _merge(toMerge);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * merges this tree with the given tree, starting from the root
     * of toMerge and the node which calls merge
     * (only copies of nodes of toMerge will be inserted into this tree,
     * to maintain the stability of toMerge)
     * <p>
     * rules:
     * <ol>
     * <li>
     * the content of toMerge MUST be equal with the content of the callernode (this)
     * </li>
     * <li>
     * this node MUST NOT be a subtree of the tree to which the callernode belongs
     * </li>
     * </ol>
     *
     * @param toMerge the tree that is merged with this tree
     * @throws TreeBuildException if any of the above rules are broken.
     */
    protected void _merge(Tree<T> toMerge)
            throws TreeBuildException {
        //no exception is thrown, if one of these rules applies:
        //1. no content set in both nodes
        //2. both nodes contain the same object
        //3. both nodes have equivalent content
        if (!((!toMerge.contentSet && !contentSet) ||
                toMerge.content == content ||
                toMerge.content.equals(content)))
            throw new TreeBuildException("Invalid merge-operation, toMerge doesn't fit this tree");

        ArrayList<Iterator<Tree<T>>> toMergeIterStack = new ArrayList<>();
        toMergeIterStack.add(toMerge.children.iterator());

        ArrayList<Collection<Tree<T>>> internalCheckStack = new ArrayList<>();
        internalCheckStack.add(children);

        ArrayList<Tree<T>> internalParentStack = new ArrayList<>();
        internalParentStack.add(this);

        while (!toMergeIterStack.isEmpty()) {
            Iterator<Tree<T>> mergeIter = toMergeIterStack.get(0);
            if (!mergeIter.hasNext()) {
                toMergeIterStack.remove(0);
                internalCheckStack.remove(0);
                internalParentStack.remove(0);
            }
            else

            {
                Tree<T> nextMerge = mergeIter.next();

                Optional<Tree<T>> equalFound = internalCheckStack.get(0).stream()
                        .filter(t -> equal(t.content, nextMerge.content)).findAny();

                if (equalFound.isPresent()) {
                    toMergeIterStack.add(0, nextMerge.children.iterator());
                    internalCheckStack.add(0, equalFound.get().children);
                    internalParentStack.add(0, equalFound.get());
                }
                else {
                    internalParentStack.get(0)._add(nextMerge._copy());
                }
            }
        }

        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.MERGE, this);
    }

    /**
     * this method evaluates the treeelement that
     * is the root of the tree of which this object
     * is an element
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return the root of the tree
     */
    public Tree<T> getRoot() {
        return runOpExceptionSuppressed(this::_getRoot, AccessTask.TaskOpType.READ);
    }

    /**
     * this method evaluates the treeelement that
     * is the root of the tree of which this object
     * is an element
     *
     * @return the root of the tree
     */
    protected Tree<T> _getRoot() {
        Tree<T> temp = this;

        while (temp.parent != null)
            temp = temp.parent;

        return temp;
    }

    public T[] getPath() {
        return runOpExceptionSuppressed(this::_getPath, AccessTask.TaskOpType.READ);
    }

    /**
     * returns a list of all objects from the root (excluded - see structural rules)
     * to this node (included) ordered from lowest to highest level
     *
     * @return the path to this node
     */
    protected T[] _getPath() {
        T[] result = (T[]) Array.newInstance(clazz, _getLevel());

        Tree<T> node = this;
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = node.content;

            node = node.parent;
        }

        return result;
    }

    /**
     * this method evaluates the treeelement that
     * is the root of the tree of which this object
     * is an element
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return the root of the tree
     */
    public boolean hasPath(T[] path) {
        return runOpExceptionSuppressed(() -> _hasPath(path), AccessTask.TaskOpType.READ);
    }

    /**
     * checks whether this path is content of the
     * tree
     * <p>
     * the root is not included in this path
     *
     * @param path a path through the tree
     * @return true if the path is content of the tree
     */
    protected boolean _hasPath(T[] path) {
        Tree<T> node = this;

        for (T t : path) {
            Optional<Tree<T>> search = node.children.stream()
                    .filter((Tree<T> child) -> equal(child.content, t))
                    .findAny();

            if (search.isPresent())
                node = search.get();
            else
                return false;
        }

        return true;
    }

    /**
     * gets the node of the tree that is at the end of the specified path
     * the path always starts at the root, no matter from where in the tree
     * the method is called
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param path the path from the root to the node
     * @return the node at the end of the path or null, it the path isn't found
     */
    public Tree<T> getNodeForPath(T[] path) {
        return runOpExceptionSuppressed(() -> _getNodeForPath(path), AccessTask.TaskOpType.READ);
    }

    /**
     * gets the node of the tree that is at the end of the specified path
     * the path always starts at the root, no matter from where in the tree
     * the method is called
     *
     * @param path the path from the root to the node
     * @return the node at the end of the path or null, it the path isn't found
     */
    protected Tree<T> _getNodeForPath(T[] path)
            throws NoSuchElementException {
        Tree<T> curNode = this;

        for (T t : path) {
            Optional<Tree<T>> search = curNode.children.stream().
                    filter(c -> equal(c.content, t)).
                    findFirst();

            if (search.isPresent())
                curNode = search.get();
            else
                return null;
        }

        return curNode;
    }

    /**
     * the root has level = 0
     * any other treenode gets its level
     * by the length of the path from the root
     * to the node
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return the level of the current node in the tree
     */
    public int getLevel() {
        return runOpExceptionSuppressed(this::_getLevel, AccessTask.TaskOpType.READ);
    }

    /**
     * the root has level = 0
     * any other treenode gets its level
     * by the length of the path from the root
     * to the node
     *
     * @return the level of the current node in the tree
     */
    protected int _getLevel() {
        Tree<T> node = this;

        int result = 0;

        while (node.parent != null) {
            result++;

            node = node.parent;
        }

        return result;
    }

    /**
     * public interface (see threadsafetynotes)
     *
     * @return the level of the node with the highest level
     */
    public int getHeight() {
        return runOpExceptionSuppressed(this::_getHeight, AccessTask.TaskOpType.READ);
    }

    /**
     * @return the level of the node with the highest level
     */
    protected int _getHeight() {
        class DepthCounter
                extends GoThroughManager {
            int maxDepth = 0;

            @Override
            public void levelChanged(Integer i) {
                if (i > maxDepth)
                    maxDepth = i;
            }
        }

        DepthCounter depth = new DepthCounter();
        goThroughTree(getRoot(), depth);

        return depth.maxDepth;
    }

    /**
     * public interface (see threadsafetynotes)
     *
     * @return the total number of nodes in this tree
     */
    public int size() {
        return runOpExceptionSuppressed(this::_size, AccessTask.TaskOpType.READ);
    }

    /**
     * @return the total number of nodes in this tree
     */
    protected int _size() {
        //this consumer increments size
        //every time it consumes a node
        class SizeCounter
                extends GoThroughManager<Tree<T>> {
            int size = 0;

            public void accept(Tree<T> t) {
                ++size;
            }
        }

        SizeCounter counter = new SizeCounter();
        //run through the tree and count all nodes
        goThroughTree(getRoot(), counter);

        return counter.size;
    }

    /**
     * checks whether this node is
     * the root of a tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return true if the node is a root
     */
    public boolean isRoot() {
        return runOpExceptionSuppressed(this::_isRoot, AccessTask.TaskOpType.READ);
    }

    /**
     * checks whether this node is
     * the root of a tree
     *
     * @return true if the node is a root
     */
    protected boolean _isRoot() {
        return (parent == null);
    }

    /**
     * checks whether this node has any
     * children
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return <code>true</code> if the node has no children
     */
    public boolean isLeaf() {
        return runOpExceptionSuppressed(this::_isLeaf, AccessTask.TaskOpType.READ);
    }

    /**
     * checks whether this node has any
     * children
     *
     * @return <code>true</code> if the node has no children
     */
    protected boolean _isLeaf() {
        return children.isEmpty();
    }

    ///////////////////////////////////////////////////////////////////////
    // list ops
    ///////////////////////////////////////////////////////////////////////

    /**
     * public interface (see threadsafetynotes)
     *
     * @return the parent of this leaf
     */
    public Tree<T> getParent() {
        return runOpExceptionSuppressed(this::_getParent, AccessTask.TaskOpType.READ);
    }

    /**
     * @return the parent of this leaf
     */
    protected Tree<T> _getParent() {
        return parent;
    }

    /**
     * returns the root of the smallest common subtree
     * this tree contains both the node calling the method and
     * t and there cannot be found any smaller subtree that contains
     * both nodes
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param t the node for which a common subtree is searched
     * @return the smallest commmon subtree
     */
    public Tree<T> smallestCommonSubtree(Tree<T> t) {
        return runOpExceptionSuppressed(() -> _smallestCommonSubtree(t), AccessTask.TaskOpType.READ);
    }

    /**
     * returns the root of the smallest common subtree
     * this tree contains both the node calling the method and
     * t and there cannot be found any smaller subtree that contains
     * both nodes
     *
     * @param t the node for which a common subtree is searched
     * @return the smallest commmon subtree
     */
    protected Tree<T> _smallestCommonSubtree(Tree<T> t) {
        //TODO
        if (!getRoot().contains(t))
            throw new IllegalArgumentException("argument is no member of this tree");

        Iterator<Tree<T>> tIter = t.listParents().iterator();
        Iterator<Tree<T>> internalIter = t.listParents().iterator();

        Tree<T> temp = null;
        Tree<T> prev;

        while (tIter.hasNext() && internalIter.hasNext()) {
            prev = temp;

            temp = tIter.next();

            if (!temp.content.equals(internalIter.next().content))
                return prev;
        }

        return null;
    }

    /**
     * lists all parents of this node
     * down to the root
     * (includes this node)
     * <p>
     * order is from the root to this node
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a list of nodes which are part of the path from this node to the root
     */
    public List<Tree<T>> listParents() {
        return runOpExceptionSuppressed(this::_listParents, AccessTask.TaskOpType.READ);
    }

    /**
     * lists all parents of this node
     * down to the root
     * (includes this node)
     * <p>
     * order is from the root to this node
     *
     * @return a list of nodes which are part of the path from this node to the root
     */
    protected List<Tree<T>> _listParents() {
        List<Tree<T>> result = new ArrayList<>();

        Tree<T> temp = this;

        while (temp != null) {
            result.add(temp);
            temp = temp.getParent();
        }

        Collections.reverse(result);

        return result;
    }

    /**
     * lists all peers of this node
     * peers of this node have the following
     * attributes in common:
     * <ul>
     * <li>same parent</li>
     * <li>same level</li>
     * </ul>
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a collection of the peers of this list
     */
    public Collection<Tree<T>> listPeers() {
        return runOpExceptionSuppressed(this::_listPeers, AccessTask.TaskOpType.READ);
    }

    /**
     * lists all peers of this node
     * peers of this node have the following
     * attributes in common:
     * <ul>
     * <li>same parent</li>
     * <li>same level</li>
     * </ul>
     *
     * @return a collection of the peers of this list
     */
    protected Collection<Tree<T>> _listPeers() {
        if (getParent() == null) {
            try {
                Collection<Tree<T>> result = children.getClass().newInstance();
                result.add(this);
                return result;
            }
            catch (InstantiationException | IllegalAccessException e) {
                ArrayList<Tree<T>> result = new ArrayList<>();
                result.add(this);
                return result;
            }
        }

        return getParent().children;
    }

    /**
     * lists all nodes with content that either equals t,
     * or, if a comparator is provided, equals the provided comparator
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param t    the T for wich nodes with equal content are searched
     * @param comp an optional comparator to compare the nodecontents with t
     * @return a list of nodes with content equal to t
     */
    public Collection<Tree<T>> listMatching(T t, Comparator<T>... comp) {
        return runOpExceptionSuppressed(() -> _listMatching(t, comp), AccessTask.TaskOpType.READ);
    }

    /**
     * lists all nodes with content that either equals t,
     * or, if a comparator is provided, equals the provided comparator
     *
     * @param t    the T for wich nodes with equal content are searched
     * @param comp an optional comparator to compare the nodecontents with t
     * @return a list of nodes with content equal to t
     */
    protected Collection<Tree<T>> _listMatching(T t, Comparator<T>... comp) {
        Collection<Tree<T>> matches = new Stack<>();

        Comparator<T> c;

        //create new comparator, if not provided
        if (comp.length > 0)
            c = comp[0];
        else
            c = (o1, o2) -> {
                if (o1 == o2)
                    return 0;
                if (o1 == null)
                    return 1;
                else if (o2 == null)
                    return -1;
                else if (o1.equals(o2))
                    return 0;
                else
                    return -1;
            };

        class MatchesSearch
                extends GoThroughManager<Tree<T>> {
            @Override
            public void accept(Tree<T> tTree) {
                if (c.compare(tTree.content, t) == 0)
                    matches.add(tTree);
            }
        }

        MatchesSearch search = new MatchesSearch();
        goThroughTree(this, search);

        return matches;
    }

    /**
     * lists the roots of all subtrees containing path
     * (the root must contain the first element of path)
     * if path is empty, this node will be returned as match
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param path the path that is searched
     * @return a list of nodes
     */
    public List<Tree<T>> listMatchingPaths(T[] path) {
        return runOpExceptionSuppressed(() -> _listMatchingPaths(path), AccessTask.TaskOpType.READ);
    }

    /**
     * lists the roots of all subtrees containing path
     * (the root must contain the first element of path)
     * if path is empty, this node will be returned as match
     *
     * @param path the path that is searched
     * @return a list of nodes
     */
    protected List<Tree<T>> _listMatchingPaths(T[] path) {
        if (path.length == 0) {
            List<Tree<T>> result = new ArrayList<>();

            result.add(this);

            return result;
        }

        List<Tree<T>> matches = new ArrayList<>();

        Collection<Tree<T>> startNodeMatch = listMatching(path[0]);

        startNodeMatch.forEach(t -> {
            Tree<T> helper = new Tree<>(clazz);
            helper.children.add(t);

            if (helper.hasPath(path))
                matches.add(t);
        });

        return matches;
    }

    /**
     * lists all children of this tree inorder
     * - from root to leafs
     * - the way the content is sorted on each level
     * depends on the collection the children are stored
     * in
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a list of the contents of this tree sorted inorder
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    public List<T> listContent() {
        return runOpExceptionSuppressed(this::_listContent, AccessTask.TaskOpType.READ);
    }

    /**
     * lists all children of this tree inorder
     * - from root to leafs
     * - the way the content is sorted on each level
     * depends on the collection the children are stored
     * in
     *
     * @return a list of the contents of this tree sorted inorder
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    protected List<T> _listContent() {
        class ListContent
                extends GoThroughManager<Tree<T>> {
            ArrayList<T> nodes = new ArrayList<>();

            public void accept(Tree<T> t) {
                if (t.contentSet)
                    nodes.add(t.content);
            }
        }

        ListContent list = new ListContent();
        goThroughTree(getRoot(), list);

        return list.nodes;
    }

    /**
     * lists all nodes of this tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a list of nodes
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    public List<Tree<T>> listNodes() {
        return runOpExceptionSuppressed(this::_listNodes, AccessTask.TaskOpType.READ);
    }

    /**
     * lists all nodes of this tree
     *
     * @return a list of nodes
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    protected List<Tree<T>> _listNodes() {
        class ListNodes
                extends GoThroughManager<Tree<T>> {
            ArrayList<Tree<T>> nodes = new ArrayList<>();

            @Override
            public void accept(Tree<T> t) {
                nodes.add(t);
            }
        }

        ListNodes list = new ListNodes();
        goThroughTree(this, list);

        return list.nodes;
    }

    /**
     * lists all leafs of this tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a list of leafs of this trees
     */
    public List<Tree<T>> listLeafs() {
        return runOpExceptionSuppressed(this::_listLeafs, AccessTask.TaskOpType.READ);
    }

    /**
     * lists all leafs of this tree
     *
     * @return a list of leafs of this trees
     */
    protected List<Tree<T>> _listLeafs() {
        class ListLeafs
                extends GoThroughManager<Tree<T>> {
            ArrayList<Tree<T>> found = new ArrayList<>();

            @Override
            public void accept(Tree<T> t) {
                if (t.children.isEmpty())
                    found.add(t);
            }
        }

        ListLeafs temp = new ListLeafs();
        goThroughTree(this, temp);

        return temp.found;
    }

    /**
     * this iterator for the current tree
     * runs through the tree inorder
     * - direction from root to leafs
     * - the way the content is sorted per level
     * depends on the collection used for storing
     * the children
     *
     * @return an iterator for this tree
     */
    @Override
    public Iterator<Tree<T>> iterator() {
        return new TreeIter(this);
    }

    /**
     * lists all paths in this tree
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a list of T[]
     */
    public List<T[]> listPaths() {
        return runOpExceptionSuppressed(this::_listPaths, AccessTask.TaskOpType.READ);
    }

    /**
     * lists all paths in this tree
     *
     * @return a list of T[]
     */
    protected List<T[]> _listPaths() {
        List<T[]> paths = new ArrayList<>();

        List<Tree<T>> leafs = _listLeafs();

        for (Tree<T> leaf : leafs)
            paths.add(leaf._getPath());

        return paths;
    }

    //////////////////////////////////////////////////
    // tree analysation helper
    //////////////////////////////////////////////////

    /**
     * makes this tree disjoint of t
     * (has no subtrees in common with t)
     * <p>
     * basically, this is the reverse of merge(t)
     * and could be used to undo a mergeoperation
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param arg the tree from which this tree should be disjoint
     */
    public void disjoint(Tree<T> arg) {
        runOpExceptionSuppressed(() -> {
            _disjoint(arg);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    /**
     * makes this tree disjoint of t
     * (has no subtrees in common with t)
     * <p>
     * basically, this is the reverse of merge(t)
     * and could be used to undo a mergeoperation
     *
     * @param arg the tree from which this tree should be disjoint
     */
    protected void _disjoint(Tree<T> arg) {
        //TODO
        /**
         * pseudocode:
         *
         * internalNode = first child of this node;
         * alienNode;
         *
         * while internalNode != null
         *      if internalNode has children
         *          alienNode = findMatchingAlienChild
         *
         *          if matching alienNode found
         *              internalNode = frst child of internalNode
         *
         *          else if peer of internalNode available
         *              internalNode = next peer of internalNode
         *
         *          else
         *              internalNode = parent of internalNode
         *      else
         *          alienNode = findMatchingAlienChild
         *
         *          if matching alienNode found AND peer of internalNode available
         *              internalNode = next peer of internalnode
         *          else
         *              internalNode = parent of internalNode
         */

        if (arg == null)
            throw new NullPointerException("invalid argument - null not allowed here");

        //root doesnt match / no children to remove are found
        if (!arg.content.equals(content) || children.isEmpty())
            return;

        Tree<T> internalNode = children.iterator().next();
        ArrayList<List<Tree<T>>> internalIterStack = new ArrayList<>();
        internalIterStack.add(convertToList(children));

        Tree<T> alienNode;
        Tree<T> alienNodeParent = arg;

        while (!internalIterStack.isEmpty()) {
            final Tree<T> finalHelper = internalNode;
            Optional<Tree<T>> searchMatchingAlien = alienNodeParent.children.stream().
                    filter(t -> (t.content == null && finalHelper.content == null)
                            || t.content.equals(finalHelper.content)).findAny();
            alienNode = (searchMatchingAlien.isPresent() ? searchMatchingAlien.get() : null);

            if (!internalNode.children.isEmpty()) {
                if (alienNode != null) {
                    internalNode = internalNode.children.iterator().next();
                    internalIterStack.add(convertToList(internalNode.children));
                }
                else if (internalIterStack.get(0).size() > 1) {
                    internalIterStack.remove(0);

                    internalNode = internalIterStack.get(0).get(0);
                }
                else {
                    internalNode = internalNode.parent;

                    internalIterStack.remove(0);
                }
            }
            else if (alienNode != null && internalIterStack.size() > 1) {
                internalIterStack.get(0).remove(0);

                internalNode = internalIterStack.get(0).get(0);
            }
            else
                internalNode = internalNode.parent;
        }

        globalVar.getNotifier().fireModelChanged(TreeModelChangedEvent.TYPE.REMOVE, this);
    }

    //////////////////////////////////////////////////
    //search
    //////////////////////////////////////////////////

    /**
     * goes through this subtree (with startNode as root) inorder
     * <p>
     * rules for running through the tree:
     * <ul>
     * <li>
     * 1. if the algorithm can go one level deeper
     * it will go one level deeper
     * </li>
     * <li>
     * 2. if the algorithm can't go any deeper
     * on it's current path, it will go on
     * with the next child of this node's parent
     * </li>
     * <li>
     * 3. if all nodes on a level with the same parent are
     * processed, the algorithm steps one level back
     * </li>
     * <li>
     * 4. if <code>mgr.breakOff()</code> returns true,
     * the algorithm will break off
     * </li>
     * </ul>
     *
     * @param startNode the root of the subtree to analyse
     * @param mgr       the manager defining the go through conditions
     * @see Tree.GoThroughManager
     */
    protected void goThroughTree(Tree<T> startNode, GoThroughManager<? extends Tree<T>> mgr) {
        //levelcounter
        int level = 0;

        //the current node used during analysis of the tree
        Tree<T> node;

        //a stack representing the path to the current subtree
        //of the tree which is processed
        ArrayList<Iterator<Tree<T>>> stack = new ArrayList<>();

        //set startnode
        ArrayList<Tree<T>> temp = new ArrayList<>();
        temp.add(startNode);

        //insert the root as startvalue
        stack.add(0, temp.iterator());

        //run through the tree as long as there are any nodes
        //remaining unconsumed
        while (!stack.isEmpty() && !mgr.breakOff()) {
            if (!stack.get(0).hasNext()) {
                //nothing to process in this subtree
                //step back and continue on the next level
                stack.remove(0);

                level -= 1;

                mgr.levelChanged(level);
            }
            else {
                //still nodes remaining to process in this subtree
                node = stack.get(0).next();

                //process current node
                mgr.nextNode(node);

                //go one level deeper, if any children
                //are available
                if (!node.isLeaf()) {
                    stack.add(0, node.children.iterator());

                    level += 1;

                    mgr.levelChanged(level);
                }
            }
        }
    }

    /**
     * searches for the first node which has a
     * content matching t found by
     * Tree.goThroughTree(...)
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param t the value to search
     * @return the first node with matching content
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    public Tree<T> searchAnyNodeFor(T t) {
        return runOpExceptionSuppressed(() -> _searchAnyNodeFor(t), AccessTask.TaskOpType.READ);
    }

    /**
     * searches for the first node which has a
     * content matching t found by
     * Tree.goThroughTree(...)
     *
     * @param t the value to search
     * @return the first node with matching content
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    protected Tree<T> _searchAnyNodeFor(T t) {
        return _searchAnyNodeFor((T a, T b) -> (equal(a, b) ? 0 : 1), t);
    }

    /**
     * searches for the first component in the
     * tree matching the comparator
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param comp with this comparator, nodes are checked
     *             for fullfilling the conditions
     * @param t    the value that is searched
     * @return the first node matching the conditions
     */
    public Tree<T> searchAnyNodeFor(Comparator<T> comp, T t) {
        return runOpExceptionSuppressed(() -> _searchAnyNodeFor(comp, t), AccessTask.TaskOpType.READ);
    }

    /**
     * searches for the first component in the
     * tree matching the comparator
     *
     * @param compare with this comparator, nodes are checked
     *                for fullfilling the conditions
     * @param t       the value that is searched
     * @return the first node matching the conditions
     */
    protected Tree<T> _searchAnyNodeFor(Comparator<T> compare, T t) {
        class SearchMgr
                extends GoThroughManager<Tree<T>> {
            Tree<T> found;

            public void accept(Tree<T> tree) {
                if (!tree.contentSet)
                    return;

                if ((compare.compare(tree.content, t) == 0))
                    found = tree;
            }

            public boolean breakOff() {
                return (found != null);
            }
        }

        SearchMgr mgr = new SearchMgr();
        goThroughTree(this, mgr);

        return mgr.found;
    }

    /**
     * lists all nodes found in the tree
     * with content equal t
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param t the value to search
     * @return a list of nodes
     */
    public ArrayList<Tree<T>> searchAllNodesFor(T t) {
        return runOpExceptionSuppressed(() -> _searchAllNodesFor(t), AccessTask.TaskOpType.READ);
    }

    /**
     * lists all nodes found in the tree
     * with content equal t
     *
     * @param t the value to search
     * @return a list of nodes
     */
    protected ArrayList<Tree<T>> _searchAllNodesFor(T t) {
        return _searchAllNodesFor((T a, T b) -> (equal(a, b) ? 0 : 1), t);
    }

    /**
     * lists all nodes which are equal to t
     * comparisons are done via <code>compare.compare</code>
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param compare with this comparator, nodes are checked
     *                for fullfilling the conditions
     * @param t       the value that is searched
     * @return a list of treenodes matching conditions
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    public ArrayList<Tree<T>> searchAllNodesFor(Comparator<T> compare, T t) {
        return runOpExceptionSuppressed(() -> _searchAllNodesFor(compare, t), AccessTask.TaskOpType.READ);
    }

    /**
     * lists all nodes which are equal to t
     * comparisons are done via <code>compare.compare</code>
     *
     * @param compare with this comparator, nodes are checked
     *                for fullfilling the conditions
     * @param t       the value that is searched
     * @return a list of treenodes matching conditions
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     */
    protected ArrayList<Tree<T>> _searchAllNodesFor(Comparator<T> compare, T t) {
        class SearchMgr
                extends GoThroughManager<Tree<T>> {
            ArrayList<Tree<T>> found = new ArrayList<>();

            public void accept(Tree<T> tree) {
                if (compare.compare(tree.content, t) == 0)
                    found.add(tree);
            }
        }

        SearchMgr mgr = new SearchMgr();
        goThroughTree(this, mgr);

        return mgr.found;
    }

    /**
     * this algorithm searches in the tree
     * for the first node from which a path
     * equal to pathpiece starts
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param pathPiece the piece of a path you're searching
     * @return the first node fitting this path
     */
    public Tree<T> searchForPathPiece(T[] pathPiece) {
        return runOpExceptionSuppressed(() -> _searchForPathPiece(pathPiece), AccessTask.TaskOpType.READ);
    }

    //////////////////////////////////////////////////
    //toString
    //////////////////////////////////////////////////

    /**
     * this algorithm searches in the tree
     * for the first node from which a path
     * equal to pathpiece starts
     *
     * @param pathPiece the piece of a path you're searching
     * @return the first node fitting this path
     */
    protected Tree<T> _searchForPathPiece(T[] pathPiece) {
        //check for valid path
        if (pathPiece.length == 0)
            throw new IllegalArgumentException("no valid path (minimum length = 1)");

        //the current node used during analysis of the tree
        Tree<T> node;

        //a stack representing the path to the current subtree
        //of the tree which is processed
        ArrayList<Iterator<Tree<T>>> stack = new ArrayList<>();

        //set startnode
        ArrayList<Tree<T>> temp = new ArrayList<>();
        temp.add(this);

        //insert the root as startvalue
        stack.add(0, temp.iterator());

        //run through the tree as long as there are any nodes
        //remaining unconsumed
        while (!stack.isEmpty()) {
            if (!stack.get(0).hasNext()) {
                //nothing to process in this subtree
                //step back and continue on the next level
                stack.remove(0);
            }
            else {
                //still nodes remaining to process in this subtree
                node = stack.get(0).next();

                if (node._hasPath(pathPiece))
                    return node;

                //process current node

                //go one level deeper, if any children
                //are available
                if (!node._isLeaf()) {
                    stack.add(0, node.children.iterator());
                }
            }
        }

        return null;
    }

    /**
     * creates a Stringrepresentation of the subtree
     * with the current node as child
     * <p>
     * Output:
     * <pre>
     * root
     *      level1nodeA
     *          level2nodeA
     *      level1nodeB
     *          level2nodeB
     *          level2nodeC
     * </pre>
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a string representation of this tree
     * @see Object#toString()
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     * @see Tree.GoThroughManager
     */
    @Override
    public String toString() {
        return runOpExceptionSuppressed(this::_toString, AccessTask.TaskOpType.READ);
    }

    /**
     * creates a stringrepresentation of the subtree
     * with the current node as child
     * <p>
     * Output:
     * <pre>
     * root
     *      level1nodeA
     *          level2nodeA
     *      level1nodeB
     *          level2nodeB
     *          level2nodeC
     * </pre>
     *
     * @return a string representation of this tree
     * @see Object#toString()
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     * @see Tree.GoThroughManager
     */
    protected String _toString() {
        class StringBuild
                extends GoThroughManager<Tree<T>> {
            StringBuilder builder = new StringBuilder();

            int level = 0;

            public void accept(Tree<T> t) {
                builder.append('\n');
                builder.append(createInsertion());
                builder.append(t.getStringRep());
            }

            private char[] createInsertion() {
                char[] result = new char[level];

                for (int i = 0; i < level; i++)
                    result[i] = '\t';

                return result;
            }

            public void levelChanged(Integer newLevel) {
                level = newLevel;
            }
        }

        StringBuild build = new StringBuild();
        goThroughTree(this, build);

        return build.builder.substring(1);
    }

    ///////////////////////////////////////////////////////////////
    //compare
    ///////////////////////////////////////////////////////////////

    /**
     * Creates a String as Representation of the node
     * used in @see dove.util.treelib.Tree#toString()
     * override to change result of the toString method
     *
     * @return representation of the node
     */
    protected String getStringRep() {
        if (!contentSet) {
            return "";
        }
        else if (content == null) {
            return "null";
        }
        else {
            return content.toString();
        }
    }

    /**
     * checks whether o is equal to <code>this</code>
     * returns true <code>if</code>:
     * - this == o
     * - o is an instance of <code>Tree<T></code> and
     * the lists of children are equal aswell
     *
     * @param o compare to this object
     * @return true if o fullfills one of the conditions
     * listed above
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Tree))
            return false;//wrong type

        Tree tree = (Tree) o;

        return
                (tree.content.equals(((Tree) o).content) &&
                        children.equals(((Tree) o).children));
    }

    /**
     * compares this treenode to another node
     * returns:
     * - 0 <code>if(this.equals(tree))</code>
     * - content.compareTo(tree.content) <code>if(content instanceof Comparable)</code>
     * - getLevel() - tree.getLevel() <code>if(!(content instanceof Comparable)</code>
     *
     * @param tree the other node
     * @return the comparator-value
     * @see Comparable
     */
    @Override
    public int compareTo(Tree<T> tree) {
        if (content instanceof Comparable) {
            //contents can be compared
            return ((Comparable<T>) content).compareTo(tree.content);
        }
        else {
            if (equals(tree))
                //equal node
                return 0;

            //content can't be compared --> compare level
            return getLevel() - tree.getLevel();
        }
    }

    /////////////////////////////////////////////////////////////////
    //clone
    /////////////////////////////////////////////////////////////////

    /**
     * the hashcode of every node is created from
     * the content-hash XOR children-hash
     *
     * @return the hashcode for this object
     */
    @Override
    public int hashCode() {
        return (content.hashCode() ^ children.hashCode());
    }

    /**
     * creates a shallow copy of this tree
     * since cloning of the content is not allowed
     * <p>
     * however any structural operations (not concerning the content)
     * will not affect any clones and reverse
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a shallow copy of this treeelement
     */
    public Tree<T> clone() {
        return runOpExceptionSuppressed(() ->
        {
            try {
                return _clone();
            }
            catch (CloneNotSupportedException ignored) {
                return null;
            }
        }, AccessTask.TaskOpType.READ);
    }

    /**
     * creates a shallow copy of this tree
     * since cloning of the content is not allowed
     * <p>
     * however any structural operations (not concerning the content)
     * will not affect any clones and reverse
     *
     * @return a shallow copy of this treeelement
     */
    protected Tree<T> _clone()
            throws CloneNotSupportedException {
        class CloneMgr
                extends GoThroughManager<Tree<T>> {
            public Tree<T> currentNode;

            private ArrayList<Tree<T>> parentStack = new ArrayList<>();

            private int level = 1;

            @Override
            public void accept(Tree<T> tTree) {
                Tree<T> clone = tTree.nodeCopy();

                if (!parentStack.isEmpty())
                    try {
                        parentStack.get(0)._add(clone);
                    }
                    catch (TreeBuildException ignored) {
                    }

                currentNode = clone;
            }

            @Override
            public void levelChanged(Integer integer) {
                if (level < integer)
                    parentStack.add(0, currentNode);
                else
                    parentStack.remove(0);
            }
        }

        CloneMgr cloneMgr = new CloneMgr();
        goThroughTree(this, cloneMgr);

        return cloneMgr.currentNode._getRoot();
    }

    /**
     * creates a copy of the current tree
     * <p>
     * all nodes are newly created, contentobjects will be transferred
     * to the new tree (no copy)
     * <p>
     * the copy only contains this subtree, the parent
     * of the copy of this node is always null
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @return a copy of this tree
     */
    public Tree<T> copy() {
        return runOpExceptionSuppressed(this::_copy, AccessTask.TaskOpType.READ);
    }

    /**
     * creates a copy of the current tree
     * <p>
     * all nodes are newly created, contentobjects will be transferred
     * to the new tree (no copy)
     * <p>
     * the copy only contains this subtree, the parent
     * of the copy of this node is always null
     *
     * @return a copy of this tree
     */
    protected Tree<T> _copy() {
        class CopyMgr
                extends GoThroughManager<Tree<T>> {
            private Tree<T> result = new Tree<>(null);

            private Tree<T> currentParent = result;

            private Tree<T> lastAdded;

            private int currentLevel = 0;

            @Override
            public void accept(Tree<T> tTree) {
                try {
                    lastAdded = tTree.nodeCopy();

                    currentParent._add(lastAdded);
                }
                catch (TreeBuildException e) {
                    //never thrown
                }
            }

            @Override
            public void levelChanged(Integer integer) {
                if (currentLevel < integer) {
                    currentParent = lastAdded;
                }
                else {
                    currentParent = currentParent._getParent();
                }

                currentLevel = integer;
            }
        }

        CopyMgr mgr = new CopyMgr();

        goThroughTree(this, mgr);

        //retrieve last node from the mgr
        //remove the temporary parent created by the copymanager
        Tree<T> result = mgr.result.children.iterator().next();
        result.parent = null;

        return result;
    }

    /**
     * generate a new tree with the same structure as the given tree,
     * but all nodes replaced by their respective pendants specified by f
     *
     * @param f     the function for transforming content
     * @param clazz the clazz of the new type of content
     * @param <V>   the type of the new content
     * @return a transformed copy of this tree
     * @throws TreeBuildException
     */
    public <V> Tree<V> transform(Function<T, V> f, Class<V> clazz)
            throws TreeBuildException {
        return runOp(() -> _transform(f, clazz), AccessTask.TaskOpType.READ);
    }

    /**
     * generate a new tree with the same structure as the given tree,
     * but all nodes replaced by their respective pendants specified by f
     *
     * @param f     the function for transforming content
     * @param clazz the clazz of the new type of content
     * @param <V>   the type of the new content
     * @return a transformed copy of this tree
     * @throws TreeBuildException
     */
    protected <V> Tree<V> _transform(Function<T, V> f, Class<V> clazz)
            throws TreeBuildException {
        Tree<V> result = new Tree<>(clazz, f.apply(content));

        List<Iterator<Tree<T>>> internalIter = new ArrayList<>();
        internalIter.add(children.iterator());
        List<Tree<V>> externalStack = new ArrayList<>();
        externalStack.add(result);

        while (!internalIter.isEmpty()) {
            Iterator<Tree<T>> iter = internalIter.get(0);

            if (iter.hasNext()) {
                Tree<T> t = iter.next();

                Tree<V> next = new Tree<>(clazz, f.apply(t.content));
                externalStack.get(0).add(next);

                internalIter.add(0, t.children.iterator());
                externalStack.add(0, next);
            } else {
                internalIter.remove(0);
                externalStack.remove(0);
            }
        }

        return result;
    }

    //////////////////////////////////////////////////////////////
    //content check
    //////////////////////////////////////////////////////////////

    /**
     * creates a copy of this node
     * (only this node, NO CHILDREN INCLUDED)
     *
     * @return a copy of this node
     */
    protected Tree<T> nodeCopy() {
        return (contentSet ? new Tree(clazz, content) : new Tree(clazz));
    }

    /**
     * checks whether this node has a child containing
     * the value specified by t
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param t the value to search
     * @return true if a child holds t as content
     */
    public boolean hasChild(T t) {
        return runOpExceptionSuppressed(() -> _hasChild(t), AccessTask.TaskOpType.READ);
    }

    /**
     * checks whether this node has a child containing
     * the value specified by t
     *
     * @param t the value to search
     * @return true if a child holds t as content
     */
    protected boolean _hasChild(T t) {
        return (children.stream().
                anyMatch((Tree<T> child) -> (
                                child.content.equals(t) && child.contentSet)
                ));
    }

    /**
     * checks whether alientree is contained inside this tree
     * the rules for this check are:
     * <p>
     * the tree may contain other elements aswell, aslong
     * as the structure of the tree is part of this tree.
     * <p>
     * the roots must have equal content.
     * <p>
     * public interface (see threadsafetynotes)
     *
     * @param subTree the subtree that is searched
     * @return true if subTree is contained in the tree with this node as root
     */
    public boolean contains(Tree subTree) {
        return runOpExceptionSuppressed(() -> _contains(subTree), AccessTask.TaskOpType.READ);
    }

    ///////////////////////////////////////////////////////////////
    // utility methods
    ///////////////////////////////////////////////////////////////

    /**
     * checks whether alientree is contained inside this tree
     * the rules for this check are:
     * <p>
     * the tree may contain other elements aswell, aslong
     * as the structure of the tree is part of this tree.
     * <p>
     * the roots must have equal content.
     *
     * @param subTree the subtree that is searched
     * @return true if subTree is contained in the tree with this node as root
     */
    protected boolean _contains(Tree subTree) {
        //shows whether the content of this tree is still equal to the one
        //contained in subTree
        //updated in every step
        boolean contentValid = (content.equals(subTree.content));

        ArrayList<Collection<Tree<T>>> internalIterStack = new ArrayList<>();
        ArrayList<Iterator<Tree<Object>>> alienIterStack = new ArrayList<>();

        ArrayList<Tree<T>> internalHelper = new ArrayList<>();
        internalHelper.add(this);
        internalIterStack.add(internalHelper);

        ArrayList<Tree<Object>> alienHelper = new ArrayList<>();
        alienHelper.add(subTree);
        alienIterStack.add(alienHelper.iterator());

        while (contentValid && !alienIterStack.isEmpty()) {
            //go one level up if no more nodes are available for
            //this subtree of subtree
            if (!alienIterStack.get(0).hasNext()) {
                alienIterStack.remove(0);
                internalIterStack.remove(0);
            }

            //get next node to check of the current subtree of subtree
            Tree<Object> alien = alienIterStack.get(0).next();

            //search for a node with equal content to alien
            Collection<Tree<T>> c = internalIterStack.get(0);
            Optional<Tree<T>> opt = c.stream().filter(t -> equal(t.content, alien.content)).findAny();

            contentValid = opt.isPresent();

            if (!alien.children.isEmpty() && contentValid) {
                alienIterStack.add(0, alien.children.iterator());
                internalIterStack.add(0, opt.get().children);
            }
        }

        return contentValid;
    }

    /**
     * @return the globalvarhelper for this tree
     */
    protected TreeGlobalVarHelper getGlobalVar() {
        return globalVar;
    }

    ///////////////////////////////////////////////////////////////
    // global var methods
    //
    // provides operations related to the global objects
    //correlated to this tree
    ///////////////////////////////////////////////////////////////

    /**
     * enables threadsafety for this tree
     */
    public void enableThreadSafety() {
        globalVar.enableThreadSafety();
    }

    ///////////////////////////////////////////////////////////////
    // Helperclasses
    ///////////////////////////////////////////////////////////////

    /**
     * disables threadafety for this tree
     */
    public void disableThreadSafety() {
        globalVar.disableThreadSafety();
    }

    /////////////////////////////////////////////////////////////////////////////
    // internal id helper
    /////////////////////////////////////////////////////////////////////////////

    /**
     * @return true if this tree is currently madde threadsafe by a treescheduler
     */
    public boolean isThreadSafe() {
        return globalVar.isThreadSafe();
    }

    //////////////////////////////////////////////////////////////////////////////////
    // thread safety
    //////////////////////////////////////////////////////////////////////////////////

    /**
     * runs an operation with the given parameters and
     * checks for exceptions/returns the result
     *
     * @param op   the task to run
     * @param type the type of task that is runned (read or write)
     * @param <R>  the typeparameter of the resultvalue
     * @return the result of op (instance of R)
     * @throws TreeBuildException if any treebuildexception occures while executing this task
     */
    protected <R> R runOp(AccessOp<R> op, AccessTask.TaskOpType type)
            throws TreeBuildException {
        try {
            if (globalVar.threadSafe)
                return globalVar.treeScheduler.invokeAndWait(op, type, this, DEFAULT_WRAPPER).getTask().finish();
            else
                return op.doOp();
        }
        catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else if (e instanceof TreeBuildException)
                throw (TreeBuildException) e;
            else
                e.printStackTrace();
        }

        return null;
    }

    /**
     * this method is called specifically, when
     * no exceptions are thrown in op
     *
     * @param op   the task to run
     * @param type the type of task that is runned (read or write)
     * @param <R>  the typeparameter of the resultvalue
     * @return the result of op (instance of R)
     */
    protected <R> R runOpExceptionSuppressed(AccessOp<R> op, AccessTask.TaskOpType type) {
        try {
            return runOp(op, type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addModelChangedListener(TreeModelChangeListener l) {
        globalVar.getNotifier().addModelChangedListener(l);
    }

    public void removeModelChangedListener(TreeModelChangeListener l) {
        globalVar.getNotifier().removeModelChangedListener(l);
    }

    /**
     * applys a ID, which is unique inside of this tree,
     * to every node inside the tree
     * <p>
     * the InternalIDHelper for a tree
     * is always held by the treeroot
     * <p>
     * if a subtree is newly added to a tree, all IDs have
     * to be changed by the InternalIDHelper of the tree
     * which holds the subtree
     * <p>
     * if a subtree is removed from a tree,
     * a new InternalIDHelper will be applied
     * to this Tree and all IDs will be updated
     */
    private static class InternalIDHelper {
        private static final int NO_ID_SET              = -1;
        /**
         * the next InternalIDHelperID that can be given
         * to an InternalIDHelper
         */
        private static       int nextInternalIDHelperID = 0;
        /**
         * the ID of this InternalIDHelperID
         * <p>
         * the pair: id + internalIDHelper.internalIDHelperID
         * is unique for every node
         */
        private int internalIDHelperID;
        /**
         * holds the next free ID, which can be applied to a
         * new node
         * <p>
         * if freedIDStack holds any freed IDs from subtrees, which
         * have been removed, these IDs will be used first
         */
        private int            nextID       = 0;
        /**
         * holds all IDs of nodes which have been removed
         * from this tree, and can therefore be reused
         * <p>
         * these IDs will always be used first, before creating a new ID
         */
        private Stack<Integer> freedIDStack = new Stack<>();

        /**
         * creates an new InternalIDHelper
         * and gives it a new ID
         */
        private InternalIDHelper() {
            internalIDHelperID = nextInternalIDHelperID++;
        }

        /**
         * called if a new subtree is added
         * <p>
         * applys a new ID to ever node of the
         * newly added subtree
         *
         * @param tree the added subtree
         */
        private void includeTree(Tree tree) {
            //apply a new ID to each node in tree
            tree.listNodes().forEach(t -> giveID((Tree) t));
        }

        /**
         * called if a subtree is removed
         * <p>
         * saves all ID which were held by
         * nodes of this subtree in the freedIDStack for
         * later use
         *
         * @param tree the removed subtree
         */
        private void excludeTree(Tree tree) {
            //save all IDs held by nodes of tree as free
            tree.listNodes().forEach(t -> removeID((Tree) t));
        }

        /**
         * gives a free ID to tree
         *
         * @param tree a new tree without ID
         */
        private void giveID(Tree tree) {
            if (freedIDStack.isEmpty())
                tree.id = nextID++;
            else
                tree.id = freedIDStack.pop();
        }

        /**
         * frees the ID held by this tree
         *
         * @param tree a node that has to be removed
         */
        private void removeID(Tree tree) {
            freedIDStack.push(tree.id);

            tree.id = NO_ID_SET;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // treeglobal varhelper
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * controlls all objects that
     * are used globally in this tree
     */
    protected static class TreeGlobalVarHelper {
        /**
         * true, if this tree is made threadsafe by the treescheduler
         * by default, threadsafety is disabled for all trees,
         * due to its resource consumption
         */
        private boolean threadSafe = false;

        /**
         * the treescheduler that handles all
         * reading and writing operations of this tree
         */
        private AccessScheduler treeScheduler;

        /**
         * the internal ID Helper of this object
         */
        private InternalIDHelper idHelper;

        private ModelChangedNotifier notifier = new ModelChangedNotifier();

        /**
         * creates a new Tree Helper and enables all
         */
        public TreeGlobalVarHelper() {
            idHelper = new InternalIDHelper();
        }

        //////////////////////////////////////////////////////////////
        // global tree components
        //////////////////////////////////////////////////////////////

        /**
         * @return the treescheduler currently controlling all operations on this tree,
         * or null, if threadsafety is disabled
         */
        public AccessScheduler getTreeScheduler() {
            return treeScheduler;
        }

        /**
         * @return the internalIDHelper of this tree
         */
        public InternalIDHelper getIdHelper() {
            return idHelper;
        }

        public ModelChangedNotifier getNotifier() {
            return notifier;
        }

        //////////////////////////////////////////////////////////////
        // threadsafety
        //////////////////////////////////////////////////////////////

        /**
         * makes this tree threadsafe and starts a new TreeScheduler-instance for this tree
         */
        public void enableThreadSafety() {
            threadSafe = true;

            treeScheduler = new AccessScheduler();
        }

        /**
         * disables threadsafty for this tree
         * quits the treescheduler
         */
        public void disableThreadSafety() {
            threadSafe = false;

            treeScheduler.quitScheduler();

            treeScheduler = null;
        }

        /**
         * @return true, if this tree is threadsafe
         */
        public boolean isThreadSafe() {
            return threadSafe;
        }

        //////////////////////////////////////////////////////////////////
        // ids
        //////////////////////////////////////////////////////////////////

        /**
         * @return the id of this tree
         */
        public int getTreeID() {
            return idHelper.internalIDHelperID;
        }

        //////////////////////////////////////////////////////////////////
        // tree structural ops
        //////////////////////////////////////////////////////////////////

        /**
         * transfers all attributes correlated
         * to the globalvarhelper of tree to
         * this tree, and updates the ids of tree
         *
         * @param tree the new subtree that is now correlated to this globalvarhelper
         */
        public void addTree(Tree tree) {
            //the old globalvarhelper of tree
            TreeGlobalVarHelper transfer = tree.getGlobalVar();

            //update the globalvar helper of all nodes of tree
            for (Tree aTree : (Iterable<Tree>) tree) aTree.globalVar = this;

            //give every node of tree a new ID matching to the
            //idgiver of this globalvarhelper
            idHelper.includeTree(tree);

            if (transfer.isThreadSafe()) {
                AccessScheduler scheduler = transfer.getTreeScheduler();

                if (isThreadSafe()) {
                    //transfer all unstarted tasks of the treescheduler
                    //of tree to the scheduler correlated to this tree
                    treeScheduler.invokeNext(() ->
                            {
                                treeScheduler.importTasks(scheduler, t -> true);
                                return null;
                            }, AccessTask.TaskOpType.WRITE, scheduler,
                            NO_EXCEPTION_WRAPPER);
                }
                else {
                    //quit the scheduler correlated to this tree
                    treeScheduler.quitScheduler();
                }
            }
        }

        public void removeTree(Tree tree) {
            //the old treeglobalvar correlated to globalvar
            TreeGlobalVarHelper oldVar = tree.globalVar;

            //create a new globalvarhelper for the removed tree
            TreeGlobalVarHelper nVar = new TreeGlobalVarHelper();

            //transfer the tree from the old idHelper to the idHelper-instance
            //of nVar
            oldVar.idHelper.excludeTree(tree);
            nVar.idHelper.includeTree(tree);

            if (oldVar.isThreadSafe()) {
                //make the new removed tree threadsafe, if threadsafety is enabled for this tree
                nVar.enableThreadSafety();

                //the old scheduler, which also handled tree
                AccessScheduler scheduler = tree.globalVar.getTreeScheduler();

                //transfer all tasks started by nodes inside of tree
                scheduler.invokeNext(() ->
                        {
                            scheduler.exportTasks(nVar.getTreeScheduler(),
                                    t -> (((Tree) t.getCaller()).globalVar.getIdHelper().internalIDHelperID == nVar.idHelper.internalIDHelperID));
                            return null;
                        }, AccessTask.TaskOpType.WRITE, scheduler,
                        NO_EXCEPTION_WRAPPER);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    // event fire
    /////////////////////////////////////////////////////////////////////////////////

    protected static class ModelChangedNotifier {
        private ArrayList<TreeModelChangeListener> modelChangeListeners = new ArrayList<>();

        /**
         * adds a new listener to the tree
         * if any changes to the interpreter happen (insert/remove)
         * the listener will be updated
         *
         * @param updater the new listener
         */
        public synchronized void addModelChangedListener(TreeModelChangeListener updater) {
            modelChangeListeners.add(updater);
        }

        /**
         * removes the listener
         * this listener will no longer be updated if any changes
         * occure on the tree
         *
         * @param updater the listener to remove
         */
        public synchronized void removeModelChangedListener(TreeModelChangeListener updater) {
            modelChangeListeners.remove(updater);
        }

        /**
         * notifys every listener, that the interpreter
         * has been modified (node inserted/removed)
         *
         * @param type the type of changed performed
         * @see dove.util.treelib.TreeModelChangedEvent.TYPE
         */
        protected synchronized void fireModelChanged(TreeModelChangedEvent.TYPE type, Tree source) {
            modelChangeListeners.forEach(l -> l.modelChanged(new TreeModelChangedEvent(source, type)));
        }
    }

    /**
     * defines all operations needed during
     *
     * @see Tree#goThroughTree(Tree, Tree.GoThroughManager)
     * to define the way, the algorithm runs through the tree
     * <p>
     * this class can be overrided, to analyse the tree
     * (compare getHeight() or listNodes())
     */
    protected class GoThroughManager<N extends Tree<T>> {
        /**
         * workaround to make accept available for trees
         * (issues with generics)
         *
         * @param tree handed on to accept
         */
        public final void nextNode(Tree<T> tree) {
            try {
                accept((N) tree);
            }
            catch (ClassCastException e) {
                throw new IllegalArgumentException(tree.getClass().getName() + " cannot be processed (invalid class)");
            }
        }

        /**
         * called for every node
         * this method can be overriden, in order to analyse the
         * tree
         *
         * @param tTree the node to be processed
         */
        public void accept(N tTree) {
            //stub override to use
            //called for each node
        }

        /**
         * this method is called if goThroughTree
         * goes one level deeper / higher whilst running through
         * the tree
         *
         * @param integer the level, the algorithm currently is running through
         */
        public void levelChanged(Integer integer) {
            //stub
            //override to use
            //called each time, the algorithm goes a level
            //deeper or higher
        }

        /**
         * this method is checked in every loop runthrough
         * of Tree.goThroughTree(...)
         * if this method returns true, the algorithm will break off
         *
         * @return true if the algorithm should break off
         */
        public boolean breakOff() {
            //stub
            //override to use
            //called in each looprunthrough to
            //check whether run on or stop
            return false;
        }
    }

    /**
     * this class provides an iterator
     * to iterate over the nodes of this tree inorder
     */
    private final class TreeIter
            implements Iterator<Tree<T>> {
        /**
         * this list provides the current position in the tree
         */
        private ArrayList<Iterator<Tree<T>>> internalIterStack;

        /**
         * the current node in the tree
         */
        private Tree<T> currentNode;

        /**
         * the root of the tree that is iterated
         */
        private Tree<T> root;

        /**
         * creates a new iterator over this tree starting
         * from root
         *
         * @param root the first node of the iterator
         */
        public TreeIter(Tree<T> root) {
            currentNode = root;

            internalIterStack = new ArrayList<>();

            ArrayList<Tree<T>> temp = new ArrayList<>();
            temp.add(root);

            internalIterStack.add(temp.iterator());

            this.root = root;
        }

        @Override
        public boolean hasNext() {
            return (!internalIterStack.isEmpty());
        }

        /**
         * uses internalIterStack to search for the next
         * element in the tree
         *
         * @return the next node inorder
         * @throws NoSuchElementException if the tree has been fully iterated
         */
        @Override
        public Tree<T> next() {
            return runOpExceptionSuppressed(this::_next, AccessTask.TaskOpType.READ);
        }

        private Tree<T> _next() {
            //check if any nodes are available
            if (internalIterStack.isEmpty())
                throw new NoSuchElementException();

            //get next node
            currentNode = internalIterStack.get(0).next();

            //add childstack if available
            if (currentNode != null && !currentNode.children.isEmpty())
                internalIterStack.add(0, currentNode.children.iterator());

            //clean iterstack
            while (!internalIterStack.isEmpty() &&
                    !internalIterStack.get(0).hasNext()) {
                internalIterStack.remove(0);
            }

            return currentNode;
        }
    }
}