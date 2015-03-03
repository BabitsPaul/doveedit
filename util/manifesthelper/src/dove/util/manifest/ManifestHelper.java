package dove.util.manifest;

import dove.util.ui.extensibletable.JExtensibleTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.*;
import java.util.stream.Stream;

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
     * the gridbagconstants for the
     * titellabel of the fileattribute component
     */
    private static final GridBagConstraints titleConstraints = new GridBagConstraints();

    /**
     * the gridbagconstants for the
     * namelabel of the fileattribute component
     */
    private static final GridBagConstraints nameLabelConstraints = new GridBagConstraints();

    /**
     * the gridbagconstants for the
     * valuelabel of the fileattribute component
     */
    private static final GridBagConstraints valueLabelConstraints = new GridBagConstraints();

    /**
     * the gridbagconstants for the
     * valuefield of the fileattribute component
     */
    private static final GridBagConstraints valueFieldConstraints = new GridBagConstraints();

    /**
     * the gridbagconstants for the
     * namefield of the fileattribute component
     */
    private static final GridBagConstraints nameFieldConstraints = new GridBagConstraints();

    /**
     * initialize gridbagconstants for the fileattributes
     */
    static {
        titleConstraints.gridx = 0;
        titleConstraints.gridy = 0;
        titleConstraints.gridwidth = 2;
        titleConstraints.gridheight = 1;
        titleConstraints.insets = new Insets(5, 5, 5, 5);
        titleConstraints.anchor = GridBagConstraints.LINE_START;

        nameLabelConstraints.gridx = 0;
        nameLabelConstraints.gridy = 1;
        titleConstraints.gridwidth = 1;
        titleConstraints.gridheight = 1;
        titleConstraints.insets = new Insets(5, 5, 5, 5);
        titleConstraints.anchor = GridBagConstraints.CENTER;

        valueLabelConstraints.gridx = 0;
        valueLabelConstraints.gridy = 2;
        valueLabelConstraints.gridwidth = 1;
        valueLabelConstraints.gridheight = 1;
        valueLabelConstraints.insets = new Insets(5 , 5 , 5 , 5);
        valueLabelConstraints.anchor = GridBagConstraints.CENTER;

        nameFieldConstraints.gridx = 1;
        nameFieldConstraints.gridy = 1;
        nameFieldConstraints.gridwidth = 1;
        nameFieldConstraints.gridheight =  1;
        nameFieldConstraints.anchor = GridBagConstraints.CENTER;

        valueFieldConstraints.gridx = 1;
        valueFieldConstraints.gridy = 2;
        valueFieldConstraints.gridwidth = 1;
        valueFieldConstraints.gridheight = 1;
        valueFieldConstraints.anchor = GridBagConstraints.CENTER;
    }

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
    private JPanel attributePanel;

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

    //////////////////////////////////////////////////////////////////////
    // manifest helpermethods
    //////////////////////////////////////////////////////////////////////

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
        {
            ManifestAttribute attribute = new MainAttribute(e.getKey().toString(), e.getValue().toString());

            attributePanel.add(attribute);
            vars.add(attribute);
        });
        mf.getEntries().entrySet().forEach(e ->
        {
            String file = e.getKey();

            FileAttribute fileAttribute = new FileAttribute(file);

            for (Map.Entry entry : e.getValue().entrySet())
                fileAttribute.addAttribute(entry.getKey().toString(), entry.getValue().toString());

            attributePanel.add(fileAttribute);
            vars.add(fileAttribute);
        });

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

                if (vars.stream().filter(a ->
                        a instanceof MainAttribute && ((MainAttribute) a).name.equals(name.getText())).count() != 0) {
                    JOptionPane.showMessageDialog(dialog, "Attribute " + name.getText() + "already exists");

                    ManifestAttribute attribute = new MainAttribute(name.getText(), value.getText());
                    vars.add(attribute);
                    attributePanel.add(attribute);

                    dialog.setVisible(false);
                    dialog.dispose();
                    return;
                }

                MainAttribute attribute = new MainAttribute(name.getText(), value.getText());

                attributePanel.add(attribute);
                vars.add(attribute);

                frame.revalidate();
                frame.repaint();

                dialog.setVisible(false);
                dialog.dispose();

                manifestEdited = true;
            });
            mainAttributes.add(add, BorderLayout.SOUTH);
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
                    final JComboBox<JarEntry> entrySelection = new JComboBox<>();

                    try {
                        JarFile jar = new JarFile(manifest);

                        ArrayList<JarEntry> entries = new ArrayList<>();
                        Enumeration<JarEntry> entrieEnum = jar.entries();

                        while (entrieEnum.hasMoreElements())
                            entries.add(entrieEnum.nextElement());

                        entrySelection.setModel(new DefaultComboBoxModel<>(entries.toArray(new JarEntry[entries.size()])));
                    }
                    catch (IOException ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to load files from jar");
                        return;
                    }

                    JDialog fileDialog = new JDialog(dialog, "Choose File", true);

                    fileDialog.getContentPane().setLayout(new BoxLayout(fileDialog.getContentPane(), BoxLayout.Y_AXIS));

                    fileDialog.add(entrySelection);

                    JButton selectFile = new JButton("Select File");
                    selectFile.addActionListener(ex ->
                    {
                        if (entrySelection.getSelectedItem() == null)
                            return;

                        fileName.setText(((JarEntry) entrySelection.getSelectedItem()).getName());

                        fileDialog.setVisible(false);
                        fileDialog.dispose();
                    });
                    fileDialog.add(selectFile);

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

            JTextArea attribute = new JTextArea();
            fileAttributes.add(attribute);

            JButton add = new JButton("Add Attribute");
            add.addActionListener(e -> {
                if (fileName.getText().length() == 0 || attribute.getText().length() == 0)
                    return;

                Stream<ManifestAttribute> searchMatching = vars.stream().filter(a ->
                        (a instanceof FileAttribute && ((FileAttribute) a).file.equals(fileName.getText())));
                if (searchMatching.count() != 0) {
                    JOptionPane.showMessageDialog(dialog, "FileAttribute already exists");

                    dialog.setVisible(false);
                    dialog.dispose();
                }


                FileAttribute fattribute = new FileAttribute(fileName.getText());

                if (attribute.getText().length() != 0) {
                    String[] lines = attribute.getText().split("\n");

                    for (int i = 0; i < lines.length; i++) {
                        String[] nameAttrSplit = lines[i].split(": ", 2);

                        if (nameAttrSplit.length != 2)
                            continue;

                        fattribute.addAttribute(nameAttrSplit[0], nameAttrSplit[1]);
                    }
                }

                attributePanel.add(fattribute);
                vars.add(fattribute);
                frame.revalidate();
                frame.repaint();

                dialog.setVisible(false);
                dialog.dispose();

                manifestEdited = true;
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

            if (selected instanceof MainAttribute) {
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

            dialog.setVisible(false);
            dialog.dispose();

            frame.revalidate();
            frame.repaint();

            manifestEdited = true;
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
                a.updateManifest(mf));

        if (jarEmbedded) {
            JarFile jar = null;
            JarOutputStream jos = null;
            File njar = new File("temp.jar").getAbsoluteFile();

            try {
                if (!njar.createNewFile())
                    throw new IOException("Failed to create tempfile");

                jar = new JarFile(manifest);

                jos = new JarOutputStream(new FileOutputStream(njar), mf);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    //ignore manifestfile (replace with new manifest)
                    if (entry.getName().endsWith(".MF"))
                        continue;

                    InputStream is = jar.getInputStream(entry);

                    //jos.putNextEntry(entry);
                    //create a new entry to avoid ZipException: invalid entry compressed size
                    jos.putNextEntry(new JarEntry(entry.getName()));
                    byte[] buffer = new byte[1048];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        jos.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    jos.flush();
                    jos.closeEntry();
                }

                //close jar
                jar.close();
                jar = null;

                //close jaroutputstream
                jos.flush();
                jos.close();
                jos = null;

                if (!manifest.delete())
                    throw new IOException("Failed to delete old jar");

                if (!njar.renameTo(manifest))
                    throw new IOException("Failed to replace jar");

                manifestEdited = false;
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "failed to commit changes - cause: " + e.getMessage());
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

                manifestEdited = false;
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
    private abstract class ManifestAttribute
            extends JPanel {
        /**
         * insert the data related to this attribute into the manifest
         *
         * @param manifest the manifest to update
         * @see java.util.jar.Manifest
         */
        public abstract void updateManifest(Manifest manifest);
    }

    private class MainAttribute
            extends ManifestAttribute {
        /**
         * name of the manifestattribute
         */
        private String name;

        /**
         * textfield containing the value of the manifestattribute
         */
        private JTextField value;

        /**
         * creates a new manifestattribute
         * with the specified name and initial value
         *
         * @param name the name of the attribute
         * @param val  the value of the attribute
         */
        public MainAttribute(String name, String val) {
            setBackground(Color.orange);

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

        /**
         * creates a stringrepresentation of this object
         *
         * @return toString of this object
         */
        @Override
        public String toString() {
            return name + ": " + value.getText();
        }

        /**
         * writes this attribute to the manifest
         *
         * @param manifest the manifest to update
         */
        public void updateManifest(Manifest manifest) {
            if (value.getText().length() == 0)
                return;

            manifest.getMainAttributes().putValue(name, value.getText());
        }
    }

    /**
     * helperclass for package-/fileattributes
     */
    private class FileAttribute
            extends ManifestAttribute
            implements TableModelListener {
        /**
         * the file this package-/fileattribute
         * describes
         */
        private String file;

        /**
         * the attributes related to the file
         */
        private HashMap<String, String> attribMap;

        /**
         * the table of this attribute
         */
        private JExtensibleTable table;

        /**
         * creates a new attribute for the specified file
         *
         * @param file the file to describe
         */
        public FileAttribute(String file) {
            this.file = file;

            attribMap = new HashMap<>();

            setLayout(new BorderLayout());

            ////////////////////////////////////////////////////////////////////
            // filePanel
            ////////////////////////////////////////////////////////////////////

            JPanel filePanel = new JPanel();
            filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));

            filePanel.add(new JLabel("File: "));

            JTextField nameField = new JTextField();
            nameField.setText(file);
            filePanel.add(nameField);
            nameField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    setFile(nameField.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    setFile(nameField.getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    setFile(nameField.getText());
                }
            });

            add(filePanel, BorderLayout.NORTH);

            //////////////////////////////////////////////////////////////////////
            // attributpanel
            //////////////////////////////////////////////////////////////////////

            String[] header = new String[]{"Name", "Value"};

            Object[][] attributes = new Object[attribMap.size()][2];

            int count = 0;
            for (Map.Entry<String, String> entry : attribMap.entrySet()) {
                attributes[count][0] = entry.getKey();
                attributes[count][1] = entry.getValue();

                count++;
            }

            table = new JExtensibleTable(attributes, header);
            table.getModel().addRow(0, new String[]{"No entries available", ""}, new boolean[]{false, false});

            JPanel attributePanel = new JPanel();
            attributePanel.setLayout(new BorderLayout());

            attributePanel.add(table, BorderLayout.CENTER);
            attributePanel.add(table.getTableHeader(), BorderLayout.NORTH);

            add(attributePanel, BorderLayout.CENTER);

            ///////////////////////////////////////////////////////////////////////
            // popupmenu
            ///////////////////////////////////////////////////////////////////////

            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem addAttribute = new JMenuItem("Add Attribute");
            addAttribute.addActionListener(e ->
            {
                JPanel attrPanel = new JPanel();
                attrPanel.setLayout(new GridBagLayout());

                JTextField attrName = new JTextField();
                JTextField attrVal = new JTextField();

                attrPanel.add(new JLabel("Add Attribute"), titleConstraints);
                attrPanel.add(new JLabel("Name: "), nameLabelConstraints);
                attrPanel.add(new JLabel("Value: "), valueLabelConstraints);
                attrPanel.add(attrName, nameFieldConstraints);
                attrPanel.add(attrVal, valueFieldConstraints);

                if (JOptionPane.showOptionDialog(frame, attrPanel, "Enter a new Attribute", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new String[]{"Add", "Cancel"}, "Add") == 0) {
                    String[] nRow = new String[]{attrName.getText(), attrVal.getText()};
                    table.getModel().addRow(-1, nRow, new boolean[]{false, true});
                    attribMap.put(attrName.getText(), attrVal.getText());

                    revalidate();
                    repaint();
                }
            });
            popupMenu.add(addAttribute);

            JMenuItem removeAttribute = new JMenuItem("Remove Attribute");
            removeAttribute.addActionListener(e ->
            {
                JPanel removePanel = new JPanel();
                removePanel.setLayout(new BoxLayout(removePanel, BoxLayout.Y_AXIS));

                JComboBox<String> jcb = new JComboBox<>(attribMap.keySet().toArray(new String[attribMap.size()]));
                removePanel.add(jcb);

                JButton removeBtn = new JButton("Remove selected attribute");
                removeBtn.addActionListener(e1 -> {
                    //noinspection SuspiciousMethodCalls
                    attribMap.remove(jcb.getSelectedItem());

                    Object[][] data = table.getModel().getData();
                    int index = 0;
                    for (; index < data.length; index++)
                        if (data[index][0].equals(jcb.getSelectedItem()))
                            break;
                    table.getModel().removeRow(index);

                    revalidate();
                    repaint();
                });
                removePanel.add(removeBtn);
            });
            popupMenu.add(removeAttribute);

            table.setComponentPopupMenu(popupMenu);
        }

        /**
         * updates the filename
         *
         * @param nfile the new filename
         */
        private void setFile(String nfile) {
            this.file = nfile;
        }

        /**
         * adds/updates a new attribute to this fileattribute
         *
         * @param name  the name of the attribute
         * @param value the value of the attribute
         */
        public void addAttribute(String name, String value) {
            attribMap.put(name, value);
        }


        @Override
        public String toString() {
            return "Name: " + file;
        }

        /**
         * adds the attributes to the manifest
         *
         * @param manifest the manifest to update
         */
        public void updateManifest(Manifest manifest) {
            Attributes attributes = new Attributes();

            for (Map.Entry<String, String> entry : attribMap.entrySet()) {
                Attributes.Name name = new Attributes.Name(entry.getKey());
                attributes.put(name, entry.getValue());
            }

            manifest.getEntries().put(file, attributes);
        }

        /**
         * listen for updates/inserts of the table
         * and commit them to the attributeMap
         *
         * @param e a tablemodelevent
         */
        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.INSERT ||
                    e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();

                String name = (String) table.getModel().getData()[row][0];
                String value = (String) table.getModel().getData()[row][1];

                attribMap.put(name, value);
            }
        }
    }
}