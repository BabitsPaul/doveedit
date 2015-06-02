package dove.cmd.runable;

import dove.cmd.model.MethodEntity;
import dove.cmd.model.datatypes.Data;

import java.util.ArrayList;
import java.util.List;

public class RunableTreeNode {
    private List<RunableTreeNode> children;

    private MethodEntity entity;

    public RunableTreeNode() {
        children = new ArrayList<>();
    }

    public Data execute() {
        Data[] tmp = new Data[children.size()];
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = children.get(i).execute();

        return entity.invoke(, tmp);
    }
}