package UserInterface;

public interface ControllerInterface {
    void setControllerSettingsAndInitialize();

    void startSim();
    void pauseSim();
    void continueSim();
    void stopSim();

    void addUser(int groupNumber);
    void removeUser(int groupNumber);
    void createRandomEvents(int eventsNumber, int eventsRepeatNumber);

    void startRealTimeGraph();
}
