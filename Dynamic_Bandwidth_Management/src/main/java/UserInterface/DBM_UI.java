package UserInterface;

import DynamicBandwidthManagement.GeneratorInterface;
import DynamicBandwidthManagement.UserActivityObserver;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DBM_UI implements ActionListener, UserActivityObserver {
    private GeneratorInterface generator;
    private ControllerInterface controller;

    private static String DEFAULT_TOTAL_BANDWIDTH = "30";
    private static String DEFAULT_MINIMAL_BANDWIDTH = "1";
    private static String DEFAULT_SLOT_BANDWIDTH = "2";
    private static String DEFAULT_EVENTS_NUMBER = "3000";
    private static String DEFAULT_EVENTS_REPEAT = "1";
    private static String DEFAULT_EVENTS_TIMER = "1";

    private int eventCounter = 0;
    private ArrayList<Integer> signalTrialValues;
    private int signalCounter = 0;
    private int deniedUsers = 0;
    private int trialCounter = 0;

    private JFrame controlFrame;

    // Start Menu
    private JMenuBar menuBar;
    private JMenu simulationMenu;
    private JMenuItem startMenuItem, pauseMenuItem, continueMenuItem, stopMenuItem, exitMenuItem;

    // Scroll Panel
    private JTextArea statusTextArea;
    private JScrollPane scrollPane;

    // User Add/Remove Panel
    private JLabel radioButtonsInfoLabel;
    private JButton createEventsButton, signalGraphButton;
    private JRadioButton addRadioButton, removeRadioButton;
    private JButton button_group0, button_group1, button_group2;

    // Settings Panel
    private JLabel msLabel;
    private JLabel totalBandLabel, minimumBandLabel, slotBandLabel, randomEventsCountLabel, randomEventsRepeatsLabel, randomEventsTimerLabel;
    private JTextField totalBandText, minimumBandText, slotBandText, randomEventsCountText, randomEventsRepeatsText, randomEventsTimerText;
    private JButton settingsApplyButton, defaultSettingsButton;
    private JCheckBox realTimeGraphCheckBox;

    private double G, g, b;
    private int randomEventsCount, randomEventsRepeats, eventTimer;

    public DBM_UI() {}
    public DBM_UI(ControllerInterface controller, GeneratorInterface generator) {
        this.controller = controller;
        this.generator = generator;
        this.generator.registerObserver((UserActivityObserver) this);
        this.signalTrialValues = new ArrayList<Integer>();
    }

    public double getG(){return G;}
    public double getg(){return g;}
    public double getb(){return b;}

    private void setSimulationMenuItems(JMenu menu) {
        startMenuItem = new JMenuItem("Start");
        menu.add(startMenuItem);
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.startSim();
            }
        });


        pauseMenuItem = new JMenuItem("Pause");
        /*
        menu.add(pauseMenuItem);
        pauseMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.pauseSim();
            }
        });
        */

        continueMenuItem = new JMenuItem("Continue");
        /*
        menu.add(continueMenuItem);
        continueMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.continueSim();
            }
        });
        */

        stopMenuItem = new JMenuItem("Stop");
        menu.add(stopMenuItem);
        stopMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.stopSim();
            }
        });

        exitMenuItem = new JMenuItem("Exit");
        menu.add(exitMenuItem);
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
    }
    private void readSettingsValues(){
        G = Double.parseDouble(totalBandText.getText());
        g = Double.parseDouble(minimumBandText.getText());
        b = Double.parseDouble(slotBandText.getText());
        randomEventsCount = Integer.parseInt(randomEventsCountText.getText());
        randomEventsRepeats = Integer.parseInt(randomEventsRepeatsText.getText());
        eventTimer = Integer.parseInt(randomEventsTimerText.getText());

        if(realTimeGraphCheckBox.isSelected()){
            startRealTimeGraph();
        }
    }
    private void setDefaultValues(){
        totalBandText.setText(DEFAULT_TOTAL_BANDWIDTH);
        minimumBandText.setText(DEFAULT_MINIMAL_BANDWIDTH);
        slotBandText.setText(DEFAULT_SLOT_BANDWIDTH);
        randomEventsCountText.setText(DEFAULT_EVENTS_NUMBER);
        randomEventsRepeatsText.setText(DEFAULT_EVENTS_REPEAT);
        randomEventsTimerText.setText(DEFAULT_EVENTS_TIMER);
    }
    private void showSignalGraph(){
        // Create new thread to run the graphing process
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SignalingChartClass.displayChart(trialCounter, eventCounter, signalTrialValues);
            }
        });
        t.start();
    }
    private void startRealTimeGraph(){
        controller.startRealTimeGraph();
    }

    public void createControlFrame() {
        int FRAME_X = 635;
        int FRAME_Y = 720;

        controlFrame = new JFrame("Simulator - Dynamic Bandwidth Management for SDN");
        controlFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        controlFrame.setLocation(15, 15);
        controlFrame.setResizable(true);
        controlFrame.setSize(new Dimension(FRAME_X, FRAME_Y));

        menuBar = new JMenuBar();
        simulationMenu = new JMenu("Simulation");
        setSimulationMenuItems(simulationMenu);
        menuBar.add(simulationMenu);
        controlFrame.setJMenuBar(menuBar);

        JPanel scrollPanel = createScrollablePanel();
        JPanel userAddRemovePanel = createUserAddRemovePanel();
        userAddRemovePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        JPanel settingsPanel = createSettingsPanel();
        settingsPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

        GroupLayout layout = new GroupLayout(controlFrame.getContentPane());
        controlFrame.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(scrollPanel)
            .addGroup(layout.createSequentialGroup()
                .addComponent(userAddRemovePanel)
                .addComponent(settingsPanel)
            )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(scrollPanel)
            .addGroup(layout.createParallelGroup()
                .addComponent(userAddRemovePanel)
                .addComponent(settingsPanel)
            )
        );

        layout.linkSize(SwingConstants.VERTICAL, userAddRemovePanel, settingsPanel);

        controlFrame.setVisible(true);
    }

    // Creating Panels
    private JPanel createScrollablePanel() {
        statusTextArea = new JTextArea(11,57);
        statusTextArea.setEditable(false);
        // Disable Horizontal Scroll Bar
        statusTextArea.setLineWrap(true);

        scrollPane = new JScrollPane(statusTextArea);

        JPanel finalPanel = new JPanel();

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        //layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(scrollPane)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(scrollPane)
        );

        return finalPanel;
    }

    private JPanel createRadioButtonsPanel() {
        JPanel finalPanel = new JPanel();

        radioButtonsInfoLabel = new JLabel("Select an action to perform:", 4);

        addRadioButton = new JRadioButton("Insert a user to:");
        addRadioButton.setForeground(Color.GREEN.darker());

        removeRadioButton = new JRadioButton("Remove a user from:");
        removeRadioButton.setForeground(Color.RED.brighter());

        addRadioButton.setSelected(true);

        ButtonGroup radioButtonsGroup = new ButtonGroup();
        radioButtonsGroup.add(addRadioButton);
        radioButtonsGroup.add(removeRadioButton);

        finalPanel.add(radioButtonsInfoLabel);
        finalPanel.add(addRadioButton);
        finalPanel.add(removeRadioButton);

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        //layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(radioButtonsInfoLabel)
                .addComponent(addRadioButton)
                .addComponent(removeRadioButton)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(radioButtonsInfoLabel)
                .addComponent(addRadioButton)
                .addComponent(removeRadioButton)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, addRadioButton, removeRadioButton);

        return finalPanel;
    }
    private JPanel createAddRemoveGroupButtonsPanel() {
        JPanel finalPanel = new JPanel();

        button_group0 = new JButton("Guest (Group 0)");
        button_group1 = new JButton("BYOD (Group 1)");
        button_group2 = new JButton("Staff (Group 2)");

        button_group0.addActionListener(this);
        button_group1.addActionListener(this);
        button_group2.addActionListener(this);

        finalPanel.add(button_group0);
        finalPanel.add(button_group1);
        finalPanel.add(button_group2);

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        //layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(button_group0)
                .addComponent(button_group1)
                .addComponent(button_group2)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(button_group0)
                .addComponent(button_group1)
                .addComponent(button_group2)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, button_group0, button_group1, button_group2);
        layout.linkSize(SwingConstants.VERTICAL, button_group0, button_group1, button_group2);

        return finalPanel;
    }
    private JPanel createUserAddRemovePanel() {
        JPanel radioButtonsPanel = createRadioButtonsPanel();
        //radioButtonsPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

        JPanel buttonsPanel = createAddRemoveGroupButtonsPanel();
        //buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

        createEventsButton = new JButton("Create Random Events");
        createEventsButton.addActionListener(this);

        signalGraphButton = new JButton("Signaling Graph");
        signalGraphButton.addActionListener(this);

        JPanel finalPanel = new JPanel();

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(radioButtonsPanel)
                    .addComponent(buttonsPanel)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(createEventsButton)
                    .addComponent(signalGraphButton)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(radioButtonsPanel)
                    .addComponent(buttonsPanel)
                )
                .addGroup(layout.createParallelGroup()
                    .addComponent(createEventsButton)
                    .addComponent(signalGraphButton)
                )
        );

        layout.linkSize(SwingConstants.VERTICAL, radioButtonsPanel, buttonsPanel);
        layout.linkSize(SwingConstants.VERTICAL, createEventsButton, signalGraphButton);

        layout.linkSize(SwingConstants.HORIZONTAL, radioButtonsPanel, createEventsButton);
        layout.linkSize(SwingConstants.HORIZONTAL, buttonsPanel, signalGraphButton);


        return finalPanel;
    }

    private JPanel createBandSettingsPanel(){
        JPanel finalPanel = new JPanel();

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        //layout.setAutoCreateContainerGaps(true);

        totalBandLabel = new JLabel("T_Bwidth: ");
        minimumBandLabel = new JLabel("M_Bwidth: ");
        slotBandLabel = new JLabel("S_Bwidth: ");

        totalBandText = new JTextField();
        totalBandText.setMaximumSize(new Dimension(35,20));
        minimumBandText = new JTextField();
        minimumBandText.setMaximumSize(new Dimension(35,20));
        slotBandText = new JTextField();
        slotBandText.setMaximumSize(new Dimension(35,20));

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(totalBandLabel).addGap(0)
                .addComponent(totalBandText).addGap(0)
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(minimumBandLabel).addGap(0)
                .addComponent(minimumBandText).addGap(0)
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(slotBandLabel).addGap(0)
                .addComponent(slotBandText).addGap(0)
            )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(totalBandLabel).addGap(0)
                .addComponent(totalBandText).addGap(0)
            )
            .addGroup(layout.createParallelGroup()
                .addComponent(minimumBandLabel).addGap(0)
                .addComponent(minimumBandText).addGap(0)
            )
            .addGroup(layout.createParallelGroup()
                .addComponent(slotBandLabel).addGap(0)
                .addComponent(slotBandText).addGap(0)
            )
        );

        layout.linkSize(SwingConstants.HORIZONTAL, totalBandLabel, minimumBandLabel, slotBandLabel);

        return finalPanel;
    }
    private JPanel createEventsSettigsPanel(){
        JPanel finalPanel = new JPanel();

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        //layout.setAutoCreateContainerGaps(true);

        randomEventsCountLabel = new JLabel("Events_C: ");
        randomEventsRepeatsLabel = new JLabel("Events_R: ");
        randomEventsTimerLabel = new JLabel("Events_T:");
        msLabel = new JLabel("ms");

        randomEventsCountText = new JTextField();
        randomEventsCountText.setMaximumSize(new Dimension(35,20));
        randomEventsRepeatsText = new JTextField();
        randomEventsRepeatsText.setMaximumSize(new Dimension(35,20));
        randomEventsTimerText = new JTextField();
        randomEventsTimerText.setMaximumSize(new Dimension(35,20));

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(randomEventsCountLabel).addGap(0)
                .addComponent(randomEventsCountText)
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(randomEventsRepeatsLabel).addGap(0)
                .addComponent(randomEventsRepeatsText)
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(randomEventsTimerLabel).addGap(0)
                .addComponent(randomEventsTimerText).addGap(0)
                .addComponent(msLabel)
            )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(randomEventsCountLabel).addGap(0)
                .addComponent(randomEventsCountText)
            )
            .addGroup(layout.createParallelGroup()
                .addComponent(randomEventsRepeatsLabel).addGap(0)
                .addComponent(randomEventsRepeatsText)
            )
            .addGroup(layout.createParallelGroup()
                .addComponent(randomEventsTimerLabel).addGap(0)
                .addComponent(randomEventsTimerText).addGap(0)
                .addComponent(msLabel)
            )
        );

        layout.linkSize(SwingConstants.HORIZONTAL, randomEventsCountLabel, randomEventsRepeatsLabel, randomEventsTimerLabel);

        return finalPanel;
    }
    private JPanel createSettingsPanel(){
        JPanel bandPanel = createBandSettingsPanel();
        //bandPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

        JPanel eventPanel = createEventsSettigsPanel();
        //eventPanel.setBorder(BorderFactory.createLineBorder(Color.RED));

        settingsApplyButton = new JButton("Apply Settings");
        settingsApplyButton.addActionListener(this);
        defaultSettingsButton = new JButton("Default Settings");
        defaultSettingsButton.addActionListener(this);

        realTimeGraphCheckBox = new JCheckBox("Real-Time Bandwidth Graph Simulation");
        realTimeGraphCheckBox.addActionListener(this);

        JPanel finalPanel = new JPanel();

        GroupLayout layout = new GroupLayout(finalPanel);
        finalPanel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(bandPanel)
                .addComponent(eventPanel)
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(settingsApplyButton)
                .addComponent(defaultSettingsButton)
            )
            .addComponent(realTimeGraphCheckBox)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(bandPanel)
                .addComponent(eventPanel)
            )
            .addGroup(layout.createParallelGroup()
                .addComponent(settingsApplyButton)
                .addComponent(defaultSettingsButton)
            )
            .addComponent(realTimeGraphCheckBox)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, bandPanel, eventPanel, settingsApplyButton, defaultSettingsButton);

        return finalPanel;
    }

    // Button Actions
    public void actionPerformed(ActionEvent e) {
        // Button actions
        if (e.getSource() == button_group0) {
            processGroupButtonPress(0);
        } else if (e.getSource() == button_group1) {
            processGroupButtonPress(1);
        } else if (e.getSource() == button_group2) {
            processGroupButtonPress(2);
        } else if (e.getSource() == signalGraphButton) {
            showSignalGraph();
        } else if (e.getSource() == createEventsButton) {
            controller.createRandomEvents(randomEventsCount, randomEventsRepeats);
        } else if (e.getSource() == settingsApplyButton){
            readSettingsValues();
        } else if (e.getSource() == defaultSettingsButton){
            setDefaultValues();
        }
    }
    private void processGroupButtonPress(int groupNumber) {
        if (addRadioButton.isSelected()) {
            controller.addUser(groupNumber);
        } else if (removeRadioButton.isSelected()) {
            controller.removeUser(groupNumber);
        }
    }

    // Scroll Panel Stuff
    public void printStatus(String status){
        statusTextArea.append(status);
    }
    public void clearStatus(){
        statusTextArea.setText(null);
    }

    // Enable/Disable Start Menu Items
    public void enableStartMenuItem(){startMenuItem.setEnabled(true);}
    public void disableStartMenuItem() { startMenuItem.setEnabled(false); }
    public void enablePauseMenuItem() {pauseMenuItem.setEnabled(true);}
    public void disablePauseMenuItem() { pauseMenuItem.setEnabled(false); }
    public void enableContinueMenuItem() {continueMenuItem.setEnabled(true);}
    public void disableContinueMenuItem() { continueMenuItem.setEnabled(false); }
    public void enableStopMenuItem() {stopMenuItem.setEnabled(true);}
    public void disableStopMenuItem() { stopMenuItem.setEnabled(false); }

    // Enable/Disable Panels
    public void statusUserAddRemovePanel(boolean status){
        radioButtonsInfoLabel.setEnabled(status);

        addRadioButton.setEnabled(status);
        removeRadioButton.setEnabled(status);

        button_group0.setEnabled(status);
        button_group1.setEnabled(status);
        button_group2.setEnabled(status);

        signalGraphButton.setEnabled(status);
        createEventsButton.setEnabled(status);
    }
    public void statusSettingsPanel(boolean status){
        totalBandLabel.setEnabled(status);
        minimumBandLabel.setEnabled(status);
        slotBandLabel.setEnabled(status);
        randomEventsCountLabel.setEnabled(status);
        randomEventsRepeatsLabel.setEnabled(status);
        randomEventsTimerLabel.setEnabled(status);
        msLabel.setEnabled(status);

        totalBandText.setEnabled(status);
        minimumBandText.setEnabled(status);
        slotBandText.setEnabled(status);
        randomEventsCountText.setEnabled(status);
        randomEventsRepeatsText.setEnabled(status);
        randomEventsTimerText.setEnabled(status);

        settingsApplyButton.setEnabled(status);
        defaultSettingsButton.setEnabled(status);

        realTimeGraphCheckBox.setEnabled(status);
    }

    @Override
    public void statusInformation(String status, int eventCounter, int signalCounter, int trialCounter, int deniedUsers, boolean trialEnd) {
        //statusTextArea.append(status);
        this.eventCounter = eventCounter;
        this.signalCounter = signalCounter;
        this.trialCounter = trialCounter;
        this.deniedUsers = deniedUsers;

        if(trialEnd){
            signalTrialValues.add(signalCounter);
        }

    }
}
