package DynamicBandwidthManagement;

import java.util.ArrayList;

public class Group {
    // The priority of this group
    protected int PRIORITY;
    // Users of this group
    protected ArrayList<User> users = new ArrayList<User>();
    // The default base of guaranteed bandwidth unit of this group
    protected double bi;
    // The guaranteed bandwidth allocation unit of this group
    protected double qi;
    // The total allocated bandwidth to this group
    protected double gi;

    public Group(){}
    public Group(int priority){this.PRIORITY = priority;}

    public int getPriority(){ return PRIORITY; }
    public ArrayList<User> getUsers(){ return users; }
    public void addUser(){
        this.users.add(new User());
    }
    public void removeUser(){
        if(users.size() > 0) this.users.remove(users.size()-1);
    }

    public void initializeBandwidth(double value_b, double value_g){
        // Calculate the guaranteed bandwidth unit of this group
        this.bi = Math.pow(value_b, PRIORITY) * value_g;

        // Calculate the guaranteed bandwidth allocation unit of this group
        this.qi = value_b * this.bi;

        // Calculate the total allocated bandwidth to this group
        this.gi = this.qi;
    }
    public void recalculateBandwidth(double value_b, double value_g){
        // Calculate the guaranteed bandwidth unit of this group
        this.bi = Math.pow(value_b, PRIORITY) * value_g;

        // Calculate the guaranteed bandwidth allocation unit of this group
        this.qi = value_b * this.bi;

        // Calculate the total allocated bandwidth to this group
        if(users.size() < value_b){
            this.gi = value_b * this.bi;
        } else{
            this.gi = users.size() * this.bi;
        }
    }

    public ArrayList<Double> getBandwidthArray(){
        ArrayList<Double> array = new ArrayList<Double>();

        for(int i=0; i<gi/bi; ++i){
            array.add(bi);
        }

        return array;
    }

    public double getTotalBandwidth(){
        // Total allocated bandwidth to the group
        return gi;
    }
    public double getAverageBandwidth(){
        int size = users.size();

        if(size == 0)
            return gi;
        else
            return gi/users.size();
    }
    public double getFreeAllocated() {
        // Free available bandwidth in this group
        return gi - users.size()*bi;
    }
    public int getConnectedUsers(){
        // Total users in this group
        return users.size();
    }
}
