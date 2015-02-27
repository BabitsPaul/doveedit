package dove.frame.m2menu;

import dove.util.treelib.Tree;

import java.util.ArrayList;

public class M2Tree
        extends Tree<String> {
    public M2Tree() {
        super(String.class, "");

        children = new ArrayList<>();
    }
}
