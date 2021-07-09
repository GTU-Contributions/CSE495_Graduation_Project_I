package DynamicBandwidthManagement;

public class User {

    protected int minBandwidth = Integer.MAX_VALUE;
    protected int maxBandwidth = 0;
    protected int currentBandwidth = 0;

    public void setBandwidth(int bandValue){
        this.currentBandwidth = bandValue;

        if(currentBandwidth > maxBandwidth)
            maxBandwidth = currentBandwidth;

        if(currentBandwidth < minBandwidth)
            minBandwidth = currentBandwidth;
    }

    public int getCurrentBandwidth(){return this.currentBandwidth;}
    public int getMinBandwidth(){ return this.minBandwidth; }
    public int getMaxBandwidth(){ return this.maxBandwidth; }
    public int getAverageBandwidth(){ return (this.minBandwidth + this.maxBandwidth)/2;}

}

