package UserInterface;

import DynamicBandwidthManagement.GeneratorInterface;

public class UIController implements ControllerInterface {
    GeneratorInterface generator;
    DBM_UI ui;

    private static final boolean ENABLED = true;
    private static final boolean DISABLED = false;
    private static boolean isRealTimeActivated = false;

    public UIController(GeneratorInterface generator){
        this.generator = generator;

        ui = new DBM_UI(this, generator);
        initializeUI();
    }

    private void initializeUI(){
        ui.createControlFrame();
        ui.enableStartMenuItem();

        // Disable some start menu items
        ui.disablePauseMenuItem();
        ui.disableContinueMenuItem();
        ui.disableStopMenuItem();

        // Disable Some Panels
        ui.statusUserAddRemovePanel(DISABLED);
    }

    @Override
    public void setControllerSettingsAndInitialize(){
        generator.setSettings(ui.getG(), ui.getg(), ui.getb(), isRealTimeActivated);
        generator.initialize();
    }

    @Override
    public void startSim() {
        setControllerSettingsAndInitialize();

        // Enable Modules
        ui.statusUserAddRemovePanel(ENABLED);
        ui.enableStopMenuItem();

        // Disable Modules
        ui.statusSettingsPanel(DISABLED);
        ui.disableStartMenuItem();
    }

    @Override
    public void stopSim() {
        ui.clearStatus();

        // Enable Modules
        ui.statusSettingsPanel(ENABLED);
        ui.enableStartMenuItem();

        // Disable Modules
        ui.statusUserAddRemovePanel(DISABLED);
        ui.disableStopMenuItem();
    }

    @Override
    public void pauseSim() {

    }

    @Override
    public void continueSim() {

    }

    @Override
    public void addUser(int groupNumber) {
        generator.userJoin(groupNumber);
    }
    @Override
    public void removeUser(int groupNumber) {
        generator.userLeave(groupNumber);
    }

    @Override
    public void createRandomEvents(int eventsNumber, int eventsRepeatNumber) {
        generator.generateRandomUsers(eventsNumber, eventsRepeatNumber);
    }

    @Override
    public void startRealTimeGraph(){
        isRealTimeActivated = true;
    }
}

