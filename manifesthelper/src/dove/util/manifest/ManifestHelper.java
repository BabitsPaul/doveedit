package dove.util.manifest;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ManifestHelper {
    /**
     * main attributes
     */
    private static final String[] MAIN_ATTRIBUTE_NAMES =
            new String[]
                    {
                            "Manifest-Version",
                            "Created-By",
                            "Main-Class",
                            "Class-Path",
                            "Name",
                            "Specification-Title",
                            "Specification-Version",
                            "Specification-Vendor",
                            "Implementation-Title",
                            "Implementation-Version",
                            "Implementation-Vendor",
                            "Permissions",
                            "Application-Name",
                            "Application-Library-Allowable-Codebase",
                            "Caller-Allowable-Codebase",
                            "Entry-Point",
                            "Trusted-Only",
                            "Trusted-Library"
                    };
    /**
     * called when the user selects "Open" in the menu
     */
    private Action                       openAction;
    /**
     * used when the user calls "Commit" in the menu
     */
    private Action                       commitAction;
    /**
     * used when the user either closes the window or
     * calls "Exit" in the menu
     */
    private Action                       exitAction;
    /**
     * used when the addattribute button is clicked
     */
    private Action                       addAttributeAction;
    /**
     * used when the removeattribute button is clicked
     */
    private Action                       removeAttributeAction;
    /**
     * the mainframe
     */
    private JFrame                       frame;
    /**
     * true, if any changes to the manifest haven't
     * yet been commited
     */
    private boolean                      manifestEdited;
    /**
     * the file where the manifest is located (either .jar or .MF)
     */
    private File                         manifest;
    /**
     * true, if the manifest is embedded in a jar
     */
    private boolean                      jarEmbedded;
    /**
     * true, if a manifest has been loaded
     */
    private boolean                      manifestLoaded;
    /**
     * the manifest that is being edited
     */
    private Manifest                     mf;
    /**
     * the table containing all attributes
     */
    private ArrayList<ManifestAttribute> vars;
    /**
     * the panel containing the attributes
     */
    private JPanel                       attributePanel;

    /**
     * creates a new manifesthelper and initializes
     * all variables
     */
    public ManifestHelper() {
        addAttributeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAttribute();
            }

            {
                putValue(Action.ACTION_COMMAND_KEY, "add attribute");
                putValue(Action.SHORT_DESCRIPTION, "add an attribute to the manifest (ctrl + a)");
                putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK).getKeyCode());
                setEnabled(false);
            }


        };

        removeAttributeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAttribute();
            }

            {
                putValue(Action.ACTION_COMMAND_KEY, "remove attribute");
                putValue(Action.SHORT_DESCRIPTION, "remove an attribute from the manifest (ctrl + r)");
                putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK).getKeyCode());
                setEnabled(false);
            }


        };

        exitAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeUI();
            }

            {
                putValue(Action.ACTION_COMMAND_KEY, "exit editor");
                putValue(Action.SHORT_DESCRIPTION, "close the editor (ctrl + f4)");
                putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK).getKeyCode());
            }


        };

        commitAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveManifest();
            }

            {
                putValue(Action.ACTION_COMMAND_KEY, "save changes");
                putValue(Action.SHORT_DESCRIPTION, "save changes to manifest (ctrl + s)");
                putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK).getKeyCode());
            }


        };

        openAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectManifest();
            }

            {
                putValue(Action.ACTION_COMMAND_KEY, "load manifest");
                putValue(Action.SHORT_DESCRIPTION, "load new manifest (ctrl + o)");
                putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK).getKeyCode());
            }


        };

        mf = new Manifest();
        manifestEdited = false;
        vars = new ArrayList<>();
        manifestLoaded = false;
    }

    //////////////////////////////////////////////////////////////////
    // constructor
    //////////////////////////////////////////////////////////////////

    /**
     * creates a new manifesthelper window
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        new ManifestHelper().createUI();
    }

    //////////////////////////////////////////////////////////////////
    // UI
    //////////////////////////////////////////////////////////////////

    /**
     * creates the ui
     */
    public void createUI() {
        //create menubar
        JMenuBar jmb = new JMenuBar();

        JMenu file = new JMenu("File");
        jmb.add(file);

        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(openAction);
        file.add(open);

        JMenuItem commit = new JMenuItem("Commit");
        commit.addActionListener(commitAction);
        file.add(commit);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(exitAction);
        file.add(exit);

        //create buttons
        attributePanel = new JPanel();
        attributePanel.setLayout(new BoxLayout(attributePanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton addAttribute = new JButton(addAttributeAction);
        addAttribute.setText("add attribute");
        buttonPanel.add(addAttribute);

        JButton removeAttribute = new JButton(removeAttributeAction);
        removeAttribute.setText("remove attribute");
        buttonPanel.add(removeAttribute);

        JScrollPane jsp = new JScrollPane(attributePanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
                , ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //create frame
        frame = new JFrame("Manifest Util");
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(buttonPanel);
        frame.add(jsp);
        frame.setJMenuBar(jmb);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(400, 500);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitAction.actionPerformed(new ActionEvent(e, ActionEvent.ACTION_PERFORMED, "exitaction"));
            }
        });
        frame.setVisible(true);

        noManifestAvailable();
    }

    /**
     * closes the ui and exits the program
     */
    public void closeUI() {
        boolean quit = !manifestEdited;

        if (!quit) {
            quit = (JOptionPane.showOptionDialog(frame, "Unsaved changes to manifest file",
                    "Exit?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new String[]{"Abort", "Quit anyway"}, "Abort")
                    == 1);
        }

        if (quit) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    /**
     * selects a manifestfile
     * <p>
     * and loads it
     */
    public void selectManifest() {
        if (manifestEdited) {
            int selection = JOptionPane.showOptionDialog(frame, "Changes to the manifest havent been saved", "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    new String[]{"Continue", "Abort"}, "Abort");

            if (selection == 1)
                return;
        }

        noManifestAvailable();

        JFileChooser jfc = new JFileChooser(new File("../"));
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.addChoosableFileFilter(new FileFilter() {
            final String jar = ".jar";

            final String mf = ".MF";

            @Override
            public boolean accept(File f) {
                return (f.getName().endsWith(jar) || f.getName().endsWith(mf) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return ".jar and .MF - files";
            }
        });

        if (jfc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            manifest = jfc.getSelectedFile();
        else
            return;

        manifestEdited = false;
        jarEmbedded = manifest.getName().endsWith(".jar");

        if (jarEmbedded) {
            try {
                JarFile jarFile = new JarFile(manifest);

                mf = jarFile.getManifest();

                jarFile.close();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to open the manifest from the specified location: " + manifest.getName() +
                        "Cause: " + e.getMessage());

                return;
            }
        }
        else {
            try {
                FileInputStream fis = new FileInputStream(manifest);

                this.mf = new Manifest(fis);

                fis.close();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to open the manifest from the specified location: " + manifest.getName() +
                        "Cause: " + e.getMessage());

                return;
            }
        }

        manifestAvailable();
    }

    /**
     * called when an manifest has been loaded
     * sets all flags, enables the buttons and
     * shows all attributes
     */
    public void manifestAvailable() {
        manifestLoaded = true;
        addAttributeAction.setEnabled(true);
        removeAttributeAction.setEnabled(true);
        showManifest();

        frame.repaint();
    }

    /**
     * called when the manifest is being unloaded
     * sets all flags, disables the buttons and
     * removes all attributes from the ui and the
     * manifest
     */
    public void noManifestAvailable() {
        manifestLoaded = false;
        mf.clear();
        addAttributeAction.setEnabled(false);
        removeAttributeAction.setEnabled(false);
        attributePanel.removeAll();

        frame.repaint();
    }

    ////////////////////////////////////////////////////////////////////
    // attributes
    ////////////////////////////////////////////////////////////////////

    /**
     * shows the manifest on the ui
     */
    public void showManifest() {
        if (!manifestLoaded)
            return;

        attributePanel.removeAll();

        mf.getMainAttributes().entrySet().forEach(e ->
                attributePanel.add(new ManifestAttribute(e.getKey().toString(), e.getValue().toString(), true)));

        mf.getEntries().entrySet().forEach(e ->
                attributePanel.add(new ManifestAttribute(e.getKey(), e.getValue().toString(), false)));

        frame.revalidate();
        frame.repaint();
    }

    /**
     * adds an attribute to the manifest
     */
    public void addAttribute() {
        //dialog containing ui
        JDialog dialog = new JDialog(frame, "Add attribute", true);

        //mainPanel contains all elements
        JPanel mainPanel = new JPanel(new BorderLayout());

        //tabbedpane for attributecreation
        JTabbedPane jtp = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        mainPanel.add(jtp, BorderLayout.CENTER);

        //////////////////////////////////////////////////
        // create panel for mainAttributes
        //////////////////////////////////////////////////

        {
            JPanel mainAttributes = new JPanel();
            mainAttributes.setName("Main Attributes");
            mainAttributes.setLayout(new BoxLayout(mainAttributes, BoxLayout.Y_AXIS));
            jtp.add(mainAttributes);

            JPanel namePanel = new JPanel();
            namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
            mainAttributes.add(namePanel);

            JTextField name = new JTextField();
            name.setPreferredSize(new Dimension(75, (int) name.getPreferredSize().getHeight()));
            namePanel.add(name);

            JButton defaultValues = new JButton("default");
            defaultValues.addActionListener(e -> {
                JDialog defaultDialog = new JDialog(frame, "Default Attributes", true);
                defaultDialog.getContentPane().setLayout(new BoxLayout(defaultDialog.getContentPane(), BoxLayout.Y_AXIS));

                JComboBox<String> jcb = new JComboBox<>(MAIN_ATTRIBUTE_NAMES);
                defaultDialog.add(jcb);

                JButton select = new JButton("Select attribute");
                select.addActionListener(e1 ->
                {
                    name.setText(jcb.getSelectedItem().toString());
                    defaultDialog.setVisible(false);
                    defaultDialog.dispose();
                });
                defaultDialog.add(select);

                defaultDialog.setSize(200, 100);
                defaultDialog.setVisible(true);
            });
            namePanel.add(defaultValues);

            JTextArea value = new JTextArea();
            mainAttributes.add(value);

            //add button
            JButton add = new JButton("Add attribute");
            add.addActionListener(e ->
            {
                if (name.getText().length() == 0 || value.getText().length() == 0)
                    return;

                ManifestAttribute attribute = new ManifestAttribute(name.getText(), value.getText(), true);

                frame.add(attribute);
                vars.add(attribute);
            });
            attributePanel.add(add, BorderLayout.SOUTH);
        }

        /////////////////////////////////////////////////////////////////////
        // file attributes
        /////////////////////////////////////////////////////////////////////

        {
            JPanel fileAttributes = new JPanel();
            fileAttributes.setName("File Attribute");
            fileAttributes.setLayout(new BoxLayout(fileAttributes, BoxLayout.Y_AXIS));
            jtp.add(fileAttributes);

            JPanel file = new JPanel();
            file.setLayout(new BoxLayout(file, BoxLayout.X_AXIS));
            fileAttributes.add(file);

            JTextField fileName = new JTextField();
            file.add(fileName);

            JButton searchFile = new JButton("Search");
            searchFile.addActionListener(e ->
            {
                if (jarEmbedded) {
                    JFileChooser jfc = new JFileChooser(manifest.getName() + "!/");
                    jfc.showOpenDialog(dialog);

                    try {
                        JarFile jar = new JarFile(manifest);

                        ArrayList<JarEntry> entries = new ArrayList<JarEntry>();
                        Enumeration<JarEntry> entrieEnum = jar.entries();

                        while (entrieEnum.hasMoreElements())
                            entries.add(entrieEnum.nextElement());

                    }
                    catch (IOException ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to load files from jar");
                    }

                    JDialog fileDialog = new JDialog(dialog, "Choose File", true);

                    fileDialog.getContentPane().setLayout(new BoxLayout(fileDialog.getContentPane(), BoxLayout.Y_AXIS));

                    JComboBox<File> fileJcb = new JComboBox<File>();

                    fileDialog.setVisible(true);
                }
                else {
                    JFileChooser jfc = new JFileChooser(manifest.getParent());

                    if (jfc.showDialog(dialog, "Select") == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = jfc.getSelectedFile();
                        fileName.setText(selectedFile.getName());
                    }
                }
            });
            file.add(searchFile);

            JTextField attribute = new JTextField();
            fileAttributes.add(attribute);

            JButton add = new JButton("Add Attribute");
            add.addActionListener(e -> {
                if (fileName.getText().length() == 0 || attribute.getText().length() == 0)
                    return;

                frame.add(new FileAttribute(fileName.getText(), attribute.getText()));

                dialog.setVisible(false);
                dialog.dispose();
            });
            fileAttributes.add(add);
        }

        //create dialog
        dialog.setContentPane(mainPanel);
        dialog.setSize(300, 300);
        dialog.setVisible(true);
    }

    /**
     * shows a dialog to select the attribute to remove
     * and remove if one is selected
     */
    public void removeAttribute() {
        JDialog dialog = new JDialog(frame, "Remove Attribute", true);
        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

        JComboBox<ManifestAttribute> attribute = new JComboBox<>(vars.toArray(new ManifestAttribute[vars.size()]));
        dialog.add(attribute);

        JButton remove = new JButton("Remove selected");
        remove.addActionListener(e -> {
            ManifestAttribute selected = (ManifestAttribute) attribute.getSelectedItem();

            if (selected.isMainAttribute()) {
                int continue_ = JOptionPane.showOptionDialog(dialog, "Remove Mainattribute?", "", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null,
                        new String[]{"Remove", "Abort"}, "Abort");

                if (continue_ == 1) {
                    dialog.setVisible(false);
                    dialog.dispose();
                    return;
                }
            }

            vars.remove(selected);
            frame.remove(selected);

            frame.repaint();
        });
        dialog.add(remove);

        dialog.setSize(75, 100);
        dialog.setVisible(true);
    }

    ////////////////////////////////////////////////////////////////////
    // IO
    ////////////////////////////////////////////////////////////////////

    /**
     * commit all changes to the manifestfile
     * <p>
     * jarembedded:
     * creates a new jar and seals all files including
     * the new manifest in it.
     * afterwards replaces the old jar with the new version
     * <p>
     * .mf-file:
     * writes all changes to manifestfile
     */
    public void saveManifest() {
        if (!manifestEdited)
            return;

        vars.forEach(a ->
        {
            try {
                a.updateManifest(mf);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to save attribute " + a.name + " cause: " + e.getMessage());
            }
        });

        if (jarEmbedded) {
            JarFile jar = null;
            JarOutputStream jos = null;
            File njar = new File("temp.jar");

            try {
                jar = new JarFile(manifest);

                jos = new JarOutputStream(new FileOutputStream(njar), mf);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    jos.putNextEntry(entries.nextElement());
                    jos.flush();
                    jos.closeEntry();
                }

                jar.close();
                if (!manifest.delete())
                    throw new IOException("Failed to delete old jar");

                njar = new File("temp.jar");
                if (!njar.renameTo(manifest))
                    throw new IOException("Failed to replace jar");
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(null, null);
            }
            finally {
                if (jar != null)
                    try {
                        jar.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                if (jos != null) {
                    try {
                        jos.flush();
                        jos.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (njar.exists())
                    if (!njar.delete())
                        JOptionPane.showMessageDialog(frame, "Failed to delete tempfile");
            }
        }
        else {
            try {
                FileOutputStream fos = new FileOutputStream(manifest);
                mf.write(fos);
                fos.flush();
                fos.close();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to save manifestfile to: " +
                        manifest.getName() + " cause: " + e.getMessage());
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // manifestattribute
    /////////////////////////////////////////////////////////////////////////

    /**
     * helperclass for manifestattributes
     */
    private class ManifestAttribute
            extends JPanel {
        /**
         * name of the manifestattribute
         */
        protected String name;

        /**
         * textfield containing the value of the manifestattribute
         */
        protected JTextField value;

        /**
         * true, if this attribute is a mainattribute
         */
        protected boolean mainAttribute;

        /**
         * creates a new manifestattribute
         * with the specified name and initial value
         *
         * @param name the name of the attribute
         * @param val  the value of the attribute
         */
        public ManifestAttribute(String name, String val, boolean isMainAttribute) {
            if (isMainAttribute)
                setBackground(Color.orange);
            mainAttribute = isMainAttribute;

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            add(new JLabel(name));
            this.name = name;

            value = new JTextField(val);
            value.setPreferredSize(
                    new Dimension(ManifestHelper.this.frame.getWidth() / 2, (int) getPreferredSize().getHeight()));
            value.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    manifestEdited = true;
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    manifestEdited = true;
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    manifestEdited = true;
                }
            });
            add(value);

            vars.add(this);
        }

        @Override
        public String toString() {
            return name + ": " + value.getText();
        }

        /**
         * writes this attribute to the manifest
         *
         * @param manifest the manifest to update
         * @throws IOException internal exception
         */
        public void updateManifest(Manifest manifest)
                throws IOException {
            if (value.getText().length() == 0)
                return;

            if (mainAttribute) {
                manifest.getMainAttributes().putValue(name, value.getText());
            }
            else {
                manifest.read(new ByteArrayInputStream((name + ": " + value.getText()).getBytes()));
            }
        }

        public boolean isMainAttribute() {
            return mainAttribute;
        }
    }

    /**
     * helperclass for packageattribute
     */
    private class FileAttribute
            extends ManifestAttribute {
        public FileAttribute(String file, String value) {
            super("Name: " + file, value, false);
        }

        public void updateManifest(Manifest manifest)
                throws IOException {
            manifest.read(new ByteArrayInputStream((name + "\n" + value).getBytes()));
        }
    }
}