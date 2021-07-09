package UserInterface;

import org.knowm.xchart.*;

import javax.swing.*;
import java.util.ArrayList;

public class SignalingChartClass {

    public static XYChart getChart(int trials, int events, int[] signals) {
        int[] signalsArray = signals;

        // Create Chart
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Signaling Chart").xAxisTitle("Trials").yAxisTitle("Signal Overhead").build();

        double[] xAges = new double[trials];
        double[] signalsWorst = new double[trials];
        double[] signalsBetter = new double[trials];

        for(int i=0; i<trials; ++i){
            xAges[i] = i+1;
            signalsWorst[i] = events;
            signalsBetter[i] = signalsArray[i];
        }

        chart.addSeries("Without the algorithm", xAges, signalsWorst);
        chart.addSeries("With the algorithm", xAges, signalsBetter);

        return chart;
    }

    public static void displayChart(int trials, int events, ArrayList<Integer> signals){
        JFrame frame = new JFrame("Signaling Graph");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        int[] signalsArray = new int[signals.size()];
        for(int i=0; i<signals.size(); ++i)
            signalsArray[i] = signals.get(i);

        new SwingWrapper<XYChart>(new SignalingChartClass().getChart(trials, events, signalsArray)).displayChart();
    }

}