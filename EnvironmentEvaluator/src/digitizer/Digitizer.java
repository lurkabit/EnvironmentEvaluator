package digitizer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Digitizer {

    //Digitizer.resAverages
    public static int[][] resAverages;
    public int[] currentImage; 

    public static final int sourceWidth = 920;
    public static final int sourceHeight = 1080;
    
    String filePathStr = "C:\\docs\\myJava\\input\\test.jpg";

    public Digitizer() {
        resAverages = importData();
        
        processDataInFolder(); 
        /*
        File file = new File(filePathStr);
        BufferedImage image = readInputFile(file);
        int[] imgForAnalysis = image.getRGB(0, 0, Digitizer.sourceWidth, Digitizer.sourceHeight, null, 0, Digitizer.sourceWidth);
        */
        
    }

    public static int[][] importData() {
        String path = "C:\\docs\\myJava\\digitGridsInts.d";
        return Z.readArrays(path);
    }





    public void processDataInFolder() {
        
        
        //String[] folders = {"C:\\docs\\myJava\\input\\test\\","C:\\docs\\myJava\\input\\whiteAndRed\\w\\", "C:\\docs\\myJava\\input\\whiteAndRed\\r\\"};
        //String[] folders = {"C:\\docs\\myJava\\input\\whiteAndRed\\r\\"};
         String[] folders = {"C:\\docs\\myJava\\input\\whiteAndRed1\\"};
        Map<String, String> actualValues = new HashMap<>();
        
        /*
        String startHere = "2023 1323 112/186 w";
        String red =  "6284 3372 149/120 r";
        String white  = "345 347 44/69 w";
 
        
        actualValues.put("startHere.jpg", startHere);
        actualValues.put("red.jpg", red);
        actualValues.put("white.jpg", white);
        */
        
        for (String folder : folders) {
            File[] files = Z.getFilesFromDirectory(folder);
            
            for (File file : files) {
                
                String name = file.getName();
                
                /*
                if(actualValues.containsKey(name)){
                    System.out.println("\n"+name +"\n"+actualValues.get(name));
                }else{
                    System.out.println(name+ " not in map");
                }*/
                
                BufferedImage image = Z.readInputFile(file);
                int[]  imgForAnalysis = image.getRGB(0, 0, Digitizer.sourceWidth, Digitizer.sourceHeight, null, 0, Digitizer.sourceWidth);
                ResourceProcessor p = new ResourceProcessor(imgForAnalysis);
                
            }
            
        }
        
    }

}
