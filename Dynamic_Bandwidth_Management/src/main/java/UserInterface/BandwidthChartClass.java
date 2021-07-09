package UserInterface;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.ArrayList;

public class BandwidthChartClass {

    private static XYChart chart;
    private static SwingWrapper<XYChart> wrapper;
    private static boolean isCreated = false;
    private static double[] xAges;
    private static double[] group0Array, group1Array, group2Array;

    private static void createChart(double[] group0, double[] group1, double[] group2) {

        // Create Chart
        chart = new XYChartBuilder().width(800).height(720).title("Bandwidth Chart").xAxisTitle("Events").yAxisTitle("Average Bandwidth").build();

        xAges = new double[group0.length];

        for(int i=0; i<group0.length; ++i){
            xAges[i] = i+1;
        }

        chart.addSeries("Group 0", xAges, group0);
        chart.addSeries("Group 1", xAges, group1);
        chart.addSeries("Group 2", xAges, group2);
    }
    private static void createWrapper(double[] group0, double[] group1, double[] group2){
        createChart(group0, group1, group2);
        wrapper = new SwingWrapper<XYChart>(chart);
        isCreated = true;
    }
    public static void updateAndDisplay(int events, ArrayList<Double> group0, ArrayList<Double> group1, ArrayList<Double> group2){
        group0Array = new double[events];
        group1Array = new double[events];
        group2Array = new double[events];
        xAges = new double[events];

        for(int i=0; i<events; ++i){
            xAges[i] = i+1;
            group0Array[i] = group0.get(i);
            group1Array[i] = group1.get(i);
            group2Array[i] = group2.get(i);
        }

        if(group0Array.length > 0){
            // If the wrapper is not created, create it
            if(!isCreated){
                createWrapper(group0Array, group1Array, group2Array);
                wrapper.displayChart();
                isCreated = true;
            }
            else{
                chart.updateXYSeries("Group 0", xAges, group0Array, null);
                chart.updateXYSeries("Group 1", xAges, group1Array, null);
                chart.updateXYSeries("Group 2", xAges, group2Array, null);
                wrapper.repaintChart();
            }
        }
    }
}
