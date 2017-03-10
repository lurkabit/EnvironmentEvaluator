package digitizer;


import java.util.List;

public class ResourceProcessor {
    

    int[] imgForAnalysis;
    int[] result;

    public static final int minOffsetRes = 96;
    public static final int maxOffsetRes = 224;

    public static final int characterGridWidth = 12;
    public static final int characterGridHeight = 12;

    public int[] msgXOffsets = {1525, 1647, 1775, 1768};

    public static final int yOffset = 22;
    int currentXOffset;

    int mgsCurrent = 0;  //0-4
    int digitPos = 0;

    int[] results = {0, 0, 0, 0};

    int scanWidth = 5;
    int charWidth = 12;

    public static final int[] binValues = {minOffsetRes, maxOffsetRes};
    public static final int[] binCountMin = {0, 0};
    public static final int[] binCountMax = {characterGridWidth, characterGridHeight};

    public static final int linearMax = 100000;
    public static final int rmsMax = 15000;
    public static final int rmsMaxRedOnly = 20000;

    List<ResFitResult> rawData;
    
    boolean redChecked = false;
    boolean isRedOnly = false;

    public ResourceProcessor(int[] arrayToProcess) {
        imgForAnalysis = arrayToProcess;
        result = processResources();
    }

    public int[] processResources() {

        

        mgsCurrent = 2;  //0-4//////////////////////////
        currentXOffset = msgXOffsets[mgsCurrent];
        digitPos = 0;

        while (mgsCurrent < results.length) {
            
            if(mgsCurrent == 2 && !redChecked ){
                isRedOnly = checkIfSupplyCapped();
                redChecked = true;
            }
            
            //processDigit();
            if(isRedOnly){
            saveDigit();
            }

            mgsCurrent = 5; ///////////////////
        }

        
        System.out.println("");
        return results;
    }

    public void processDigit() {
        ResFitResult rfr = new ResFitResult();

        int xInit = currentXOffset - scanWidth / 2;
        int xEnd = currentXOffset + scanWidth / 2;

        for (int xOff = xInit; xOff < xEnd; xOff++) {

            int[] cropped = cropImage(imgForAnalysis, xOff);
            
            int[] scoresRMS = computeRMSValues(cropped, Digitizer.resAverages, isRedOnly);
            //int[] scoresLinear = computeLinearMeanDeviation(cropped, Digitizer.resAverages);

            for (int digitID = 0; digitID < 11; digitID++) {

                if (rfr.bestAccuracyRMS < 0 || rfr.bestAccuracyRMS > scoresRMS[digitID]) {
                    rfr.bestAccuracyRMS = scoresRMS[digitID];
                    rfr.bestCharIDbyRMS = digitID;
                    rfr.x = xOff;
                }

            }
        }
        


        if (rfr.bestAccuracyRMS < rmsMax && rfr.bestCharIDbyRMS < 10) {
            results[mgsCurrent] = results[mgsCurrent] * 10 + rfr.bestCharIDbyRMS;

            digitPos++;
            currentXOffset = rfr.x + charWidth;
        } else if (rfr.bestAccuracyRMS < rmsMax && rfr.bestCharIDbyRMS == 10 && mgsCurrent == 2 && digitPos > 0) {
            currentXOffset = rfr.x + charWidth;
            mgsCurrent++;
            msgXOffsets[mgsCurrent] = rfr.x + charWidth;
            digitPos = 0;
        } else {
            mgsCurrent++;
            digitPos = 0;
            if (mgsCurrent < 4) {
                currentXOffset = msgXOffsets[mgsCurrent];
            }
        }

    }
    
    
    
    
    

