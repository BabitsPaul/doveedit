package dove.util.ui;

import java.awt.*;
import java.util.ArrayList;

public class FocusCycle extends FocusTraversalPolicy {

    private Container            parent_container;
    private ArrayList<Component> components;

    private int default_component;

    public FocusCycle(ArrayList<Component> components, int default_component)
            throws IllegalArgumentException, NullPointerException {

        if (components == null) {
            throw new NullPointerException("null not allowed here - list of coreComponents required");
        }

        if (components.size() == 0) {
            return;
        }

        parent_container = components.get(0).getParent();

        for (int i = 1; i < components.size(); i++) {
            if (components.get(i).getParent() != parent_container) {
                throw new IllegalArgumentException("coreComponents must be in the same container");
            }
        }

        this.components = components;

        if (default_component < 0 || default_component >= components.size()) {
            throw new IllegalArgumentException("invalid default_component - value between 0 and " + components.size() + " required");
        }

        this.default_component = default_component;
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        if (!components.contains(aComponent)) {
            throw new IllegalArgumentException("component is no valid component of the focuscycle");
        }

        int index = components.indexOf(aComponent) + 1;
        if (index == components.size()) {
            index = 0;
        }

        return (components.get(index));
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        if (!components.contains(aComponent)) {
            throw new IllegalArgumentException("component is no valid component of the focuscycle");
        }

        int index = components.indexOf(aComponent) - 1;
        if (index == -1) {
            index = components.size() - 1;
        }

        return (components.get(index));
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        return (components.get(0));
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        return (components.get(components.size() - 1));
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        return components.get(default_component);
    }
}
