/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package digitizer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author stepan
 */
public class Z {

    public static File[] getFilesFromDirectory(String dirName) {
        File folder = new File(dirName);
        return folder.listFiles();
    }

    public static int[][] readArrays(String path) {

        int[][] result = null;

        try {
            File file = new File(path);
            try (FileReader fileReader = new FileReader(file)) {
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                boolean mapIsImitialized = false;
                String line;

                int currentIndex = 0;

                while ((line = bufferedReader.readLine()) != null) {

                    if (mapIsImitialized) {
                        String[] arrData = line.split(",");
                        for (int i = 0; i < arrData.length; i++) {

                            result[currentIndex][i] = Integer.parseInt(arrData[i]);
                        }
                        currentIndex += 1;
                    } else {

                        result = initializeArray(line);
                        mapIsImitialized = true;
                    }
                }
                return result;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int[][] initializeArray(String line) {
        String[] namesAndSizes = line.split(";");

        int a = Integer.parseInt(namesAndSizes[1]);
        int b = Integer.parseInt(namesAndSizes[2]);

        //int[][] arr = new int[Integer.parseInt(namesAndSizes[1])][Integer.parseInt(namesAndSizes[2])];
        int[][] arr = new int[a][b];

        return arr;
    }

    public static BufferedImage readInputFile(File f) {
        try {
            BufferedImage bufferedJpegIn = ImageIO.read(f);
            return bufferedJpegIn;
        } catch (IOException ex) {
            System.out.println("error:\n" + ex.toString());

        }
        return null;
    }
    
        public static void saveImage(int[] imageOutPixels, String outputFileName, int imgWidth) {
            int imgHeight = imageOutPixels.length/imgWidth;
        BufferedImage imageOut = new BufferedImage(imgWidth, imgHeight, 5);
        imageOut.setRGB(0, 0, imgWidth, imgHeight, imageOutPixels, 0, imgWidth);

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(outputFileName);

            File outputfile = new File(outputFileName);

            ImageIO.write(imageOut, "jpg", outputfile);

            fout.close();
        } catch (FileNotFoundException ex) {
            System.out.println("error:\n" + ex.toString());
        } catch (IOException ex) {
            System.out.println("error:\n" + ex.toString());
        }
    }
        
    public static final void saveArrays(int[] img, String path, String name) {

        StringBuilder sb = new StringBuilder("");
            sb.append(name).append(";").append(img.length).append(";").append(img.length).append(";\n");
            appendArray(img, sb);        

        try (FileWriter file = new FileWriter(path)) {

            file.write(sb.toString());
            file.flush();
        } catch (IOException e) {
        }

    }
    public static void appendArray(int[] arr, StringBuilder sb) {

        
        String tokken = "";
        for (int pos = 0; pos < arr.length; pos++) {
            

            sb.append(tokken).append(arr[pos]);
                tokken = ",";
            
        }
        sb.append("\n");            
        }
    
    //move to z
    public static String getFilePrefix(String inputFile) {
        String[] splitpath = inputFile.split("\\");
        String[] fileAndExt = splitpath[splitpath.length - 1].split(".");
        return fileAndExt[0];
    }
    
        public static int[] toRGB(int pixel) {
        int[] colors = new int[3];
        colors[0] += pixel >> 16 & 0xff;
        colors[1] += pixel >> 8 & 0xff;
        colors[2] += pixel & 0xff;

        return colors;
    }
        
            public static int getPixel(int[] rgb) {
        return rgb[0] * 256 * 256 + rgb[1] * 256 + rgb[2];
    }
    

}
