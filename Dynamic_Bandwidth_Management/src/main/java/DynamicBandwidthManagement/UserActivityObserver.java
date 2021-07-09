package DynamicBandwidthManagement;

public interface UserActivityObserver {
    void statusInformation(String status, int eventCounter, int signalCounter, int trialCounter, int deniedUsers, boolean trialEnd);
}
