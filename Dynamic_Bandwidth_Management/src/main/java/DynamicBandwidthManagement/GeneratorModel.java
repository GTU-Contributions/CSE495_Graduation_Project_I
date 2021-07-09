package DynamicBandwidthManagement;

import UserInterface.BandwidthChartClass;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneratorModel implements GeneratorInterface {
    private final boolean DEBUG = false;
    // Number of the groups
    private final int NUMBER_OF_GROUPS = 3;
    private int lastGeneratedGroup = 0;
    private int lastEventAction = 0;
    private int eventCounter = 0;
    private int signalCounter = 0;
    private int trialCounter = 0;
    private int deniedUsers = 0;
    private StringBuilder lastEventMessage;
    private boolean trialEnd = false;

    private int ThreadRepeats;
    private int ThreadNum;

    // Observers array
    ArrayList<UserActivityObserver> activityObservers;
    // Groups of users
    private ArrayList<Group> groupsOfUsers;
    // Total available bandwidth
    private double G;
    // Minimal guaranteed bandwidth unit to a user of any group
    private double g;
    // Default base of guaranteed bandwidth units
    private double b;

    // Band averages for graph
    Thread bandGraphThread;
    protected ArrayList<Double> group0BandAverage, group1BandAverage, group2BandAverage;
    private boolean isRealTimeStarted = false;

    public GeneratorModel(){
        activityObservers = new ArrayList<UserActivityObserver>();
        groupsOfUsers = new ArrayList<Group>();
        lastEventMessage = new StringBuilder();

        group0BandAverage = new ArrayList<Double>();
        group1BandAverage = new ArrayList<Double>();
        group2BandAverage = new ArrayList<Double>();
    }

    private String getLastActionPerformed(){
        StringBuilder action = new StringBuilder();

        if(lastEventAction == 0){
            action.append("A user is joining to Guest (Group 0)");
        } else if(lastEventAction == 1){
            action.append("A user is joining to BYOD (Group 1)");
        } else if(lastEventAction == 2){
            action.append("A user is joining to Staff (Group 2)");
        } else if(lastEventAction == 3){
            action.append("A user is leaving from Guest (Group 0)");
        } else if(lastEventAction == 4){
            action.append("A user is leaving from BYOD (Group 1)");
        } else if(lastEventAction == 5){
            action.append("A user is leaving from Staff (Group 2)");
        }

        return action.toString();
    }
    private boolean isCriteria1Broken(){

        for(int i=0; i<groupsOfUsers.size()-1; ++i){
            if(groupsOfUsers.get(i).getAverageBandwidth() > groupsOfUsers.get(i+1).getAverageBandwidth()){
                if(DEBUG){
                    System.out.print("Criteria1_B_DATA");
                    System.out.print(", [G]: " + groupsOfUsers.get(0).getAverageBandwidth());
                    System.out.print(", [B]: " + groupsOfUsers.get(1).getAverageBandwidth());
                    System.out.println(", [S]: " + groupsOfUsers.get(2).getAverageBandwidth());
                }
                return true;
            }
        }

        return false;
    }
    private boolean isCriteria2Broken(){

        if(groupsOfUsers.get(0).getAverageBandwidth() < g)
            if(DEBUG) System.out.println("Criteria2_B_DATA" + ", AverageBandOfLowest: " + groupsOfUsers.get(0).getAverageBandwidth() + ", g: " + g);

        return groupsOfUsers.get(0).getAverageBandwidth() < g;
    }
    private boolean isCriteria3Broken(){
        double totalGrantedBandwidth = 0.0;

        for(Group currentGroup : groupsOfUsers)
            totalGrantedBandwidth += currentGroup.getTotalBandwidth();

        if(totalGrantedBandwidth > G)
            if(DEBUG) System.out.println("Criteria3_B_DATA" + ", TotalGrantedBandwidth: " + totalGrantedBandwidth + ", G: " + G);

        return totalGrantedBandwidth > G;
    }
    private boolean areAllCriteriaMet(){

        if(DEBUG) System.out.println("AllCriteriaCheckStart");

        if(isCriteria1Broken())
            return false;

        if(DEBUG) System.out.println("Criteria 1 met");

        if(isCriteria2Broken())
            return false;

        if(DEBUG) System.out.println("Criteria 2 met");

        if(isCriteria3Broken())
            return false;

        if(DEBUG) System.out.println("Criteria 3 met");

        return true;
    }

    public void addGroup(Group group){
        groupsOfUsers.add(group);
    }
    public ArrayList<Group> getGroups(){ return groupsOfUsers; }

    public long getSignalCounter(){ return signalCounter; }
    public void resetSignalCounter() { this.signalCounter = 0; }

    public void setSettings(double G, double g, double b, boolean isRealTimeStarted){
        this.G = G;
        this.g = g;
        this.b = b;
        this.isRealTimeStarted = isRealTimeStarted;

        if(isRealTimeStarted){
            bandGraphThread = new Thread(new BandGraphThread());
            bandGraphThread.start();
        }
    }
    // Adds the 3 groups and 1 user in each group and initialize each group's bandwidth
    public void initialize(){
        groupsOfUsers = new ArrayList<Group>();
        lastEventMessage = new StringBuilder();
        this.signalCounter = 0;
        this.eventCounter = 0;
        this.lastGeneratedGroup = 0;
        this.lastEventAction = 0;

        for(int i=0; i<NUMBER_OF_GROUPS; ++i){
            this.addGroup(new Group(i));
            this.groupsOfUsers.get(i).addUser();
            this.groupsOfUsers.get(i).initializeBandwidth(b, g);
        }

        group0BandAverage = new ArrayList<Double>();
        group1BandAverage = new ArrayList<Double>();
        group2BandAverage = new ArrayList<Double>();
    }
    public String stringStatus(){
        StringBuilder status = new StringBuilder();
        Group currentGroup;

        // For error check debugging
        double[] group_averages = new double[3];
        double min_average = 0;

        ++eventCounter;
        status.append("__EventNO["); status.append(eventCounter); status.append("]");
        status.append("__TrialNO["); status.append(trialCounter); status.append("]");
        status.append("_____ ");
        status.append(getLastActionPerformed());
        status.append(" __________\n");
        for(int i=0; i<groupsOfUsers.size(); ++i){
            currentGroup = groupsOfUsers.get(i);

            status.append("__Group["); status.append(i); status.append("] : ");
            status.append(currentGroup.getBandwidthArray()); status.append("\n");

            status.append("Connected:");      status.append(currentGroup.getConnectedUsers());
            status.append("__FreeBand:");     status.append(String.format("%.2f", currentGroup.getFreeAllocated()));
            status.append("__AverageBand:");  status.append(String.format("%.2f", currentGroup.getAverageBandwidth()));
            status.append("__TotalBand:");    status.append(String.format("%.2f", currentGroup.getTotalBandwidth()));
            status.append("\n");

            // For error check debugging
            group_averages[i] = currentGroup.getAverageBandwidth();
        }

        status.append("___Net Statistics"); status.append("\n");
        status.append("TotalBand:");        status.append(G);
        status.append("__AllocatedBand:");  status.append(getAllocatedBandwidth());
        status.append("__RemainBand:");     status.append(getRemainderBandwidth());
        status.append("__Signals:");        status.append(signalCounter);
        status.append("\n");

        status.append(lastEventMessage.toString()); status.append("\n");

        // For error check debugging - Criteria 3
        if(getRemainderBandwidth() < 0){
            System.out.println("EventNO[" + eventCounter + "]___" + "RemainBand[" + getRemainderBandwidth() + "]");
        }
        // For error check debugging - Criteria 2
        min_average = groupsOfUsers.get(0).getAverageBandwidth();
        if(min_average < g){
            System.out.println("EventNO[" + eventCounter + "]___" + "AverageBand[" + min_average + "]");
        }
        // For error check debugging - Criteria 1
        for(int i=0; i<groupsOfUsers.size()-1; ++i){
            if(group_averages[i] > group_averages[i+1]){
                System.out.println("EventNO[" + eventCounter + "]");
                System.out.println("Averages:" + group_averages[0] + " " + group_averages[1] + " " + group_averages[2]);
            }
        }

        return status.toString();
    }

    public void generateRandomUser(){
        Random random = new Random();

        // Generate random Join or Leave
        boolean isJoining = random.nextBoolean();
        // Generate random group
        int groupNumber = random.nextInt(NUMBER_OF_GROUPS);

        while(lastGeneratedGroup == groupNumber){
            groupNumber = random.nextInt(NUMBER_OF_GROUPS);
        }

        int totalUsersInTheNetwork = 0;
        for(int i=0; i<groupsOfUsers.size(); ++i){
            totalUsersInTheNetwork += groupsOfUsers.get(i).users.size();
        }

        // If there are less than three users in the network
        // the next generated result is Joining User
        if(totalUsersInTheNetwork < 3){
            isJoining = true;
        }

        // The generated value is Joining User
        if(isJoining){
            if(DEBUG){System.out.println("A user is joining to group[" + groupNumber + "]");}
            userJoin(groupNumber);
        }
        // The generated value is Leaving User
        else{
            // If we have only 1 user in a group, add user instead of leaving
            if(groupsOfUsers.get(groupNumber).users.size() < 1){
                if(DEBUG) System.out.println("A user is joining to group[" + groupNumber + "]");
                userJoin(groupNumber);
            }
            else{
                if(DEBUG) System.out.println("A user is leaving from group[" + groupNumber + "]");
                userLeave(groupNumber);
            }
        }

        lastGeneratedGroup = groupNumber;
    }
    public void generateRandomUsers(int userNumber, int repeats){
        ThreadNum = userNumber;
        ThreadRepeats = repeats;

        for(int i=0; i<ThreadRepeats; ++i){
            trialEnd = false;
            eventCounter = 0;
            signalCounter = 0;
            for(int j=0; j<ThreadNum; ++j){
                if(j == ThreadNum-1){
                    ++trialCounter;
                    trialEnd = true;
                }
                generateRandomUser();
            }
        }
    }

    public void userJoin(int groupNumber){
        Group group = groupsOfUsers.get(groupNumber);
        lastEventMessage.delete(0, lastEventMessage.length());

        if(groupNumber == 0){
            lastEventAction = 0;
        } else if(groupNumber == 1){
            lastEventAction = 1;
        } else if(groupNumber == 2){
            lastEventAction = 2;
        }

        group.addUser();

        if(group.getAverageBandwidth() >= group.bi){
            if(!areAllCriteriaMet()){
                for(int i=0; i<groupsOfUsers.size(); ++i)
                    groupsOfUsers.get(i).recalculateBandwidth(b, g);

                ++signalCounter;
            }
        }
        else if(getRemainderBandwidth() >= group.qi){
            group.gi += group.qi;
        }
        else if(!areAllCriteriaMet()){
            for(int i=0; i<groupsOfUsers.size(); ++i){
                groupsOfUsers.get(i).recalculateBandwidth(b, g);
            }

            if(getRemainderBandwidth() < 0){
                group.removeUser();
                ++deniedUsers;
                lastEventMessage.append("WARNING1: User Acess Denied - Not enough bandwidth available!");
                lastEventMessage.append("\n");

                for(int i=0; i<groupsOfUsers.size(); ++i)
                    groupsOfUsers.get(i).recalculateBandwidth(b, g);
            }

            ++signalCounter;
        }
        else{
            lastEventMessage.append("WARNING2: User Acess Denied - Not enough bandwidth available!");
            lastEventMessage.append("\n");
            group.removeUser();
            ++deniedUsers;
        }

        notifyUserActivityObservers();
    }
    public void userLeave(int groupNumber){
        Group group = groupsOfUsers.get(groupNumber);
        lastEventMessage.delete(0, lastEventMessage.length());

        if(groupNumber == 0){
            lastEventAction = 3;
        } else if(groupNumber == 1){
            lastEventAction = 4;
        } else if(groupNumber == 2){
            lastEventAction = 5;
        }

        group.removeUser();

        if(!areAllCriteriaMet()){
            for(int i=0; i<groupsOfUsers.size(); ++i){
                groupsOfUsers.get(i).recalculateBandwidth(b, g);
            }
            ++signalCounter;
        }

        notifyUserActivityObservers();
    }

    public double getRemainderBandwidth(){
        double reminder = G;

        for(int i=0; i<groupsOfUsers.size(); ++i){
            reminder -= groupsOfUsers.get(i).getTotalBandwidth();
        }

        return reminder;
    }
    public double getAllocatedBandwidth(){
        double allocated = 0.0;

        for(int i=0; i<groupsOfUsers.size(); ++i){
            allocated += groupsOfUsers.get(i).getTotalBandwidth();
        }

        return allocated;
    }

    public void registerObserver(UserActivityObserver observer){
        // Add an user activity observer - DBM_UI in our case
        activityObservers.add(observer);
    }
    public void removeObserver(UserActivityObserver observer){
        int i = activityObservers.indexOf(observer);
        if (i >= 0) {
            activityObservers.remove(i);
        }
    }
    public void notifyUserActivityObservers(){
        for(int i = 0; i < activityObservers.size(); i++) {
            UserActivityObserver observer = activityObservers.get(i);
            observer.statusInformation(stringStatus(), eventCounter, signalCounter, trialCounter, deniedUsers, trialEnd);
        }

        // Update info for Bandwidth Graph Thread
        if(isRealTimeStarted){
            group0BandAverage.add(groupsOfUsers.get(0).getAverageBandwidth());
            group1BandAverage.add(groupsOfUsers.get(1).getAverageBandwidth());
            group2BandAverage.add(groupsOfUsers.get(2).getAverageBandwidth());
        }
    }

    public void startRealTimeGraph(){
        isRealTimeStarted = true;
    }
    private class BandGraphThread implements Runnable{

        public void run(){
            while(true){
                try {
                    Thread.sleep(1000);
                    BandwidthChartClass.updateAndDisplay(eventCounter, group0BandAverage, group1BandAverage, group2BandAverage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
