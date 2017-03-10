package digitizer;

public class ResFitResult {

    ResFitResult() {

    }

    ResFitResult(ResourceProcessor ps, int[] sampleImage) {

    }

    int x;

    int linX;

    int bestCharIDbyRMS;
    int bestAccuracyRMS = -1;
    int bestCharLinear;
    int bestAccuracyLinear = -1;

    int digitPos;
    int resourceType;

    int digitValidity;      //-1 - invalid, 0 - valid digit, 1 - valid slash
    boolean isRedOnly;

    public String toString() {
        String s = bestCharIDbyRMS + " " + bestAccuracyRMS;
        return s;
    }

}
