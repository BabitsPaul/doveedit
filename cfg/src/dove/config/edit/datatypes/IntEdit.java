package dove.config.edit.datatypes;

import dove.config.edit.ConfigEditElem;
import dove.document.DocumentContext;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class IntEdit
        extends ConfigEditElem {
    public static final int SLIDER_EDIT  = 0;
    public static final int SPINNER_EDIT = 1;
    public static final int TEXT_EDIT    = 2;

    public static final int DEFAULT = SLIDER_EDIT;

    private int editMode;

    private int bottom, top, value;

    private JComponent comp;

    public IntEdit(String configName, String propertyName, DocumentContext doc,
                   int bottom, int top, int... additional) {
        super(configName, propertyName);

        if (!(doc.config.getConfiguration(configName).get(propertyName) instanceof Integer))
            throw new IllegalArgumentException("Invalid type - only integer configurations accepted");

        if (bottom < top)
            throw new IllegalArgumentException("Invalid bottom and top values - always bottom < top");

        this.bottom = bottom;
        this.top = top;

        if (additional.length > 0)
            editMode = additional[0];
        else
            editMode = DEFAULT;


        if (additional.length > 1)
            value = additional[1];
        else
            value = ((Integer) doc.config.getConfiguration(configName).get(propertyName)).intValue();

        comp = new JPanel();
        comp.setLayout(new BoxLayout(comp, BoxLayout.Y_AXIS));

        comp.add(new JLabel(propertyName));

        switch (editMode) {
            case SLIDER_EDIT:
                JSlider slider = new JSlider(bottom, top, value);

                comp.add(slider);
                break;

            case SPINNER_EDIT:
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, bottom, top, 1));

                comp.add(spinner);
                break;

            case TEXT_EDIT:
                JTextField jtx = new JFormattedTextField(new NumberFormatter());

                jtx.setText(new Integer(value).toString());

                comp.add(jtx);
                break;
        }
    }

    @Override
    public JComponent component() {
        return comp;
    }
}