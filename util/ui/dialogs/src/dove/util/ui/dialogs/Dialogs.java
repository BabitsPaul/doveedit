package dove.util.ui.dialogs;

import dove.util.misc.ValHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Dialogs
        extends JOptionPane {
    private static int first_checkbox_bit_temp;

    public static int getFirst_checkbox_bit_temp() {
        return first_checkbox_bit_temp;
    }

    /**
     * <pre>
     * creates a dialog
     *
     * setup of the dove.frame:
     * <font color = "blue">
     * |---------------------------------------------------------|
     * |  title                                               |x||
     * |---------------------------------------------------------|
     * |       a      _____                                      |
     * |  b  message |icon |                                     |
     * |       c                                                 |
     * |     button1  d  button2  d  button3 ...                 |
     * |       e                                                 |
     * |  g  checkbox1                                           |
     * |       f                                                 |
     * |     checkbox2                                           |
     * |     ...                                                 |
     * |       h                                                 |
     * |_________________________________________________________|
     * </font>
     *
     * <font color = "black"></font>
     * values:
     * a = label_top_space
     * b = label_left_space
     * c = label_button_space
     * d = button_button_space
     * e = button_checkbox_space
     * f = checkbox_checkbox_space
     * g = checkbox_left_space
     * h = checkbox_down_space
     *
     * you can modify these hardcoded constants to rearrange the coreComponents in the dialog
     *
     * the message can be left blank
     * in that case, the free space is used to push the buttons up
     * the message label always has its specified place according to the constants
     * that are defined in the code (label_top_space and label_left_space)
     *
     *
     * for the buttons a not null value is required
     * the buttons are always arranged in a way that keeps the space between them defined by
     * button_button_space and are in the middle of the dialog (x_axis)
     * they determine the lower bits of the result (index of the button that was clicked)
     * if no message is displayed, the y-position of the buttons is defined by label_button_space
     *
     *
     * the checkboxes can be left blank
     * in that case no checkboxes are displayed
     * the checkboxes are always arranged in a line (y-axis) with a distance of
     * checkbox_left_space to the left side of the dialog and the distance determined by
     * checkbox_checkbox_space between each other
     * the upper bits starting from first_checkbox_bit_temp are used by the checkboxes
     * to set of remove their masks from the result
     * </pre>
     *
     * @param title         the title of the dialog
     * @param message       message that is displayed by the dialog
     * @param messagetype   the messagetype @see javax.swing.JOptionPane
     * @param buttons       list of buttons the dialog contains
     * @param checkboxes    list of checkboxes the dialog contains
     * @param parent        parent component null to place the dialog in the framecenter
     * @param default_value number of the button that is defaultly selected
     * @return the number of the button that is selected and the masks of the checkboxes that are selected
     */
    public static int showDialog(String title, String message, int messagetype, String[] buttons, String[] checkboxes,
                                 Component parent, int default_value) {
        final ValHelper<Integer> result = new ValHelper<>(default_value);

        //------------------------------------------------------------------------
        //constants
        //------------------------------------------------------------------------
        final int label_top_space = 10;
        final int label_button_space = 10;
        final int button_checkbox_space = 10;
        final int button_button_space = 10;
        final int button_left_space = 10;
        final int checkbox_checkbox_space = 0;
        final int checkbox_left_space = 20;
        final int checkbox_down_space = 5;

        //-----------------------------------------------------------------------
        //check for required coreComponents
        //-----------------------------------------------------------------------
        if (allNull(buttons) || buttons == null) {
            return result.getT();
        }

        boolean message_active = (message != null);
        boolean check_boxes_active = !(checkboxes == null || checkboxes.length == 0 || allNull(checkboxes));

        //-------------------------------------------------------------------------
        //create components
        //-------------------------------------------------------------------------
        final JDialog jd = new JDialog();

        jd.setTitle(title);
        jd.getContentPane().setLayout(null);
        jd.setLayout(null);
        jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jd.setResizable(true);
        jd.setModal(true);

        Dimension msg_size = null;
        JLabel msg = null;
        Icon icon = null;
        if (message_active) {
            switch (messagetype) {
                case ERROR_MESSAGE:
                    icon = UIManager.getIcon("Optionpane.errorIcon");
                    break;
                case WARNING_MESSAGE:
                    icon = UIManager.getIcon("Optionpane.warningIcon");
                    break;
                case PLAIN_MESSAGE:
                    icon = new ImageIcon();
                    break;
                case INFORMATION_MESSAGE:
                    icon = UIManager.getIcon("Optionpane.informationIcon");
                    break;
                case QUESTION_MESSAGE:
                    icon = UIManager.getIcon("Optionpane.questionIcon");
                    break;
            }

            msg = new JLabel(message, icon, JLabel.TRAILING);
            jd.add(msg);
            msg_size = msg.getPreferredSize();
        }

        int button_height = 0;
        Dimension[] button_size = new Dimension[buttons.length];
        JButton[] jButtons = new JButton[buttons.length];
        int i = 0;
        for (String button : buttons) {
            if (button == null) {
                i++;
                continue;
            }
            JButton temp = new JButton(button);
            jButtons[i] = temp;
            button_size[i] = temp.getPreferredSize();
            jd.add(temp);
            button_height = (int) temp.getPreferredSize().getHeight();
            i++;
        }


        Dimension[] checkbox_size = null;
        JCheckBox[] jCheckBox = null;
        if (check_boxes_active) {
            jCheckBox = new JCheckBox[checkboxes.length];
            checkbox_size = new Dimension[checkboxes.length];
            i = 0;
            for (String checkbox : checkboxes) {
                if (checkbox == null) {
                    i++;
                    continue;
                }
                JCheckBox temp = new JCheckBox(checkbox);
                checkbox_size[i] = temp.getPreferredSize();
                jCheckBox[i] = temp;
                jd.add(temp);
                i++;
            }
        }

        //----------------------------------------------------------------------------
        //setup focus-manager
        //----------------------------------------------------------------------------
        jd.setFocusTraversalKeysEnabled(true);

        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE); //make buttons react to enter
        //default only space (with metal look and feel
        ArrayList<Component> list = new ArrayList<>();
        for (JButton button : jButtons) {
            if (button != null) {
                list.add(button);
            }
        }
        jd.setFocusTraversalPolicy(new FocusCycle(list, default_value));

        Set<AWTKeyStroke> forward = new HashSet<>();
        forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0));
        forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
        forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
        jd.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);

        Set<AWTKeyStroke> backward = new HashSet<>();
        backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0));
        backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK));
        backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));
        jd.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);

        Set<AWTKeyStroke> upward = new HashSet<>();
        upward.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        upward.add(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0));
        upward.add(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));
        jd.setFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, upward);

        Set<AWTKeyStroke> downward = new HashSet<>();
        downward.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        downward.add(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0));
        downward.add(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
        jd.setFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, downward);

        //----------------------------------------------------------------------------
        //create listeners
        //----------------------------------------------------------------------------
        final int first_mask_bit = (int) Math.nextUp(Math.log(buttons.length) / Math.log(2)) + 1;
        first_checkbox_bit_temp = first_mask_bit;

        for (int temp = 0; temp < buttons.length; temp++) {
            final int temp_val = temp;

            if (buttons[temp] == null) {
                continue;
            }

            jButtons[temp].addActionListener(new ActionListener() {
                int result_val = temp_val;

                @Override
                public void actionPerformed(ActionEvent e) {
                    result.setT(result.getT() + result_val);

                    jd.setModal(false);
                    jd.setVisible(false);
                    jd.dispose();
                }
            });
        }

        if (check_boxes_active) {
            for (int temp = 0; temp < checkboxes.length; temp++) {
                final int temp_val = temp;

                if (checkboxes[temp] == null) {
                    continue;
                }

                jCheckBox[temp].addActionListener(new ActionListener() {
                    int mask = 1 << (first_mask_bit + temp_val);

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if ((result.getT() & mask) != 0) {
                            result.setT(result.getT() & ~mask);
                        }
                        else {
                            result.setT(result.getT() | mask);
                        }
                    }
                });
            }
        }

        //----------------------------------------------------------------------------
        //calculate dialog-metrics
        //----------------------------------------------------------------------------
        double dialog_width = 0;
        double dialog_height = 0; //dove.frame top

        if (message_active) {
            dialog_height += msg_size.getHeight() + label_top_space;
            dialog_width = msg_size.getWidth();
        }

        int buttons_width = button_left_space;
        dialog_height += button_height + label_button_space + button_checkbox_space;
        for (Dimension size : button_size) {
            if (size == null) {
                continue;
            }

            buttons_width += size.getWidth() + button_button_space;
        }
        if (buttons_width > dialog_width) {
            dialog_width = buttons_width;
        }

        if (check_boxes_active) {
            for (Dimension size : checkbox_size) {
                if (size == null) {
                    continue;
                }

                dialog_height += size.getHeight() + checkbox_checkbox_space;
                if (size.getWidth() + checkbox_left_space > dialog_width) {
                    dialog_width = size.getWidth();
                }
            }
            dialog_height += checkbox_down_space;
        }

        jd.getContentPane().setPreferredSize(new Dimension((int) dialog_width, (int) dialog_height));
        jd.pack();
        if (parent == null) {
            jd.setLocationRelativeTo(null); //set dialog to screen-center
        }
        else {
            jd.setLocationRelativeTo(parent);
        }

        //----------------------------------------------------------------------
        //calculate label-measures
        //----------------------------------------------------------------------
        if (message_active) {
            int message_x = (int) (dialog_width - msg_size.getWidth()) / 2;

            msg.setBounds(message_x, label_top_space, (int) msg_size.getWidth(), (int) msg_size.getHeight());
        }

        //-----------------------------------------------------------------------
        //calculate button-measures
        //-----------------------------------------------------------------------
        int button_y;
        //buttons_width
        //jButtons

        if (message_active) {
            button_y = (int) msg_size.getHeight() + label_button_space + label_top_space;
        }
        else {
            button_y = label_top_space;
        }

        int button_border_x = button_left_space + (int) (dialog_width - buttons_width) / 2;
        int button_num = 0;
        for (JButton button : jButtons) {
            if (button == null) {
                button_num++;
                continue;
            }

            button.setBounds(button_border_x, button_y,
                    (int) button_size[button_num].getWidth(), (int) button_size[button_num].getHeight());
            button_border_x += button_button_space + button_size[button_num].getWidth();
            ++button_num;
        }

        //-------------------------------------------------------------------------------
        //calculate checkbox-bounds
        //-------------------------------------------------------------------------------
        if (check_boxes_active) {
            //checkbox_x

            if (check_boxes_active) {
                int checkbox_border_y = (int) (label_button_space + button_height + button_checkbox_space +
                        (message_active ? label_top_space + msg_size.getHeight() : 0));
                int checkbox_num = 0;
                for (JCheckBox checkBox : jCheckBox) {
                    if (checkBox == null) {
                        checkbox_num++;
                        continue;
                    }

                    checkBox.setBounds(checkbox_left_space, checkbox_border_y,
                            (int) checkbox_size[checkbox_num].getWidth(), (int) checkbox_size[checkbox_num].getHeight());
                    checkbox_border_y += checkbox_size[checkbox_num].getHeight() + checkbox_checkbox_space;
                    ++checkbox_num;
                }
            }
        }

        //-------------------------------------------------------------------------------
        //show dialog and return selection of the user
        //-------------------------------------------------------------------------------
        jd.setVisible(true);

        return result.getT();
    }

    private static boolean allNull(Object[] o) {
        for (Object temp : o) {
            if (temp != null) {
                return false;
            }
        }

        return true;
    }
}