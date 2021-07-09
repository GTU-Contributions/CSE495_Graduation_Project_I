package DynamicBandwidthManagement;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        GeneratorModel DBG = new GeneratorModel();

        DBG.initialize();

// Test Criteria 1, 2 and 3
        int averageAllocations = 0;
        ArrayList<Long> allocations = new ArrayList<Long>();

        int averageGroup0band = 0;
        ArrayList<Integer> group0band = new ArrayList<Integer>();

        int averageGroup1band = 0;
        ArrayList<Integer> group1band = new ArrayList<Integer>();

        int averageGroup2band = 0;
        ArrayList<Integer> group2band = new ArrayList<Integer>();
        int numberOfConnectionEvents = 5000;
        int outerLoopTimes = 50;

        for(int j=0; j<outerLoopTimes; ++j){
            for(int i=0; i<numberOfConnectionEvents; ++i) {
                DBG.generateRandomUser();
                averageGroup0band += DBG.getGroups().get(0).getTotalBandwidth();
                averageGroup1band += DBG.getGroups().get(1).getTotalBandwidth();
                averageGroup2band += DBG.getGroups().get(2).getTotalBandwidth();
            }

            averageAllocations += DBG.getSignalCounter();
            allocations.add(DBG.getSignalCounter());

            group0band.add(averageGroup0band/numberOfConnectionEvents);
            group1band.add(averageGroup1band/numberOfConnectionEvents);
            group2band.add(averageGroup2band/numberOfConnectionEvents);

            DBG.resetSignalCounter();
            averageGroup0band = 0;
            averageGroup1band = 0;
            averageGroup2band = 0;
        }

        averageAllocations /= outerLoopTimes;
        System.out.println("All Allocations:" + allocations.toString());
        System.out.println("Average Allocations: " + averageAllocations);

        System.out.println("Average Total Bandwidth:");
        System.out.println("Group0band:" + group0band.toString());
        System.out.println("Group1band:" + group1band.toString());
        System.out.println("Group2band:" + group2band.toString());

//
    }
}


