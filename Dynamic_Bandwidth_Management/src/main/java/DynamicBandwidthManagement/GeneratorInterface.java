package DynamicBandwidthManagement;

public interface GeneratorInterface {
    void setSettings(double G, double g, double b, boolean isRealTimeActivated);
    void initialize();
    String stringStatus();

    void generateRandomUser();
    void generateRandomUsers(int userNumber, int repeats);

    void userJoin(int groupNumber);
    void userLeave(int groupNumber);

    double getRemainderBandwidth();
    double getAllocatedBandwidth();

    void registerObserver(UserActivityObserver observer);
    void removeObserver(UserActivityObserver observer);
    void notifyUserActivityObservers();

    void startRealTimeGraph();
}
