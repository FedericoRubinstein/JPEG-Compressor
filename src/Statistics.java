import java.math.BigDecimal;
import java.math.RoundingMode;

public class Statistics {
    private int compressionOrDecompression; //0 = compression, 1 = decompression
    private long StartTime;
    private long FinalTime;
    private long TotalTime;
    private int IniSize;
    private int FinalSize;
    private int TotalCompression;
    private double CompressionSpeedAvg;
    private int subType;

    //Constructor
    public Statistics(){}

    //Setters:
    public void setCompressionOrDecompression(int compressionOrDecompression){this.compressionOrDecompression = compressionOrDecompression;}
    public void setStartTime(long StartTime){this.StartTime = StartTime;}
    public void setFinalTime(long FinalTime){this.FinalTime = FinalTime;}
    public void setTotalTime(long TotalTime){this.TotalTime = TotalTime;}
    public void setIniSize(int IniSize){this.IniSize = IniSize;}
    public void setFinalSize(int FinalSize){this.FinalSize = FinalSize;}
    public void setTotalCompression(int TotalCompression){this.TotalCompression = TotalCompression;}
    public void setCompressionSpeedAvg(double CompressionSpeedAvg){this.CompressionSpeedAvg = CompressionSpeedAvg;}
    public void setSubType(int subType){this.subType = subType;}

    //Getters:
    public int getCompressionOrDecompression() {return this.compressionOrDecompression;}
    public long getStartTime() {return this.StartTime;}
    public long getFinalTime() {return this.FinalTime;}
    public long getTotalTime() {return this.TotalTime;}
    public int getIniSize() {return this.IniSize;}
    public int getFinalSize() {return this.FinalSize;}
    public int getTotalCompression() {return this.TotalCompression;}
    public double getCompressionSpeedAvg() {return this.CompressionSpeedAvg;}
    public int getSubType(){return this.subType;}

    //Time handlers:
    public void startClock(){
        setStartTime(System.nanoTime());
    }

    public void stopClock(){
        setFinalTime(System.nanoTime());
        setTotalTime(this.FinalTime - this.StartTime);
    }

    //Compression calculations:
    public void calculateCompression(){
        if(this.FinalSize > this.IniSize) {
            setTotalCompression(this.FinalSize - this.IniSize);
        } else {
            setTotalCompression(this.IniSize - this.FinalSize);
        }
    }

    public void calculateCompressionSpeed(){
        setCompressionSpeedAvg(
                (double) (this.TotalCompression/1000) /
                        BigDecimal.valueOf(this.TotalTime*Math.pow(10, -9)).setScale(4, RoundingMode.HALF_UP).doubleValue());
    }

    //Print statistics
    public void print(){
        String operation = this.getCompressionOrDecompression() == 0 ? "Compression" : "Decompression";
        IO.println("***********************************************************************\n\nStatistics:\n");
        IO.println("\tOperation: " + operation);
        String downsampling = this.subType == 0 ? "4:4:4" : this.subType == 1 ? "4:2:2" : "4:2:0";
        IO.println("\tDownsampling used: " + downsampling);
        IO.println("\tTime Spent: " +
                BigDecimal.valueOf(this.getTotalTime()*Math.pow(10, -9)).setScale(4, RoundingMode.HALF_UP).doubleValue()
                + " seconds. (" + this.getTotalTime() +" nanoseconds)");
        IO.println("\tInitial Size: " + this.getIniSize() + " Bytes");
        IO.println("\tFinal Size: " + this.getFinalSize() + " Bytes");
        IO.println("\tTotal " + operation + ": "+ this.getTotalCompression() + " Bytes");
        if(operation.equals("Compression")) IO.println("\tCompression Ratio: "+ (float)this.getIniSize()/this.getFinalSize());
        IO.println("\tAverage " + operation + " Speed: "+ this.getCompressionSpeedAvg()+" KB/s");
        IO.println("\n\n***********************************************************************\n");

    }
}
