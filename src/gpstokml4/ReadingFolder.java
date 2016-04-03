/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marko
 */
public class ReadingFolder {
    private static final Path SOURCE_DIRECTORY = Paths.get(System.getProperty("user.dir") + "/data_files");
    private static final String LOGFILE_EXTENSION = "txt";
    //private static final double KNOTS_TO_KMH = 1.852;
    //private static final double SPEED_LIMIT_KMH = 94;
    public static double SPEED_LIMIT_KNOTS = 52.375; // including (50.76 mean 94.00752 km/h and 52.375 mean 97 km/h)
    public static double MAX_SPEED_FIND = 0;
    public static String MAX_SPEED_TIME = "";
    public static double MAX_LENGTH = 0;
    private static int JRK = 0;
    
    /* Get all GPS files from source folder */
    public List<Datapoint> importSourceFiles() {
        // List<Datapoint> allDatapoints = new ArrayList<>();
 
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(SOURCE_DIRECTORY, "*." + LOGFILE_EXTENSION)) {
            ArrayList myFiles = new ArrayList<>();
            for (Path sourceFilePath : directoryStream) {
                myFiles.add(sourceFilePath.toString()); // This is file name with full directory C:\Users\Okram\Documents\NetBeansProjects\GPStoKML4\data_files\2016-01-03.txt
            }
            
            return myFiles;
        } catch (IOException ex) {
            Logger.getLogger(ReadingFolder.class.getName()).log(Level.SEVERE, null, ex);
        }         
        return null;
    }
    
    public List<Datapoint> sourceFileBothReader(List datapoints, double speed) { // if speed 0, then all must 
        List<Datapoint> pointsFromFile = new ArrayList<>(); // Failide nimed
        for(int i=0; i < datapoints.size(); i++){ // failide arvu jagu infot
            String file = datapoints.get(i).toString();            
            Path path = Paths.get(file);
            // Only filename, no folder, no extension. This is date like 2015-12-31 YYYY-MM-DD
            String fileName = path.getFileName().toString().substring(0, 10);
            String yearWithMonth = fileName.substring(0, 7);    // only YYYY-MM            
            try {
                BufferedReader br = Files.newBufferedReader(path);
                String line;
                double pikkus = 0;
                while ((line = br.readLine()) != null) { // Faili lugemine
                    String[] split = line.split(",");
                    if(split[0].toUpperCase().equals("A") && Double.valueOf(split[7]) >= speed) { // Rida on õige ja kiirus >= 97
                        int year = 2000 + Integer.valueOf(split[1].substring(4,6));
                        int month = Integer.valueOf(split[1].substring(2,4));
                        int day = Integer.valueOf(split[1].substring(0,2));
                        int hour = Integer.valueOf(split[2].substring(0, 2));
                        int minute = Integer.valueOf(split[2].substring(2, 4));
                        int second = Integer.valueOf(split[2].substring(4, 6));
                        int millisecond = Integer.valueOf(split[2].substring(7, 10));
                        LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute, second, millisecond);
                        LocalDateTime timestamp2 = LocalDateTime.of(year, month, day, hour, minute, second);                        
                        double latitude = Double.valueOf(split[3].substring(0,3)) + Double.valueOf(split[3].substring(3)) / 60;                        
                        double longitude = Double.valueOf(split[5].substring(0,4)) + Double.valueOf(split[5].substring(4)) / 60;
                        setMaxSpeedTime(Double.valueOf(split[7]), timestamp2); // MAX kiirus ja leitud aeg
                        
                        pointsFromFile.add(new Datapoint(latitude, longitude, timestamp, timestamp2, fileName, yearWithMonth));
                        
                        if(pointsFromFile.size() >= 2) {
                            int maxSuurus = pointsFromFile.size();
                            double prevLat = pointsFromFile.get(maxSuurus-2).getLatitude();
                            double prevLon = pointsFromFile.get(maxSuurus-2).getLongitude();
                            
                            LocalDateTime prevTime =  pointsFromFile.get(maxSuurus-2).getTimestamp();                            
                            //System.out.println(maxSuurus + " " + (maxSuurus-1) + " " + latitude + " " + longitude + " " + timestamp + " | " + prevLat + " " + prevLon + " " + prevTime + " > ");
                            //System.out.println(distance());
                            pikkus = distance(latitude, longitude, prevLat, prevLon, "K");
                            if(pikkus >= 0) {
                                MAX_LENGTH = MAX_LENGTH + pikkus;
                            }
                        }                   
                    } else {
                        // Kindlasti on vaja teada MAX kiirust ja aega.
                        if(split[0].toUpperCase().equals("A")) {
                            int year = 2000 + Integer.valueOf(split[1].substring(4,6));
                            int month = Integer.valueOf(split[1].substring(2,4));
                            int day = Integer.valueOf(split[1].substring(0,2));
                            int hour = Integer.valueOf(split[2].substring(0, 2));
                            int minute = Integer.valueOf(split[2].substring(2, 4));
                            int second = Integer.valueOf(split[2].substring(4, 6));                                                
                            LocalDateTime timestamp2 = LocalDateTime.of(year, month, day, hour, minute, second); 
                            double latitude = Double.valueOf(split[3].substring(0,3)) + Double.valueOf(split[3].substring(3)) / 60;                        
                            double longitude = Double.valueOf(split[5].substring(0,4)) + Double.valueOf(split[5].substring(4)) / 60;
                            setMaxSpeedTime(Double.valueOf(split[7]), timestamp2); // MAX kiirus ja leitud aeg
                        }
                    }
                    // Siin arvutab kilomeetreid
//                    if(pointsFromFile.size() > 0) {
//                        if((pointsFromFile.size()-1) > JRK) {
//                            double latitude = Double.valueOf(split[3].substring(0,3)) + Double.valueOf(split[3].substring(3)) / 60;                        
//                            double longitude = Double.valueOf(split[5].substring(0,4)) + Double.valueOf(split[5].substring(4)) / 60;
//                            pikkus = pikkus + 
//                            //System.out.println(latitude + " " + longitude + " | " + pointsFromFile.get(pointsFromFile.size() - 1).getLatitude() + " " + pointsFromFile.get(pointsFromFile.size() - 1).getLongitude());
//                            System.out.println(pikkus);
//                            JRK++;
//                        }
//                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(ReadingFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pointsFromFile;        
    }
    
    private void setMaxSpeedTime(double speed, LocalDateTime time) {
        if(speed > MAX_SPEED_FIND) { // Otsitakse MAX kiirust ja aega
            MAX_SPEED_FIND = speed;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"); 
            MAX_SPEED_TIME = dtf.format(time);
        }
    }
    
    // Distansi arvutamiseks;
    //https://www.geodatasource.com/developers/java
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if ("K".equals(unit)) {
            dist = dist * 1.609344;
        } else if ("N".equals(unit)) {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
