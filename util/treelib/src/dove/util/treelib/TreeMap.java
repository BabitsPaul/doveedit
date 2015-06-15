package dove.util.treelib;

import dove.util.collections.SortedList;
import dove.util.concurrent.access.AccessTask;
import dove.util.math.MathUtil;

import java.util.*;

/**
 * creates a treemap that can be used
 * to relate every key to a value.
 *
 * @param <Key> every value can be accessed by an array of this type as key
 * @param <Val> Values saved
 */
public class TreeMap<Key, Val>
        extends Tree<Key> {
    private final Comparator<Tree<Key>> KEY_COMP =
            (t1, t2) -> MathUtil.getSign(t1.content.hashCode() - t2.content.hashCode());

    private final SortedList.SearchHelper<TreeMap<Key, Val>, Val> SEARCH_HELPER =
            ((k, v) -> MathUtil.getSign(k.content.hashCode() - v.hashCode()));

    private Val val;

    private boolean valSet;

    private Class<Key> keyClass;

    private Class<Val> valClass;

    public TreeMap(Class<Key> key, Class<Val> val) {
        super();

        this.valClass = val;
        this.keyClass = key;

        valSet = false;

        setupChildren();
    }

    public TreeMap(Key key, Class<Key> keyClazz, Class<Val> valClass) {
        super(key);

        this.keyClass = keyClazz;
        this.valClass = valClass;

        this.valSet = false;

        setupChildren();
    }

    private TreeMap(Key key, Val val, Class<Key> keyClass, Class<Val> valClass) {
        super(key);

        valSet = true;

        this.val = val;

        this.valSet = true;

        this.valClass = valClass;
        this.keyClass = keyClass;

        setupChildren();
    }

    private void setupChildren() {
        children = new SortedList<>(KEY_COMP);
    }

    private void insert(Key key, Val val) {
        try {
            _add(new TreeMap<>(key, val, keyClass, valClass));
        }
        catch (TreeBuildException ignored) {
        }
    }

    public Val getVal(Key[] path) {
        return runOpExceptionSuppressed(() -> _getVal(path), AccessTask.TaskOpType.READ);
    }

    protected Val _getVal(Key[] path) {
        TreeMap<Key, Val> node = this;

        for (Key key : path) {
            node = getMatchingChild(key);

            if (node == null)
                return null;
        }

        return node.val;
    }

    public Val getVal() {
        return runOpExceptionSuppressed(this::_getVal, AccessTask.TaskOpType.READ);
    }

    private void setVal(Val v) {
        val = v;
        valSet = true;
    }

    protected Val _getVal() {
        if (valSet)
            return val;
        else
            throw new NoSuchElementException("Value not available for this node");
    }

    public int size() {
        return runOpExceptionSuppressed(this::_size, AccessTask.TaskOpType.READ);
    }

    protected int _size() {
        class SizeMgr
                extends GoThroughManager<TreeMap<Key, Val>> {
            int size = 0;

            @Override
            public void accept(TreeMap<Key, Val> tTree) {
                if (tTree.valSet)
                    ++size;
            }
        }

        SizeMgr size = new SizeMgr();
        goThroughTree(this, size);

        return size.size;
    }

    public boolean isEmpty() {
        return runOpExceptionSuppressed(() -> _isEmpty(), AccessTask.TaskOpType.READ);
    }

    protected boolean _isEmpty() {
        return children.isEmpty();
    }

    public boolean containsKey(Object key) {
        return runOpExceptionSuppressed(() -> _containsKey(key), AccessTask.TaskOpType.READ);
    }

    protected boolean _containsKey(Object key) {
        Key[] k;

        try {
            k = (Key[]) key;
        }
        catch (ClassCastException e) {
            return false;
        }

        return ((TreeMap<Key, Val>) _getNodeForPath(k)).valSet;
    }

    public boolean containsValue(Object value) {
        return runOpExceptionSuppressed(() -> _containsValue(value), AccessTask.TaskOpType.READ);
    }

    protected boolean _containsValue(Object value) {
        return (_listLeafs().stream().filter(v -> equal(v, value)).count() != 0);
    }

    public boolean hasKey(Key[] k) {
        return runOpExceptionSuppressed(() -> _hasKey(k), AccessTask.TaskOpType.READ);
    }

    protected boolean _hasKey(Key[] k) {
        return ((TreeMap<Key, Val>) _getNodeForPath(k)).valSet;
    }

    public Val get(Key[] key) {
        return runOpExceptionSuppressed(() -> _get(key), AccessTask.TaskOpType.READ);
    }

    protected Val _get(Key[] key) {
        TreeMap<Key, Val> temp = ((TreeMap<Key, Val>) _getNodeForPath(key));

        if (temp == null)
            return null;
        else
            return temp.val;
    }

    public Val put(Key[] key, Val val) {
        return runOpExceptionSuppressed(() -> _put(key, val), AccessTask.TaskOpType.WRITE);
    }

    protected Val _put(Key[] key, Val value) {
        TreeMap<Key, Val> currentNode = this;

        for (int k = 0; k < key.length; k++) {
            //search for next child in the map (must match k)
            TreeMap<Key, Val> nextNode = currentNode.getMatchingChild(key[k]);

            //if node node with the specified value exists, create one
            if (nextNode == null) {
                //create new node
                nextNode = new TreeMap<>(key[k], keyClass, valClass);

                //insert node
                try {
                    currentNode._add(nextNode);
                }
                catch (TreeBuildException ignored) {
                    //never thrown, since this is always a valid action
                }
            }

            currentNode = nextNode;
        }

        //save previous value for key and replace with new value
        Val temp = currentNode.val;
        currentNode.setVal(value);

        //return old value
        return temp;
    }

    public void remove(Key[] key) {
        runOpExceptionSuppressed(() -> {
            _remove(key);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    protected boolean _remove(Key[] key) {
        TreeMap n = (TreeMap) getNodeForPath(key);

        if (n == null || !n.valSet)
            return false;

        n.valSet = false;

        return true;
    }

    public void putAll(Map<? extends Key[], ? extends Val> m) {
        runOpExceptionSuppressed(() -> {
            _putAll(m);
            return null;
        }, AccessTask.TaskOpType.WRITE);
    }

    protected void _putAll(Map<? extends Key[], ? extends Val> m) {
        m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
    }

    public Tree<Key> keyTree() {
        Tree<Key> result = new Tree<>();

        ArrayList<Iterator<Tree<Key>>> entryIterStack = new ArrayList<>();
        entryIterStack.add(children.iterator());

        ArrayList<Tree<Key>> productIterStack = new ArrayList<>();
        productIterStack.add(result);

        while (!entryIterStack.isEmpty()) {
            Iterator<Tree<Key>> iter = entryIterStack.get(0);

            if (!iter.hasNext()) {
                entryIterStack.remove(0);
                productIterStack.remove(0);
            }
            else {
                Tree<Key> nextChild = iter.next();

                Tree<Key> nextNode = new Tree<>(nextChild.getContent());

                try {
                    productIterStack.get(0).add(nextNode);
                }
                catch (TreeBuildException ignored) {
                }

                productIterStack.add(0, nextNode);

                entryIterStack.add(nextChild.children.iterator());
            }
        }

        return result;
    }

    public Tree<Val> valTree() {
        Tree<Val> result = new Tree<>();

        //TODO

        return result;
    }

    public List<Tree<Key>> listLeafs() {
        List<Tree<Key>> result = new ArrayList<>();

        class SearchLeafs
                extends GoThroughManager<TreeMap<Key, Val>> {
            @Override
            public void accept(TreeMap<Key, Val> tTree) {
                if (tTree.valSet)
                    result.add(tTree);
            }
        }

        goThroughTree(this, new SearchLeafs());

        return result;
    }

    public List<Key[]> listPaths() {
        ArrayList<Key[]> paths = new ArrayList<>();

        class SearchPaths
                extends GoThroughManager<TreeMap<Key, Val>> {
            @Override
            public void accept(TreeMap<Key, Val> tTree) {
                if (tTree.valSet)
                    paths.add(tTree.getPath(keyClass));
            }
        }

        goThroughTree(this, new SearchPaths());

        return paths;
    }

    @Override
    protected String getStringRep() {
        return super.getStringRep() + (valSet ? " -> " + val : "");
    }

    @Override
    protected TreeMap<Key, Val> nodeCopy() {
        TreeMap<Key, Val> result = new TreeMap(keyClass, valClass);

        if (contentSet())
            result.setContent(getContent());

        if (valSet)
            result.setVal(val);

        return result;
    }

    private TreeMap<Key, Val> getMatchingChild(Key k) {
        return (TreeMap<Key, Val>) ((SortedList) children).searchBinary(SEARCH_HELPER, k);
    }
}