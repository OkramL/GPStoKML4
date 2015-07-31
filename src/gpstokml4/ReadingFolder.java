/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Marko
 */
public class ReadingFolder {
    private static final Path SOURCE_DIRECTORY = Paths.get(System.getProperty("user.dir") + "/data_files");
    private static final String LOGFILE_EXTENSION = "txt";
    private static final double KNOTS_TO_KMH = 1.852;
    private static final double SPEED_LIMIT = 94;
    
    /* Get all GPS files from source folder */
    public List<Datapoint> importSourceFiles() {
        // List<Datapoint> allDatapoints = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(SOURCE_DIRECTORY, "*." + LOGFILE_EXTENSION)) {
            ArrayList myFiles = new ArrayList<>();
            for (Path sourceFilePath : directoryStream) {
                myFiles.add(sourceFilePath.toString());
            }
            return myFiles;
        } catch (IOException ex) {
            Logger.getLogger(ReadingFolder.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    /* Read files and parse correct lines. For MAP */
    public List<Datapoint> sourceFileReader(List Datapoint) {
        List<Datapoint> pointsFromFile = new ArrayList<>(); // Failide nimed
        for(int i=0; i < Datapoint.size(); i++){
            String file = Datapoint.get(i).toString();            
            Path path = Paths.get(file);
            String fileName = path.getFileName().toString();    // Only filename, no folders
            try {
                BufferedReader br = Files.newBufferedReader(path);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(",");
                    if(split[0].toUpperCase().equals("A")) {
                        int year = 2000 + Integer.valueOf(split[1].substring(4,6));
                        int month = Integer.valueOf(split[1].substring(2,4));
                        int day = Integer.valueOf(split[1].substring(0,2));
                        int hour = Integer.valueOf(split[2].substring(0, 2));
                        int minute = Integer.valueOf(split[2].substring(2, 4));
                        int second = Integer.valueOf(split[2].substring(4, 6));
                        int millisecond = Integer.valueOf(split[2].substring(7, 10));
                        LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute, second, millisecond);
                        double latitude = Double.valueOf(split[3].substring(0,3)) + Double.valueOf(split[3].substring(3)) / 60;
                        double longitude = Double.valueOf(split[5].substring(0,4)) + Double.valueOf(split[5].substring(4)) / 60;
                        pointsFromFile.add(new Datapoint(latitude, longitude, timestamp, fileName));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ReadingFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pointsFromFile;
    }
    
      /* Read files and parse correct lines For SPEED*/
    public List<Datapoint> sourceFileSpeedReader(List Datapoint) {
        List<Datapoint> pointsFromFile = new ArrayList<>(); // Failide nimed
        for(int i=0; i < Datapoint.size(); i++){
            String file = Datapoint.get(i).toString();            
            Path path = Paths.get(file);
            String fileName = path.getFileName().toString();    // Only filename, no folders
            try {
                BufferedReader br = Files.newBufferedReader(path);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(",");
                    if(split[0].toUpperCase().equals("A") && Double.valueOf(split[7]) >= 50.76) {
                        int year = 2000 + Integer.valueOf(split[1].substring(4,6));
                        int month = Integer.valueOf(split[1].substring(2,4));
                        int day = Integer.valueOf(split[1].substring(0,2));
                        int hour = Integer.valueOf(split[2].substring(0, 2));
                        int minute = Integer.valueOf(split[2].substring(2, 4));
                        int second = Integer.valueOf(split[2].substring(4, 6));
                        int millisecond = Integer.valueOf(split[2].substring(7, 10));
                        LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute, second, millisecond);
                        double latitude = Double.valueOf(split[3].substring(0,3)) + Double.valueOf(split[3].substring(3)) / 60;
                        double longitude = Double.valueOf(split[5].substring(0,4)) + Double.valueOf(split[5].substring(4)) / 60;
                        pointsFromFile.add(new Datapoint(latitude, longitude, timestamp, fileName));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ReadingFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pointsFromFile;
    }
}
