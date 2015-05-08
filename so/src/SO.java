import javafx.util.Pair;

import java.util.ArrayList;

public class SO {
    public static void main(String[] args) {
        Pair[] p = new Pair[]{new Pair("Hello", 123), new Pair("Hi", 1234)
                , new Pair("John", 42142), null, new Pair("Chris", null), new Pair("Peter", null), null};

        ArrayList<Pair<String, Float>> list = new ArrayList<>();
        for (Pair pr : p)
            list.add(pr);

        createTree(list);
    }

    public static void createTree(ArrayList<Pair<String, Float>> vector) {
        System.out.println(vector);

        ArrayList<TreeNode> todo = new ArrayList<>();
        todo.add(new TreeNode(vector.remove(0), null, null));
        TreeNode root = todo.get(0);

        while (!todo.isEmpty() && !vector.isEmpty()) {
            TreeNode node = todo.remove(0);

            if (node == null)
                continue;

            TreeNode left = vector.get(0) == null ? null : new TreeNode(vector.get(0), null, null);
            TreeNode right = vector.get(1) == null ? null : new TreeNode(vector.get(1), null, null);

            vector.remove(0);
            vector.remove(0);

            node.leftNode = left;
            node.rightNode = right;

            todo.add(left);
            todo.add(right);
        }

        System.out.print(TreeNode.listNodes(root));
    }

    private static class TreeNode {
        private Pair<String, Float> data;
        private TreeNode leftNode;
        private TreeNode rightNode;

        private TreeNode(Pair<String, Float> data, TreeNode left, TreeNode right) {
            this.data = data;
            this.leftNode = left;
            this.rightNode = right;
        }

        private static ArrayList<TreeNode> listNodes(TreeNode root) {
            ArrayList result = new ArrayList();
            ArrayList<TreeNode> todo = new ArrayList();
            todo.add(root);

            while (!todo.isEmpty()) {
                TreeNode node = todo.remove(0);

                if (node != null) {
                    todo.add(node.leftNode);
                    todo.add(node.rightNode);
                }

                result.add(node);
            }


            return result;
        }

        public String toString() {
            return "< " + data.toString() + " >";
        }
    }
}
