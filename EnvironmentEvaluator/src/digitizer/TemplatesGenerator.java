/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package digitizer;

import java.io.File;

/**
 *
 * @author stepan
 */
public class TemplatesGenerator {



    public static void processSorted() {

        int[] samplesCount = new int[11];
        int[][] resAverages = new int[11][144];

        String inArrays = "C:\\docs\\myJava\\output\\redDigits\\grids\\";

        String inJpg = "C:\\docs\\myJava\\output\\redDigits\\sorted\\";

        for (int digit = 0; digit < 11; digit++) {
            int[][] tmpStorage = new int[144][3];

            File dirJpg = new File(inJpg + digit + "\\");

            File[] js = dirJpg.listFiles();
            samplesCount[digit] = js.length;

            for (File j : js) {
                String jName = j.getName();
                
                System.out.println(jName);
                String[] prefix = jName.split("\\.");
                
                
                
                String pathToGrid = inArrays+prefix[0]+".d";
                
                int[][] arr = Z.readArrays(pathToGrid);
                
                for (int pxlID = 0; pxlID < arr[0].length; pxlID++) {
                    int[] rgb = Z.toRGB(arr[0][pxlID]);
                    
                    tmpStorage[pxlID][0] +=  rgb[0];
                    tmpStorage[pxlID][1] +=  rgb[1];
                    tmpStorage[pxlID][2] +=  rgb[2];
                    
                }

            } //done with files, finilizing char
            
            for (int pxlID = 0; pxlID < resAverages[0].length; pxlID++) {
                
                tmpStorage[pxlID][0] = tmpStorage[pxlID][0]/samplesCount[digit];
                tmpStorage[pxlID][1] = tmpStorage[pxlID][1]/samplesCount[digit];
                tmpStorage[pxlID][2] = tmpStorage[pxlID][2]/samplesCount[digit];
                                
                resAverages[digit][pxlID] = Z.getPixel(tmpStorage[pxlID]);                
            }            
        }

        for (int i = 0; i < resAverages.length; i++) {
            String path = "C:\\docs\\myJava\\output\\resAverages\\"+i+"_red.d";
            String name = i+"_red";
            
        
            Z.saveArrays(resAverages[i], path, name) ;
        }
    }

}
