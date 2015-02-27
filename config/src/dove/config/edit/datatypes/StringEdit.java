package dove.config.edit.datatypes;

import dove.config.edit.ConfigEditElem;
import dove.document.DocumentContext;

import javax.swing.*;

public class StringEdit
        extends ConfigEditElem {
    public static final int MULTIPLE_OPTIONS = 0;
    public static final int ANY_VALUE        = 1;

    public static final int DEFAULT = ANY_VALUE;

    private int mode;

    private JComponent comp;

    public StringEdit(String configElem, String propertyName, DocumentContext doc, int... mode) {
        super(configElem, propertyName);

        if (mode.length == 0)
            this.mode = DEFAULT;
        else
            this.mode = mode[0];

        comp = new JPanel();
        comp.setLayout(new BoxLayout(comp, BoxLayout.Y_AXIS));

        comp.add(new JLabel(propertyName));

        String val = doc.config.getConfiguration(configElem).get(propertyName).toString();
        switch (this.mode) {
            case MULTIPLE_OPTIONS:
                JComboBox<String> jcb = new JComboBox<String>();

                jcb.setSelectedItem(val);

                comp.add(jcb);
                break;

            case ANY_VALUE:
                JTextField jtx = new JTextField(val);

                comp.add(jtx);
                break;
        }
    }

    @Override
    public JComponent component() {
        return comp;
    }
}