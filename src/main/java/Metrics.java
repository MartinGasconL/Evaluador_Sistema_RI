import java.util.*;

public class Metrics {

    private Map<String, Float> precision;
    private Map<String, Float> recall;
    private Map<String, Float> precToK;
    private Map<String, Float> map;
    private Map<String, Matrix<Float,Float>> interpolatedPrecision;
    private Map<String, Matrix<Float,Float>> recallPrecision;
    private Map<String, Float> f1Score;
    protected static final float[] recalls = {0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1};
    protected static final float[] recallsRP = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f,0.7f, 0.8f, 0.9f};

    public Metrics() {
        this.interpolatedPrecision = new HashMap<>();
        this.recallPrecision = new HashMap<>();
    }

    public Map<String, Float> getF1Score() {
        return f1Score;
    }

    public void setF1Score(Map<String, Float> f1Score) {
        this.f1Score = f1Score;
    }

    public Map<String, Float> getPrecision() {
        return precision;
    }

    public void setPrecision(Map<String, Float> precision) {
        this.precision = precision;
    }

    public Map<String, Float> getRecall() {
        return recall;
    }

    public void setRecall(Map<String, Float> recall) {
        this.recall = recall;
    }

    public Map<String, Float> getPrecToK() {
        return precToK;
    }

    public void setPrecToK(Map<String, Float> precToK) {
        this.precToK = precToK;
    }

    public Map<String, Float> getMap() {
        return map;
    }

    public void setMAP(Map<String, Float> map) {
        this.map = map;
    }

    public void addRecallPrecisionLine(String infoNeed, float recall, float precision) {
        if(recallPrecision.containsKey(infoNeed))
            recallPrecision.get(infoNeed).addRow(recall, precision);
        else {
            Matrix<Float, Float> m = new Matrix<>();
            m.addRow(recall, precision);
            recallPrecision.put(infoNeed, m);
        }
    }

    public void addRInterpolatedPrecisionLine(String infoNeed, float recall, float precision) {
        if(interpolatedPrecision.containsKey(infoNeed))
            interpolatedPrecision.get(infoNeed).addRow(recall, precision);
        else {
            Matrix<Float, Float> m = new Matrix<>();
            m.addRow(recall, precision);
            interpolatedPrecision.put(infoNeed, m);
        }
    }



    public String printMetrics(){
        StringBuilder retval = new StringBuilder();
        for(String infoNeed : interpolatedPrecision.keySet()) {
            retval.append("INFO NEED " +infoNeed+ "\n");
            retval.append("PRECISION:\t" + precision.get(infoNeed) + "\n");
            retval.append("RECALL\t" + recall.get(infoNeed) + "\n");
            retval.append("F1 SCORE\t" + f1Score.get(infoNeed) + "\n");
            retval.append("PREC@10\t" + precToK.get(infoNeed) + "\n");
            retval.append("AVERAGE_PRECISION:\t" + map.get(infoNeed) + "\n");
            retval.append("RECALL-PRECISION\n");
            for (float r : recallsRP)
                retval.append("   " + r + "   " + recallPrecision.get(infoNeed).getB(r) + "\n");
            retval.append("INTERPOLATED RECALL PRECISION\n");
            for (float r : recalls)
                retval.append("   " + r + "   " + interpolatedPrecision.get(infoNeed).getB(r) + "\n");
        }

        retval.append("TOTAL\n");
        retval.append("PRECISION:\t"  + calculateAverage(precision.values()) + "\n");
        retval.append("RECALL\t"      + calculateAverage(recall.values()) + "\n");
        retval.append("F1 SCORE\t"    + calculateAverage(f1Score.values()) + "\n");
        retval.append("PREC@10\t"     + calculateAverage(precToK.values()) + "\n");
        retval.append("MAP:\t"        + calculateAverage(map.values()) + "\n");
        retval.append("INTERPOLATED RECALL PRECISION\n");

        Map<Float, Float>averages = calculateInterpolatedPrecisionAverage(interpolatedPrecision.values());
        for(Map.Entry em : averages.entrySet())
            retval.append("    " + em.getKey() + "   " + em.getValue() + "\n");


        return retval.toString();
    }

    private Map<Float, Float> calculateInterpolatedPrecisionAverage(Collection<Matrix<Float,Float>> interpolatedLines){
        Map <Float, Float> retval = new LinkedHashMap<>();
        float total;
        for(float r: recalls) {
            total = 0;
            for (Matrix<Float, Float> interpolatedLine : interpolatedLines) {
                total += interpolatedLine.getB(r);
            }
            retval.put(r,total/interpolatedLines.size());
        }
        return retval;
    }

    private float calculateAverage(Collection<Float> set) {
        float total = 0;
        for(float value : set){
            total += value;
        }
        return total/set.size();
    }
    public float getPrecision(String infoNeed) {
        return precision.get(infoNeed);
    }

    public float getRecall(String infoNeed) {
        return recall.get(infoNeed);
    }
}