    public static int[] cropImage(int[] imgForAnalysis, int offsetX) {
        return cropImage(imgForAnalysis, Digitizer.sourceWidth, offsetX, yOffset,  characterGridWidth, characterGridHeight) ;
        
        
        
    }
    public static int[] cropImage(int[] source, int sourceWidth, int offsetX, int offsetY, int newImgWidth, int newImgHeight) {

        int[] imageOutPixels = new int[newImgWidth * newImgHeight];

        for (int itY = 0; itY < newImgHeight; itY++) {
            for (int itX = 0; itX < newImgWidth; itX++) {

                int indexIn = (itY + offsetY) * sourceWidth + itX + offsetX;
                int indexOut = itX + itY * newImgWidth;

                imageOutPixels[indexOut] = source[indexIn];
            }
        }
        return imageOutPixels;
    }

    public int[] computeRMSValues(int[] sample, int[][] standards, boolean isRedOnly) {

        int[] scores = new int[Digitizer.resAverages.length];

        for (int charID = 0; charID < Digitizer.resAverages.length; charID++) {
            for (int pointPos = 0; pointPos < Digitizer.resAverages[0].length; pointPos++) {
                scores[charID] += getRMS(standards[charID][pointPos], sample[pointPos], isRedOnly);
            }
        }
        //scores = scores/records.length; return (int)  Math.pow(scores, 0.5);
        return scores;
    }

    public static double getRMS(int firstPixel, int secondPixel, boolean isRedOnly) {

        double rms = 0.;
        int[] rgb1 = Z.toRGB(firstPixel);
        int[] rgb2 = Z.toRGB(secondPixel);

        if (isRedOnly) {
            return Math.abs(rgb1[0] - rgb2[0] + 0.0);
        } else {

            for (int i = 0; i < rgb1.length; i++) {
                rms += Math.pow(rgb1[i] - rgb2[i] + 0.0, 2);
            }
            rms = rms / rgb1.length;

            return Math.pow(rms, 0.5);
        }
    }



    public int[] computeLinearMeanDeviation(int[] sample, int[][] standards) {

        int[] scores = new int[Digitizer.resAverages.length];

        for (int charID = 0; charID < Digitizer.resAverages.length; charID++) {
            for (int pointPos = 0; pointPos < Digitizer.resAverages[0].length; pointPos++) {
                scores[charID] += getPixelPixelColorDiff(standards[charID][pointPos], sample[pointPos]);
            }
        }
        for (int score : scores) {
            score = score / (Digitizer.resAverages.length * 3);
        }
        return scores;
    }

    public int getPixelPixelColorDiff(int a, int b) {
        int[] rgba = Z.toRGB(a);
        int[] rgbb = Z.toRGB(b);

        return Math.abs(rgba[0] - rgbb[0]) + Math.abs(rgba[1] - rgbb[1]) + Math.abs(rgba[2] - rgbb[2]);
    }

