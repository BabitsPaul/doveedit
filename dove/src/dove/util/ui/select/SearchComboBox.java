package dove.util.ui.select;

import dove.clipboard.InternalClipboardHelper;
import dove.util.concurrent.Ticker;
import dove.util.misc.ColorUtil;
import dove.util.treelib.Tree;
import dove.util.treelib.TreeMap;
import dove.util.ui.Dialogs;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.awt.event.KeyEvent.*;

public class SearchComboBox<T>
        extends JPanel
        implements KeyListener,
        ActionListener,
        MouseListener,
        ComponentListener,
        FocusListener {
    protected static final int XPOS_LEFT_BOUND    = 5;
    protected static final int CURSOR_RIGHT_SPACE = 5;

    //////////////////////////////////////////////////////////////////////
    // size
    //
    // provides size methods like set size,
    // update size, minimumSize, preferredSize, etc.
    //////////////////////////////////////////////////////////////////////
    protected int scrollTextLeft = 0;

    //////////////////////////////////////////////////////////////////////
    // ui block
    //
    // provides methods to setup and handle the ui
    // related to this class
    //////////////////////////////////////////////////////////////////////
    protected FontMetrics          fontMetrics;
    protected boolean              popupVisible;
    protected JPopupMenu           popup;
    protected JButton              popupButton;
    protected ArrayList<Component> fontUpdate;
    protected Color   selectedColor     = Color.blue.brighter();
    protected String  infoString        = "search";
    protected boolean displayInfoString = true;
    protected CursorTicker          cursorTicker;
    protected Thread                currentUpdater;
    protected int                   cursorPosition;
    protected int                   selectionStart;
    protected int                   selectionEnd;
    protected TreeMap<Character, T> possibleSelections;
    protected String                currentSearch;
    protected AbstractList<String>  currentOptions;
    protected int                   currentOptionIndex;
    protected boolean optionSelected  = false;
    protected int     startDragMouseX = 0;
    protected int     endDragMouseX   = 0;
    protected int     textDragPos     = 0;
    protected ArrayList<SelectionListener<T>> listeners;
    protected SearchBoxExceptionHandler handler                 = (e -> {
        throw e;
    });
    protected ExceptionPolicy           policy                  = ExceptionPolicy.CORRECT_IF_POSSIBLE;
    protected boolean                   searchComboBoxAvailable = true;
    protected Runnable                  globalStop              = (() -> System.exit(1));

    public SearchComboBox(TreeMap<Character, T> provider) {
        //create basic vars
        possibleSelections = provider;
        provider.addModelChangedListener(e -> updateDisplayedOptions());

        currentSearch = "";

        currentOptionIndex = 0;

        fontUpdate = new ArrayList<>();

        cursorPosition = 0;

        selectionStart = 0;

        selectionEnd = 0;

        listeners = new ArrayList<>();

        popupVisible = false;

        fontMetrics = getFontMetrics(getFont());

        cursorTicker = new CursorTicker(500);

        //setup component
        cursorTicker.start();

        sizeHelper();

        setupUI();
    }

    /**
     * creates the minimumSize
     * and preferredSize Dimension for this element
     */
    protected void sizeHelper() {
        int width = getFont().getSize() * 10;
        int height;

        if (fontMetrics == null) {
            height = getFont().getSize();

            //try to update the font
            try {
                fontMetrics = getFontMetrics(getFont());
            }
            catch (NullPointerException e) {
                handleException(e, "couldn't create fontmetrics instance",
                        InternalExceptionCause.FONTMETRICS_NOT_AVAILABLE);
            }
        }
        else
            height = fontMetrics.getHeight();

        setMinimumSize(new Dimension(width, height + 2));

        setPreferredSize(new Dimension(width * 2, height + 10));
    }

    protected void setupUI() {
        setLayout(new BorderLayout());

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        popupButton = new BasicArrowButton(SwingConstants.SOUTH);
        popupButton.addActionListener(this);
        add(popupButton, BorderLayout.EAST);
        fontUpdate.add(popupButton);

        popup = new JPopupMenu("options");
        popup.setInvoker(this);
        popup.setFocusable(false);
        popupUpdateHelper();
        fontUpdate.add(popup);
    }

    protected void setPopupVisible(boolean popupVisible) {
        if (popupVisible) {
            popup.setVisible(false);
        }
        else {
            popup.show(this, 0, getHeight());
        }

        this.popupVisible = popupVisible;
    }

    /////////////////////////////////////////////////////////////////////////////
    // model
    /////////////////////////////////////////////////////////////////////////////

    protected void recreatePopup() {
        if (!popupVisible)
            return;

        //if a previous updater is still running, interrupt it
        if (currentUpdater != null) {
            currentUpdater.interrupt();

            currentUpdater = null;
        }

        //start new updater thread and remove it as previous updater when done
        currentUpdater = new Thread(() -> SwingUtilities.invokeLater(() -> {
            popup.invalidate();

            popup.removeAll();

            JMenuItem load = new JMenuItem("load");
            popup.add(load);

            popupUpdateHelper();

            popup.remove(load);

            currentUpdater = null;

            if (currentOptions.isEmpty())
                popup.add(new JMenuItem("no results"));

            popup.validate();
            popup.repaint();

            //TODO create content properly
            //TODO apply content to size
        }));

        currentUpdater.setName("search result updater");
        currentUpdater.setDaemon(true);
        currentUpdater.start();
    }

    protected void popupUpdateHelper() {
        //recalculate found options for this search
        updateDisplayedOptions();

        popup.invalidate();

        popup.removeAll();

        Iterator<String> optIter = currentOptions.iterator();

        int count = 1;
        while (optIter.hasNext()) {
            if ((count % 10) == 0) {
                JMenu more = new JMenu("more");
                popup.add(more);
            }
            else {
                String opt = optIter.next();

                JMenuItem jmi = new JMenuItem(opt);
                jmi.setActionCommand(opt);
                jmi.addActionListener(this);
                popup.add(jmi);
            }
        }

        popup.validate();
        popup.repaint();
    }

    protected void updateText() {
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int yPos_lower_bound = 5 + fontMetrics.getHeight();

        paintBoxes(g);
        paintSelection(g, yPos_lower_bound);
        paintText(g, yPos_lower_bound);
        paintCursor(g, yPos_lower_bound);
    }

    protected void paintText(Graphics g, int yPos_lower_bound) {
        g.setColor(getForeground());

        if (displayInfoString) {
            g.drawString(infoString, XPOS_LEFT_BOUND, yPos_lower_bound);
        }
        else {
            g.drawString(currentSearch, XPOS_LEFT_BOUND + scrollTextLeft, yPos_lower_bound);
        }
    }

    protected void paintCursor(Graphics g, int yPos_lower_bound) {
        //only paint if the cursor is currently visible
        //(cursor is blinking)
        g.setColor(getForeground());

        if (!cursorTicker.cursorVisible())
            return;

        System.out.println(cursorPosition + " / " + currentSearch.length());

        int cursorPosX =
                fontMetrics.stringWidth(currentSearch.substring(0, cursorPosition))
                        + XPOS_LEFT_BOUND + scrollTextLeft;

        g.drawLine(cursorPosX, yPos_lower_bound - fontMetrics.getHeight(),
                cursorPosX, yPos_lower_bound);
    }

    protected void paintSelection(Graphics g, int yPos_lower_bound) {
        //TODO paint mark at correct position
        if (selectionStart == selectionEnd)
            return;

        g.setColor(selectedColor);

        int startX = calculateScreenPosition(selectionStart) + scrollTextLeft;
        int endX = calculateScreenPosition(selectionEnd) + scrollTextLeft;

        g.fillRect(startX, yPos_lower_bound - fontMetrics.getHeight(),
                endX, yPos_lower_bound);
    }

    protected void paintBoxes(Graphics g) {
        g.drawRect(0, 0, getWidth() - popupButton.getWidth() - 1, getHeight() - 1);
        g.setColor(getBackground());
        g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
    }

    @Override
    public void setFont(Font f) {
        super.setFont(f);

        //used during initialization of superclass (method call without variable-initialisation)
        if (fontUpdate != null)
            fontUpdate.forEach(c -> c.setFont(f));

        fontMetrics = getFontMetrics(f);
    }

    @Override
    public void setForeground(Color foreground) {
        super.setForeground(foreground);

        selectedColor = ColorUtil.avgComplementaryColor(getForeground(), getBackground());
    }

    @Override
    public void setBackground(Color background) {
        super.setBackground(background);

        selectedColor = ColorUtil.avgComplementaryColor(getForeground(), getBackground());
    }

    public void setInfoString(String info) {
        infoString = info;
    }

    public void setDisplayedText(String text) {
        currentSearch = text;

        updateText();
    }

    protected void calcScrollLeft() {
        int cursorPosX =
                fontMetrics.stringWidth(currentSearch.substring(0, cursorPosition))
                        + XPOS_LEFT_BOUND;

        int visibleTextBoundRight = getWidth() - popupButton.getWidth() - CURSOR_RIGHT_SPACE;

        //the cursor is still within the visible part of the text
        //leave value
        if (cursorPosX + scrollTextLeft < visibleTextBoundRight)
            if (cursorPosX + scrollTextLeft < 0)
                scrollTextLeft = 0;
            else
                return;

        if (cursorPosX > visibleTextBoundRight)
            //calculate how much the text must be moved to the left to
            //make the full text visible
            scrollTextLeft = -(cursorPosX - visibleTextBoundRight) - CURSOR_RIGHT_SPACE;
    }

    protected int calculateCharPosition(int mouseAtX) {
        int char_at = 0;

        //text starts after this position
        mouseAtX -= XPOS_LEFT_BOUND;

        //if the text is bigger than the component
        //the text will be moved -scrollTextLeft pixels to the left
        mouseAtX -= scrollTextLeft;

        //calculate new cursorposition
        while (mouseAtX > 0 && char_at < currentSearch.length())
            mouseAtX -= fontMetrics.charWidth(currentSearch.charAt(char_at++));


        return char_at - 1;
    }

    protected int calculateScreenPosition(int atChar) {
        String toChar = currentSearch.substring(0, atChar + 1);

        return fontMetrics.stringWidth(toChar);
    }

    protected void moveCursor(int deltaPos) {
        setCursor(cursorPosition + deltaPos);

        if (deltaPos > 0 && optionSelected)
            currentSearch = currentOptions.get(currentOptionIndex);

        updateText();
    }

    protected void openSelected() {
        //find selected element
        fireNotification(currentSearch);
    }

    protected void removeChar() {
        if (currentSearch.length() == 0 || cursorPosition == 0)
            return;

        String partA;
        String partB;

        if (cursorPosition == currentSearch.length()) {
            partA = currentSearch.substring(0, currentSearch.length() - 1);
            partB = "";
        }
        else {
            partA = currentSearch.substring(0, cursorPosition);
            partB = currentSearch.substring(cursorPosition + 1);
        }

        setText(partA + partB);

        cursorPosition--;

        recreatePopup();

        updateText();
    }

    protected void addChar(char c) {
        StringBuilder ins = new StringBuilder(currentSearch);

        ins.insert(cursorPosition, c);

        setText(ins.toString());

        setCursor(cursorPosition + 1);
    }

    /////////////////////////////////////////////////////////////////////////////
    // event handling
    //
    // processes events and translates them into actions
    /////////////////////////////////////////////////////////////////////////////

    protected void setText(String text) {
        currentSearch = text;

        if (cursorPosition > text.length())
            setCursor(text.length());

        displayInfoString = (text.length() == 0);

        recreatePopup();

        updateText();
    }

    protected void selectText(int from, int to) {
        selectionStart = from;
        selectionEnd = to;

        updateText();
    }

    protected void setCursor(int at) {
        if (at < 0 || at > currentSearch.length())
            return;

        calcScrollLeft();

        cursorPosition = at;

        updateText();
    }

    protected void removeSelected() {
        String textA;
        String textB;

        if (selectionStart > 0)
            textA = currentSearch.substring(0, selectionStart);
        else
            textA = "";

        if (selectionEnd < currentSearch.length() - 1)
            textB = currentSearch.substring(selectionEnd + 1);
        else
            textB = "";

        setText(textA + textB);
    }

    protected void copySelected() {
        //nothing selected
        if (selectionStart == selectionEnd)
            return;


        String selected = currentSearch.substring(selectionStart, selectionEnd);

        InternalClipboardHelper.setClipboardContent(selected);

        selectionStart = selectionEnd;
    }

    protected void pasteClipboard() {
        String currentString = InternalClipboardHelper.getClipboardContent();

        String nText;

        if (cursorPosition == 0)
            nText = currentString + currentSearch;
        else
            nText = currentString.substring(0, cursorPosition) +
                    currentString +
                    currentString.substring(cursorPosition);

        setText(nText);
    }

    protected void cutSelected() {
        if (selectionStart == selectionEnd)
            return;

        InternalClipboardHelper.setClipboardContent(
                currentSearch.substring(selectionStart, selectionEnd));

        String textA;
        String textB;

        if (selectionStart == 0)
            textA = "";
        else
            textA = currentSearch.substring(0, selectionStart);

        if (selectionEnd == currentSearch.length())
            textB = "";
        else
            textB = currentSearch.substring(selectionEnd);

        setText(textA + textB);
    }

    @SuppressWarnings("unchecked")
    protected void updateDisplayedOptions()//works
    {
        Character[] path = new Character[currentSearch.length()];
        for (int i = 0; i < path.length; i++)
            path[i] = currentSearch.charAt(i);

        List<Tree<Character>> search = possibleSelections.listMatchingPaths(path);

        Tree<Character> result = Tree.glue(search, Character.class);

        currentOptions = new ArrayList<>();

        for (Character[] p : result.listPaths()) {
            String opt = "";

            for (Character c : p)
                opt += c;

            currentOptions.add(opt);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!searchComboBoxAvailable)
            return;

        if (e.isControlDown())
            switch (e.getKeyChar()) {
                case 'a':
                    selectText(0, currentSearch.length());
                    break;

                case 'c':
                    copySelected();
                    break;

                case 'v':
                    pasteClipboard();
                    break;

                case 'x':
                    cutSelected();
                    break;
            }

        if (     //filter backspace and enter
                e.getKeyChar() != '\n' &&
                        e.getKeyChar() != '\b' &&
                        e.getKeyChar() != '\r') {
            addChar(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!searchComboBoxAvailable)
            return;

        switch (e.getKeyCode()) {
            case VK_RIGHT:
                moveCursor(1);
                break;

            case VK_LEFT:
                moveCursor(-1);
                break;

            case VK_ENTER:
                openSelected();
                break;

            case VK_BACK_SPACE:
                if (selectionStart == selectionEnd)
                    removeChar();
                else
                    removeSelected();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!searchComboBoxAvailable)
            return;

        if (e.getSource().equals(popupButton))
            setPopupVisible(!popupVisible);
        else {
            fireNotification(e.getActionCommand());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //set new cursorposition
        setCursor(calculateCharPosition(e.getX()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startDragMouseX = e.getX();
        endDragMouseX = e.getX();

        if (displayInfoString)
            displayInfoString = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        endDragMouseX = e.getX();

        if (endDragMouseX != startDragMouseX) {
            selectionStart = calculateCharPosition(startDragMouseX);
            selectionEnd = calculateCharPosition(endDragMouseX);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //empty stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //empty stub
    }

    @Override
    public void componentResized(ComponentEvent e) {
        //TODO update uivars
    }

    //////////////////////////////////////////////////////////////////////////////////
    // notification
    //
    // methods to notify listeners if an element has been selected
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void componentMoved(ComponentEvent e) {
        if (popupVisible) {
            setPopupVisible(false);
            setPopupVisible(true);
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //empty stub
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        setPopupVisible(false);
    }

    /////////////////////////////////////////////////////////////////
    // exception handling
    /////////////////////////////////////////////////////////////////

    @Override
    public void focusGained(FocusEvent e) {
        //empty stub
    }

    @Override
    public void focusLost(FocusEvent e) {

        setPopupVisible(false);
    }

    public void addSelectionListener(SelectionListener<T> listener) {
        if (listener == null)
            throw new NullPointerException("invalid argument - null is no valid listener");

        listeners.add(listener);
    }

    protected void fireNotification(String option) {
        Character[] toChar = new Character[option.length()];
        for (int i = 0; i < option.length(); i++)
            toChar[i] = option.charAt(i);

        //break off if the selected option is no valid option
        //example: uncomplete key
        if (!possibleSelections.hasPath(toChar))
            return;

        T selected = possibleSelections.get(toChar);

        listeners.forEach(l -> l.selected(new SelectionEvent<>(selected)));
    }

    protected void handleException(Exception e, String msg, InternalExceptionCause cause) {
        SearchBoxInternalException exception = new SearchBoxInternalException(e, msg);

        switch (policy) {
            case CORRECT_IF_POSSIBLE:

                switch (cause) {
                    case POLICY_NOT_AVAILABLE:
                        policy = ExceptionPolicy.CORRECT_IF_POSSIBLE;
                        break;

                    case FONTMETRICS_NOT_AVAILABLE:
                        if (!fixFontMetrics())
                            handler.exceptionThrown(exception);
                        break;

                    case UNKNOWN:
                        handler.exceptionThrown(exception);
                        break;
                }
                break;

            case STOP_PROCESS:
                searchComboBoxAvailable = false;

                if (currentUpdater != null) {
                    currentUpdater.stop();
                }

                break;

            case GLOBAL_STOP:
                globalStop.run();
                break;

            case NOTIFY_AND_CONTINUE:
                Dialogs.showDialog("Internal Exception", msg, JOptionPane.ERROR_MESSAGE,
                        new String[]{"OK"}, new String[]{}, this, 0);
                break;

            default:
                break;
        }
    }

    /**
     * fixes the fontmetrics if an error occured due to some unknown reason
     *
     * @return true, if successfully a fontmetricsobject could be created
     */
    protected boolean fixFontMetrics() {
        Font f = getFont();
        FontMetrics metrics = this.fontMetrics;

        if (f == null) {
            f = new Font("Italic", Font.PLAIN, 12);

            metrics = getFontMetrics(f);
        }

        if (metrics == null) {
            BufferedImage bi = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);

            Graphics g = bi.createGraphics();

            metrics = g.getFontMetrics(f);

            g.dispose();
        }

        fontMetrics = metrics;

        return (metrics == null);
    }

    public void setExceptionPolicy(ExceptionPolicy policy) {
        if (policy == null)
            handleException(new NullPointerException("invalid policy"),
                    "the exceptionpolicy must always be a valid value",
                    InternalExceptionCause.POLICY_NOT_AVAILABLE);

        this.policy = policy;
    }

    public void setGlobalStopBehaviour(Runnable r) {
        if (r == null)
            handleException(new NullPointerException("null not allowed here - a behaviour must be specified"),
                    "null is not a valid behaviour", InternalExceptionCause.UNKNOWN);

        globalStop = r;
    }

    public void setInternalExceptionHandler(SearchBoxExceptionHandler h) {
        if (h == null)
            handleException(new NullPointerException("invalid exceptionhandler"), "no handler specified",
                    InternalExceptionCause.UNKNOWN);

        handler = h;
    }

    public enum ExceptionPolicy {
        CORRECT_IF_POSSIBLE,
        STOP_PROCESS,

        /**
         * stops the complete process utilizing this
         * combobox-instance
         */
        GLOBAL_STOP,

        /**
         * the process is continued and the exception
         * simply ignored - not recommended due to the hazard of severe errors
         */
        NOTIFY_AND_CONTINUE
    }

    enum InternalExceptionCause {
        FONTMETRICS_NOT_AVAILABLE,
        INVALID_DATA_STRUCTURE,
        POLICY_NOT_AVAILABLE,
        UNKNOWN
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // internal helper classes
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected class CursorTicker
            extends Ticker {
        protected boolean cursorVisible = true;

        public CursorTicker(long wait) {
            super(wait);
        }

        public synchronized boolean cursorVisible() {
            return cursorVisible;
        }

        protected synchronized void nextTick() {
            cursorVisible = !cursorVisible;

            repaint();
        }
    }
}