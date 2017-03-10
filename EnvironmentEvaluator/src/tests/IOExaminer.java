/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import digitizer.Digitizer;
import static digitizer.ResourceProcessor.cropImage;
import digitizer.Z;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author stepan
 */
public class IOExaminer {
    
    public IOExaminer(){
        String filePathStr = "C:\\docs\\myJava\\input\\test\\test.jpg";
       BufferedImage image = Z.readInputFile(new File(filePathStr));
        
        int[] imgForAnalysis = image.getRGB(0, 0, Digitizer.sourceWidth, Digitizer.sourceHeight, null, 0, Digitizer.sourceWidth);
        
        //int[] imgForAnalysis = image.getRGB(0, 0, 32, 16, null, 0, 32);
        
       // int[] arr = initializeArray();
        
       // cropImage(int[] source, int sourceWidth, int offsetX, int offsetY, int newImgWidth, int newImgHeight)
        //System.out.println(arrayToString(imgForAnalysis,32));
        //System.out.println("\n");
        
        //System.out.println(arrayToString(cropped,24));
        
        System.out.println(imgForAnalysis[10]+" "+imgForAnalysis[100]+" "+imgForAnalysis[1000]+" "+imgForAnalysis[10000]+"\n");
        int[]  cropped = crop(imgForAnalysis, 1920, 0, 0,1920, 1080);
        
        System.out.println(cropped[10]+" "+cropped[100]+" "+cropped[1000]+" "+cropped[10000]+"\n");
        Z.saveImage(cropped, "C:\\docs\\myJava\\input\\test\\saves\\test2.jpg", 1920);
        
        
        BufferedImage image2 = Z.readInputFile(new File("C:\\docs\\myJava\\input\\test\\saves\\test2.jpg"));
        int[] img2Arr = image2.getRGB(0, 0, Digitizer.sourceWidth, Digitizer.sourceHeight, null, 0, Digitizer.sourceWidth);
        
        System.out.println(img2Arr[10]+" "+img2Arr[100]+" "+img2Arr[1000]+" "+img2Arr[10000]+"\n");
        
        int[]  cropped2 = crop(img2Arr, 1920, 0, 0,1920, 1080);
        
        System.out.println(cropped2[10]+" "+cropped2[100]+" "+cropped2[1000]+" "+cropped2[10000]+"\n");
        Z.saveImage(cropped, "C:\\docs\\myJava\\input\\test\\saves\\test3.jpg", 1920);
        
        
        /*
        //correct
        int[]  cropped = crop(imgForAnalysis, 1920, 200, 4,1600, 200);
        Z.saveImage(cropped, "C:\\docs\\myJava\\input\\test\\saves\\test2.jpg", 1600);
        
        */
    }
    
    
    public String arrayToString(int[] arr, int maxWidth){
        
        StringBuilder sb = new StringBuilder("");
        
        
        int hMax = arr.length/maxWidth;
        
        
        for (int h = 0; h < hMax; h++) {
        for (int w = 0; w < maxWidth; w++) {
            
            
            
            sb.append(arr[h*maxWidth+w]/(256*256*4)).append(" ");
            //sb.append(arr[h*maxWidth+w]).append(" ");
        }
        sb.append("\n");
            
        }        
        return sb.toString();
        
    }
    
    
    
        public static int[] crop(int[] source, int sourceWidth, int offsetX, int offsetY, int newImgWidth, int newImgHeight) {

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

    private int[] initializeArray() {
        
        int[][] arr = new int[7][5];
        
        for (int y = 0; y < arr.length; y++) {
        for (int x = 0; x < arr[0].length; x++) {
            if((y*arr[0].length +x )%4 == 3){
            arr[y][x] = y*1000+x;  
            }
        }            
        }
        
        return flatten(arr);
    }

    private int[] flatten(int[][] arr) {
        int[] result = new int[arr.length*arr[0].length];
                for (int y = 0; y < arr.length; y++) {
        for (int x = 0; x < arr[0].length; x++) {
            
            int current = y*arr[0].length+x;
            
            result[current] = arr[y][x];
            
        }
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
    
}