    ///debugging
    public static void printSelection(int[] selection, int width, String legend) {

        System.out.println("\n" + legend);
        int height = selection.length / width;

        for (int color = 0; color < 4; color++) {

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int[] rgb = Z.toRGB(selection[i * 12 + j]);

                    int result = color == 3 ? (rgb[0] + rgb[1] + rgb[2]) / 3 : rgb[color];

                    if (result < 160) {
                        System.out.print("   \t");
                    } else {
                        System.out.print(result + "\t");
                    }
                }
                System.out.println("");
            }
            System.out.println("");
            System.out.println("");
        }
    }

    
        private boolean checkIfSupplyCapped() {


        int cutoff = 160;
        int redSum = 0;
        int allColorsSum = 0;

        int[] cropped = cropImage(imgForAnalysis, msgXOffsets[2]);
        
        for (int pixel = 0; pixel < cropped.length; pixel++) {
            
            int[] rgb = Z.toRGB(cropped[pixel]);

            if (rgb[0] > cutoff) {
                redSum++;
                allColorsSum--;
            }
            if (rgb[1] > cutoff) {
                allColorsSum++;
            }
            if (rgb[2] > cutoff) {
                allColorsSum++;
            }
        }
            
            return redSum > 5 && allColorsSum < -5 ;           

    }
    
    
    
    
    /*  full version
    private boolean checkIfSupplyCapped() {


        int[] cutoffs = {144, 160, 176, 192};
        int[][] bins = new int[3][cutoffs.length];

        int[] cropped = cropImage(imgForAnalysis, msgXOffsets[2]);
        for (int pixel = 0; pixel < cropped.length; pixel++) {
            int[] rgb = toRGB(cropped[pixel]);
            for (int color = 0; color < rgb.length; color++) {
                for (int cut = 0; cut < cutoffs.length; cut++) {

                    if (rgb[color] >= cutoffs[cut]) {
                        bins[color][cut] += 1;

                    }
                }

            }
        }
        
        System.out.print("\nCutoff   \tR\tG\tB");
        for (int cutoff = 0; cutoff < bins[0].length; cutoff++) {
            System.out.print("\n"+cutoffs[cutoff]+"\t");
            for (int color = 0; color < bins.length; color++) {
                
                System.out.print("\t"+bins[color][cutoff]);
            }
        }
        System.out.println("");
        System.out.println("");

        return false;

    }
    */

    //slow and not reliable  
    /*
    private boolean checkIfSupplyCapped() {

        ResFitResult[] rfr = new ResFitResult[2];
        rfr[0] = new ResFitResult();
        rfr[1] = new ResFitResult();

        for (int color = 0; color < 2; color++) {  //1 is red and 0 is white

            int xInit = msgXOffsets[2] - scanWidth / 2;
            int xEnd = msgXOffsets[2] + scanWidth / 2;

            for (int xOff = xInit; xOff < xEnd; xOff++) {

                int[] cropped = cropImage(imgForAnalysis, xOff);

                //String st  = "x " +  xOff;
                //printSelection(cropped, 12, st);
                
                int[][] scoresRMS = new int[2][Digitizer.resAverages.length];
                
                scoresRMS[color] = computeRMSValues(cropped, Digitizer.resAverages, color == 1);
                

                for (int digitID = 0; digitID < 11; digitID++) {

                    if (rfr[color].bestAccuracyRMS < 0 || rfr[color].bestAccuracyRMS > scoresRMS[color][digitID]) {
                        rfr[color].bestAccuracyRMS = scoresRMS[color][digitID];
                        rfr[color].bestCharIDbyRMS = digitID;
                        rfr[color].x = xOff;
                    }
                }
            }
        }
        boolean result = rfr[0].bestAccuracyRMS > rfr[1].bestAccuracyRMS;

        System.out.println("\nWhite: " + rfr[0].bestCharIDbyRMS + " " + rfr[0].bestAccuracyRMS + "\nRed: " + rfr[1].bestCharIDbyRMS + " " + rfr[1].bestAccuracyRMS + "\nReturn " + result);

        return result;

    }
     */

    private String[] generateFileNames(int xOff) {
        
        
        long perfix = System.nanoTime();
        String[] paths = new String[3];
        paths[0] = "C:\\docs\\myJava\\output\\redDigits\\jpegs\\"+xOff+"__"+perfix+".jpg";
        paths[1] = "C:\\docs\\myJava\\output\\redDigits\\grids\\"+xOff+"__"+perfix+".d";
        paths[2] = Long.toString(perfix);
        
        return paths;
        
    }

    private void saveDigit() {
                //////////////////////////////////////////temporary        
                    
            int[] xOffsets = {1783, 1796, 1809, 1822, 1834, 1847}; 
            
            for (int xOff : xOffsets) {
                int[] saveAsArray = cropImage(imgForAnalysis, xOff);
                
                String[] fileNames = generateFileNames(xOff);
                Z.saveImage(saveAsArray, fileNames[0], 12);
                Z.saveArrays(saveAsArray, fileNames[1], fileNames[2]);               
                               
            }            
        
    }
}
