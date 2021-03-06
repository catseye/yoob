/*
 * The yoob ContentPane.  Contains all the GUI controls for yoob.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.List;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.security.AccessControlException;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;

import java.applet.AppletContext;
import java.net.URL;
import java.net.MalformedURLException;


class LanguageMenuItemActionListener implements ActionListener {
    private RunThread rt;
    private String name;

    public LanguageMenuItemActionListener(RunThread rt, String name) {
        this.rt = rt;
        this.name = name;
    }

    public void actionPerformed(ActionEvent e) {
        rt.load(name);
    } 
}

class ExampleMenuItemActionListener implements ActionListener {
    private ContentPane cp;
    private int index;

    public ExampleMenuItemActionListener(ContentPane cp, int index) {
        this.cp = cp;
        this.index = index;
    }

    public void actionPerformed(ActionEvent e) {
        cp.loadExampleProgram(index);
    }
}

class OptionMenuItemActionListener implements ActionListener {
    private ContentPane cp;
    private String name;

    public OptionMenuItemActionListener(ContentPane cp, String name) {
        this.cp = cp;
        this.name = name;
    }

    public void actionPerformed(ActionEvent e) {
        cp.setOption(name,
          ((AbstractButton)e.getSource()).getModel().isSelected()
        );
    }
}

class RunSpeedSliderChangeListener implements ChangeListener {
    private RunThread rt;

    public RunSpeedSliderChangeListener (RunThread rt) {
        this.rt = rt;
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        rt.setRunSpeed(source.getValue());
    }
}

public class ContentPane extends JPanel {
    private String title;
    private Language language = null;
    private State currentState = null, initialState = null;

    private JMenuBar menuBar;
    private JMenu languageMenu, examplesMenu, optionsMenu, viewMenu;
    private JMenuItem gridLinesMenuItem, zoomInMenuItem, zoomOutMenuItem;
    private PlayfieldDepiction[] pd = {null, null, null, null, null};
    private TapeDepiction[] td = {null, null, null, null, null};
    private JLabel statusBar;
    private Font editFont;
    private ProgramTextArea progBox;
    private InputTextArea inputBox;
    private JTextArea outputBox, editBox, aboutBox;
    private JScrollPane editScrollPane, aboutScrollPane;
    private JPanel interiorPanel, aboutPanel, editPanel;
    private JComponent languageComponent = null;
    private JLabel welcomeLabel;
    private JPanel buttonPanel;
    private final JApplet plet;

    private JButton buttonReset, buttonStop, buttonEdit;
    private JButton buttonStep, buttonWalk, buttonRun;
    private JButton buttonAbout;
    private JButton buttonEsowiki;
    private JSlider sliderRunSpeed;

    static final int HZ_MIN = 0;
    static final int HZ_MAX = 30;
    static final int HZ_INIT = 5;    // initial cycles per second

    private RunThread runThread = null;

    private TextAreasWorld world = null;

    private double zoom = 1.0;
    private boolean gridLines = false;

    public ContentPane(EsolangLoader loader, JApplet applet, String selectedLanguage) {
        super(new BorderLayout());

        title = "Yoob";
        this.plet = applet; // can be null

        runThread = new RunThread(this, HZ_INIT, loader);
        runThread.start();

        //---------- the menus -----------
        menuBar = new JMenuBar();

        languageMenu = new JMenu("Language");
        languageMenu.setMnemonic(KeyEvent.VK_G);
        languageMenu.getAccessibleContext().setAccessibleDescription("The menu containing language selection");
        menuBar.add(languageMenu);

        ButtonGroup group = new ButtonGroup();

        for (String name : loader.getNames()) {
            JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(name);
            group.add(rbMenuItem);
            languageMenu.add(rbMenuItem);
            rbMenuItem.addActionListener(new LanguageMenuItemActionListener(runThread, name));
        }

        examplesMenu = new JMenu("Examples");
        examplesMenu.setMnemonic(KeyEvent.VK_X);
        examplesMenu.getAccessibleContext().setAccessibleDescription("The menu containing selection of example programs");
        menuBar.add(examplesMenu);
        examplesMenu.setVisible(false);

        optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);
        optionsMenu.getAccessibleContext().setAccessibleDescription("The menu containing options that configure language behaviour");
        menuBar.add(optionsMenu);
        optionsMenu.setVisible(false);

        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.getAccessibleContext().setAccessibleDescription("The menu of ways to alter how the program is displayed");
        menuBar.add(viewMenu);
        viewMenu.setVisible(false);

        gridLinesMenuItem = new JCheckBoxMenuItem("Grid Lines");
        viewMenu.add(gridLinesMenuItem);
        gridLinesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gridLines = ((AbstractButton)e.getSource()).getModel().isSelected();
                refreshDepictions();
            } 
        });

        zoomInMenuItem = new JMenuItem("Zoom In");
        viewMenu.add(zoomInMenuItem);
        zoomInMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoom += 0.25;
                refreshDepictions();
            } 
        });

        zoomOutMenuItem = new JMenuItem("Zoom Out");
        viewMenu.add(zoomOutMenuItem);
        zoomOutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (zoom > 0.25) {
                    zoom -= 0.25;
                    refreshDepictions();
                }
            } 
        });

        //---------- the toolbar -----------
        buttonReset = new JButton("Reset");
        buttonReset.setMnemonic(KeyEvent.VK_T);
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        buttonReset.setToolTipText("Reset to the initial state of the program.");
        buttonReset.setEnabled(false);

        buttonStep = new JButton("Step");
        buttonStep.setMnemonic(KeyEvent.VK_S);
        buttonStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                step();
            } 
        });
        buttonStep.setToolTipText("Execute one step of the program.");
        buttonStep.setEnabled(false);

        buttonWalk = new JButton("Walk");
        buttonWalk.setMnemonic(KeyEvent.VK_W);
        buttonWalk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                walk();
            }
        });
        buttonWalk.setToolTipText("Walk through (animate the execution of) the program.");
        buttonWalk.setEnabled(false);

        buttonRun = new JButton("Run");
        buttonRun.setMnemonic(KeyEvent.VK_R);
        buttonRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });
        buttonRun.setToolTipText("Run the program (full speed, no animation.)");
        buttonRun.setEnabled(false);

        buttonStop = new JButton("Stop");
        buttonStop.setMnemonic(KeyEvent.VK_P);
        buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        buttonStop.setToolTipText("Stop the program.");
        buttonStop.setEnabled(false);

        buttonAbout = new JButton("About...");
        buttonAbout.setMnemonic(KeyEvent.VK_A);
        buttonAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutBox();
            } 
        });
        buttonAbout.setToolTipText("Obtain more information about the current language.");
        buttonAbout.setEnabled(false);

        sliderRunSpeed = new JSlider(JSlider.HORIZONTAL, HZ_MIN, HZ_MAX, HZ_INIT);
        sliderRunSpeed.setMajorTickSpacing(10);
        sliderRunSpeed.setMinorTickSpacing(1);
        sliderRunSpeed.setPaintTicks(true);
        sliderRunSpeed.setPaintLabels(true);
        sliderRunSpeed.addChangeListener(new RunSpeedSliderChangeListener(runThread));
        //sliderRunSpeed.setEnabled(false);

        buttonEdit = new JButton("Edit");
        buttonEdit.setMnemonic(KeyEvent.VK_E);
        buttonEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEditor();
            } 
        });
        buttonEdit.setToolTipText("Edit a textual representation of the initial state of the program.");
        buttonEdit.setEnabled(false);

        //--------- the welcome message -------
        welcomeLabel = new JLabel("<html><font size=+2>Welcome to Yoob!</font><br><br>Select a language from the <i>Language</i> menu,<br>" +
                                  "an example from the <i>Examples</i> menu,<br>and click <i>Run</i>.</html>");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setVerticalAlignment(JLabel.CENTER);

        //---- the (initially hidden) editor -----
        editFont = new Font("Monospaced", Font.PLAIN, 12);
        editBox = new JTextArea();
        editBox.setFont(editFont);
        editScrollPane = new JScrollPane(editBox);

        editPanel = new JPanel(new BorderLayout());
        JButton buttonDoneEditing = new JButton("Done");
        buttonDoneEditing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);

                initialState = language.importFromText(editBox.getText());
                applyCurrentlySelectedOptions(initialState);
                currentState = initialState.clone();

                interiorPanel.remove(editPanel);
                interiorPanel.add(buttonPanel, BorderLayout.PAGE_START);
                if (languageComponent != null) {
                    interiorPanel.add(languageComponent, BorderLayout.CENTER);
                }
                languageMenu.setEnabled(true);
                examplesMenu.setEnabled(true);
                optionsMenu.setEnabled(true);
                viewMenu.setEnabled(true);
                refreshDepictions();
                setVisible(true);
            } 
        });
        buttonDoneEditing.setToolTipText("Commit changes to this program and return to yoob.");
        buttonDoneEditing.setEnabled(true);

        JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        editButtonPanel.add(buttonDoneEditing);
        editPanel.add(editButtonPanel, BorderLayout.PAGE_START);
        editPanel.add(editScrollPane, BorderLayout.CENTER);

        //---------- the panels -----------
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        buttonPanel.add(buttonEdit);
        buttonPanel.add(buttonReset);
        buttonPanel.add(buttonStep);
        buttonPanel.add(buttonWalk);
        buttonPanel.add(buttonRun);
        buttonPanel.add(buttonStop);
        buttonPanel.add(sliderRunSpeed);
        buttonPanel.add(buttonAbout);

        interiorPanel = new JPanel(new BorderLayout());
        interiorPanel.add(buttonPanel, BorderLayout.PAGE_START);
        interiorPanel.add(welcomeLabel, BorderLayout.CENTER);

        aboutPanel = new JPanel(new BorderLayout());
        buttonEsowiki = new JButton("Esowiki article...");
        buttonEsowiki.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    AppletContext a = plet.getAppletContext();
                    URL url = new URL("http://www.esolangs.org/wiki/" + language.getName());
                    a.showDocument(url, "_blank");
                } catch (MalformedURLException exc) {
                    // nothing
                }
            } 
        });
        buttonEsowiki.setToolTipText("Pop up the Esowiki article for this language in a new browser window or tab.");
        buttonEsowiki.setEnabled(true);
        JButton buttonDone = new JButton("Done");
        buttonDone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                interiorPanel.remove(aboutPanel);
                interiorPanel.add(buttonPanel, BorderLayout.PAGE_START);
                if (languageComponent != null) {
                    interiorPanel.add(languageComponent, BorderLayout.CENTER);
                }
                languageMenu.setEnabled(true);
                examplesMenu.setEnabled(true);
                optionsMenu.setEnabled(true);
                viewMenu.setEnabled(true);
                refreshDepictions();
                setVisible(true);
            } 
        });
        buttonDone.setToolTipText("Close this message and return to yoob.");
        buttonDone.setEnabled(true);

        JPanel aboutButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        aboutButtonPanel.add(buttonDone);
        if (plet != null) aboutButtonPanel.add(buttonEsowiki);
        aboutPanel.add(aboutButtonPanel, BorderLayout.PAGE_START);

        aboutBox = new JTextArea();
        aboutBox.setEditable(false);
        aboutBox.setLineWrap(true);
        aboutScrollPane = new JScrollPane(aboutBox);
        aboutPanel.add(aboutScrollPane, BorderLayout.CENTER);
        aboutPanel.setVisible(false);

        statusBar = new JLabel("Ready.");

        setPreferredSize(new Dimension(640, 400));
        add(menuBar, BorderLayout.PAGE_START);
        add(interiorPanel, BorderLayout.CENTER);
        //add(statusBar, BorderLayout.PAGE_END);

        if (selectedLanguage != null) {
            State state = loader.getState(selectedLanguage);
            if (state != null) { // TODO and display an error
                selectLanguage(state.getLanguage());
            }
        }

        setOpaque(true);
        setVisible(true);
    }

    protected void selectLanguage(Language language) {
        this.language = language;
        currentState = initialState = null;

        world = new TextAreasWorld();

        interiorPanel.remove(welcomeLabel);

        this.setVisible(false);
        if (languageComponent != null) {
            interiorPanel.remove(languageComponent);
        }
        languageComponent = null;

        //---------- the playfield(s) -----------
        if (language.numPlayfields() > 0) {
            JComponent pfComponent = null;
            for (int pfNum = 0; pfNum < language.numPlayfields(); pfNum++) {
                pd[pfNum] = new PlayfieldDepiction();
                pd[pfNum].setBackground(Color.yellow);
                pd[pfNum].setMinimumSize(new Dimension(100, 50));
                JScrollPane playfieldScrollPane = new JScrollPane(pd[pfNum]);
                if (pfComponent == null) {
                    pfComponent = playfieldScrollPane;
                } else {
                    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                        pfComponent, playfieldScrollPane);
                    splitPane.setOneTouchExpandable(true);
                    splitPane.setDividerLocation(-1);
                    splitPane.setResizeWeight(1.0 / language.numPlayfields());
                    pfComponent = splitPane;
                }
            }
            languageComponent = pfComponent;
        }

        //---------- the program text -----------
        if (language.hasProgramText()) {
            progBox = new ProgramTextArea();
            progBox.setFocusable(false);
            progBox.setEditable(false);
            progBox.setFont(editFont);
            JScrollPane progScrollPane = new JScrollPane(progBox);
            progScrollPane.setPreferredSize(new Dimension(640, 100));

            if (languageComponent == null) {
                languageComponent = progScrollPane;
            } else {
                JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    progScrollPane, languageComponent);
                splitPane.setOneTouchExpandable(true);
                splitPane.setDividerLocation(-1);
                splitPane.setResizeWeight(0.0); // the bottom is what gets resized
                languageComponent = splitPane;
            }
        }

        //---------- input and output -----------
        if (language.hasInput() || language.hasOutput()) {
            inputBox = new InputTextArea();
            JScrollPane inputScrollPane = new JScrollPane(inputBox);
            inputScrollPane.setPreferredSize(new Dimension(640, 100));

            outputBox = new JTextArea();
            outputBox.setEditable(false);
            JScrollPane outputScrollPane = new JScrollPane(outputBox);
            outputScrollPane.setPreferredSize(new Dimension(640, 100));

            JSplitPane ioSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                inputBox, outputBox);
            ioSplitPane.setOneTouchExpandable(true);
            ioSplitPane.setDividerLocation(-1);
            ioSplitPane.setResizeWeight(0.5);

            world.setInputTextArea(inputBox);
            world.setOutputTextArea(outputBox);
 
            if (languageComponent == null) {
                languageComponent = ioSplitPane;
            } else {
                JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    languageComponent, ioSplitPane);
                splitPane.setOneTouchExpandable(true);
                splitPane.setDividerLocation(-1);
                splitPane.setResizeWeight(1.0); // the top is what gets resized
                languageComponent = splitPane;
            }
        }

        //---------- the tape(s) -----------
        if (language.numTapes() > 0) {
            JComponent tapesComponent = null;
            for (int tapeNum = 0; tapeNum < language.numTapes(); tapeNum++) {
                td[tapeNum] = new TapeDepiction();
                td[tapeNum].setBackground(Color.blue);
                td[tapeNum].setMinimumSize(new Dimension(100, 50));

                JScrollPane tapeScrollPane = new JScrollPane(td[tapeNum]);
                tapeScrollPane.setPreferredSize(new Dimension(200, 100));

                if (tapesComponent == null) {
                    tapesComponent = tapeScrollPane;
                } else {
                    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        tapesComponent, tapeScrollPane);
                    splitPane.setOneTouchExpandable(true);
                    splitPane.setDividerLocation(-1);
                    splitPane.setResizeWeight(1.0 / language.numTapes());
                    tapesComponent = splitPane;
                }
            }
            if (languageComponent == null) {
                languageComponent = tapesComponent;
            } else {
                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    languageComponent, tapesComponent);
                splitPane.setOneTouchExpandable(true);
                splitPane.setDividerLocation(-1);
                splitPane.setResizeWeight(1.0); // the left is what gets resized
                languageComponent = splitPane;
            }
        }

        interiorPanel.add(languageComponent, BorderLayout.CENTER);

        List<String> exampleNames = language.exampleProgramNames();
        examplesMenu.setVisible(exampleNames.size() > 0);
        examplesMenu.removeAll();
        int index = 0;
        for (String name : exampleNames) {
            JMenuItem mi = new JMenuItem(name);
            examplesMenu.add(mi);
            mi.addActionListener(new ExampleMenuItemActionListener(this, index));
            index++;
        }

        List<String> optionNames = language.getAvailableOptionNames();
        optionsMenu.setVisible(optionNames.size() > 0);
        optionsMenu.removeAll();
        for (String name : optionNames) {
            JMenuItem mi = new JCheckBoxMenuItem(name);
            optionsMenu.add(mi);
            mi.addActionListener(new OptionMenuItemActionListener(this, name));
        }
        
        viewMenu.setVisible(true);

        // We just selected a language, but we don't have a program yet
        buttonReset.setEnabled(false);
        buttonStep.setEnabled(false);
        buttonWalk.setEnabled(false);
        buttonRun.setEnabled(false);
        buttonEdit.setEnabled(false);
        buttonAbout.setText("About " + language.getName() + "...");
        buttonAbout.setEnabled(true);

        refreshDepictions();
        this.setVisible(true);
    }

    protected void refreshDepictions() {
        if (currentState == null) return;
  
        if (language.hasProgramText()) {
            progBox.setText(currentState.getProgramText());
            int pos = currentState.getProgramPosition();
            progBox.highlightPosition(pos);
        }

        for (int pfNum = 0; pfNum <= language.numPlayfields(); pfNum++) {
            if (pd[pfNum] != null) {
                pd[pfNum].setPlayfield(currentState.getPlayfield(pfNum));
                pd[pfNum].setView(currentState.getPlayfieldView(pfNum));
                pd[pfNum].setZoom(zoom);
                pd[pfNum].setGridLines(gridLines);
                pd[pfNum].resize();
            }
        }

        for (int tapeNum = 0; tapeNum <= language.numTapes(); tapeNum++) {
            if (td[tapeNum] != null) {
                td[tapeNum].setTape(currentState.getTape(tapeNum));
                td[tapeNum].setView(currentState.getTapeView(tapeNum));
                td[tapeNum].setZoom(zoom);
                td[tapeNum].setGridLines(gridLines);
                td[tapeNum].resize();
            }
        }
    }

    protected void walk() {
        disableButtonsPerRun();
        runThread.setTurbo(false);
        runThread.proceed();
    }

    protected void run() {
        disableButtonsPerRun();
        runThread.setTurbo(true);
        runThread.proceed();
    }

    protected void disableButtonsPerRun() {
        languageMenu.setEnabled(false);
        examplesMenu.setEnabled(false);
        optionsMenu.setEnabled(false);
        viewMenu.setEnabled(false);
        buttonEdit.setEnabled(false);
        buttonReset.setEnabled(false);
        buttonStep.setEnabled(false);
        buttonWalk.setEnabled(false);
        buttonRun.setEnabled(false);
        buttonAbout.setEnabled(false);
        buttonStop.setEnabled(true);
    }

    protected void stop() {
        languageMenu.setEnabled(true);
        examplesMenu.setEnabled(true);
        optionsMenu.setEnabled(true);
        viewMenu.setEnabled(true);
        buttonEdit.setEnabled(true);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(true);
        buttonAbout.setEnabled(true);
        if (!currentState.hasHalted()) {
            buttonStep.setEnabled(true);
            buttonWalk.setEnabled(true);
            buttonRun.setEnabled(true);
        }
        runThread.halt();
    }

    /*
     * Returns true iff we are OK to call step again on this
     * state in the future.
     */
    protected boolean step() {
        if (currentState.hasHalted()) {
            return false;
        }
        currentState.step(world);
        refreshDepictions();
        if (currentState.hasHalted()) {
            buttonStep.setEnabled(false);
            buttonWalk.setEnabled(false);
            buttonRun.setEnabled(false);
        }
        if (currentState.needsInput()) {
            if (inputBox != null) {
                inputBox.setBackground(Color.red);
            }
        }
        return (!currentState.hasHalted());
    }

    protected void reset() {
        currentState = initialState.clone();
        buttonEdit.setEnabled(true);
        buttonWalk.setEnabled(true);
        buttonRun.setEnabled(true);
        buttonStep.setEnabled(true);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(true);
        if (inputBox != null) {
            inputBox.setText("");
            inputBox.setBackground(Color.white);
        }
        if (outputBox != null) {
            outputBox.setText("");
        }
        refreshDepictions();
    }

    static String oopsMessage =
        "Could not load example program.  This is likely due to your Java Runtime's policy preventing it from\n" +
        "fetching the example program from a remote URL.  To allow this, add the following section to\n" +
        "your java.policy file:\n" +
        "\n" +
        "    grant {\n" +
        "        permission java.lang.RuntimePermission \"getProtectionDomain\";\n" +
        "    };\n" +
        "\n" +
        "On Ubuntu, when using the OpenJDK, this file is located at /usr/lib/jvm/java-6-openjdk/jre/lib/security/java.policy.\n" +
        "\n" +
        "You will still be asked if you want to allow the applet to connect to the remote site, so this\n" +
        "change will have no significant impact on the security of your Java installation.  For more\n" +
        "information on Java Security Policies, see:\n" +
        "\n" +
        "    http://java.sun.com/developer/onlineTraining/Programming/JDCBook/appA.html";

    protected void loadExampleProgram(int index) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            initialState = language.loadExampleProgram(index);
            applyCurrentlySelectedOptions(initialState);
            reset();
        } catch (AccessControlException e) {
            System.out.println(e);
            if (e.toString().indexOf("getProtectionDomain") > 0) {
                displayModalMessage(oopsMessage, false);
            }
            // XXX otherwise display the exception itself?
        }
        setCursor(null);
    }

    protected void showAboutBox() {
        String properties[][] = language.getProperties();
        String x = "";
        for (int i = 0; i < properties.length; i++) {
            String[] pair = properties[i];
            x += pair[0] + "\n" + pair[1] + "\n\n";
        }
        displayModalMessage(x, true);
    }

    protected void showEditor() {
        this.setVisible(false);
        interiorPanel.remove(buttonPanel);
        if (languageComponent != null) {
            interiorPanel.remove(languageComponent);
        }
        interiorPanel.add(editPanel, BorderLayout.CENTER);
        editPanel.setVisible(true);
        editBox.setText(initialState.exportToText());
        languageMenu.setEnabled(false);
        examplesMenu.setEnabled(false);
        optionsMenu.setEnabled(false);
        viewMenu.setEnabled(false);
        this.setVisible(true);
    }

    protected void displayModalMessage(String message, boolean showEsowikiButton) {
        this.setVisible(false);
        interiorPanel.remove(buttonPanel);
        if (languageComponent != null) {
            interiorPanel.remove(languageComponent);
        }
        interiorPanel.add(aboutPanel, BorderLayout.CENTER);
        aboutPanel.setVisible(true);
        buttonEsowiki.setVisible(showEsowikiButton);
        aboutBox.setText(message);
        languageMenu.setEnabled(false);
        examplesMenu.setEnabled(false);
        optionsMenu.setEnabled(false);
        viewMenu.setEnabled(false);
        this.setVisible(true);
    }

    protected void setOption(String name, boolean value) {
        if (initialState != null)
            initialState.setOption(name, value);
        if (currentState != null)
            currentState.setOption(name, value);
    }

    private void applyCurrentlySelectedOptions(State s) {
        int numOptions = optionsMenu.getItemCount();
        for (int i = 0; i < numOptions; i++) {
            AbstractButton x = (AbstractButton)optionsMenu.getItem(i);
            s.setOption(x.getText(), x.getModel().isSelected());
        }
    }

    public String getTitle() {
        return title;
    }
}
