/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
public class KMLGenerator {
    
    private static final Path SOURCE_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final String OUTPUT_FILENAME = "CameraMap.kml";
    private static final String OUTPUT_SPEED_FILENAME = "CameraSpeed.kml";
    
    
    private static final String KMLFileHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                + "<Document id=\"\"><name>GPStoKML Map v.4</name>\n" 
                + "<description>Draw lines to map.</description>";
    private static final String KMLSpeedFileHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                + "<Document id=\"\"><name>GPStoKML Speed v.4</name>\n" 
                + "<description>Speed bigger than 94 km/h.</description>";
    private static final String KMLFileFooter = "</Folder>\n"
            + "</Document>\n"
            + "</kml>";
    private static final String KMLFileFolderStart = "<Folder>\n"
            + "<name>%s</name>\n";
    private static final String KMLFileFolderEnd = "</Folder>";
    private static final String PointStartingString = "<Placemark>\n"
                + "<name>%s</name>\n"
                + "<Style> \n"
                + "<LineStyle>\n"
                + "<color>FFFF0000</color>\n"
                + "<width>3</width>\n"
                + "</LineStyle>\n"
                + "</Style>\n"
                + "<LineString id=\"\">\n"
                + "<coordinates>";
    private static final String PointStartingSpeedString = "<Placemark>\n"
                + "<name>%s</name>\n"
                + "<Style> \n"
                + "<LineStyle>\n"
                + "<color>FF0000FF</color>\n"
                + "<width>3</width>\n"
                + "</LineStyle>\n"
                + "</Style>\n"
                + "<LineString id=\"\">\n"
                + "<coordinates>";
    
    private static final String PointEndingString = "</coordinates>\n"
                + "</LineString>\n"
                + "</Placemark>";
    
    public static void build(List<Datapoint> placemarks) {
        try {
            PrintWriter writer = new PrintWriter(SOURCE_DIRECTORY+"/"+OUTPUT_FILENAME, "UTF-8");
            writer.println(KMLFileHeader);            
            writer.printf(KMLFileFolderStart, placemarks.get(0).getFilename());
            writer.printf(PointStartingString, placemarks.get(0).getTimestamp2());            
            for(int i = 0; i < placemarks.size(); i++) {
                if(i > 0) {
                    LocalDateTime previousTime = placemarks.get(i-1).getTimestamp();
                    LocalDateTime currentTime = placemarks.get(i).getTimestamp2();
                    if(previousTime.plusMinutes(5).isBefore(currentTime)) {
                        //System.out.println(previousTime+" "+currentTime);
                        /* Prev and current filename is same */
                        if(placemarks.get(i-1).getFilename().equals(placemarks.get(i).getFilename())) {
                            writer.println(PointEndingString);
                            writer.printf(PointStartingString, placemarks.get(i).getTimestamp2());
                            writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                        } else {
                            writer.println(PointEndingString);
                            writer.println(KMLFileFolderEnd);
                            writer.printf(KMLFileFolderStart, placemarks.get(i).getFilename());
                            writer.printf(PointStartingString, placemarks.get(i).getTimestamp2());
                            writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                        }
                    } else {
                       writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                    }
                } else {
                    writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                }
            }
            writer.println(PointEndingString);
            writer.println(KMLFileFooter);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KMLGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KMLGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public static void speed(List<Datapoint> placemarks) {
        if(placemarks.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(SOURCE_DIRECTORY+"/"+OUTPUT_SPEED_FILENAME, "UTF-8");
                writer.println(KMLSpeedFileHeader);   
                writer.printf(KMLFileFolderStart, placemarks.get(0).getFilename());
                writer.printf(PointStartingSpeedString, placemarks.get(0).getTimestamp2());
                for(int i = 0; i < placemarks.size(); i++) {
                    if(i > 0) {
                        LocalDateTime previousTime = placemarks.get(i-1).getTimestamp();
                        LocalDateTime currentTime = placemarks.get(i).getTimestamp();
                        if(previousTime.plusSeconds(1).isBefore(currentTime)) {
                            //System.out.println(previousTime+" "+currentTime);
                            if(placemarks.get(i-1).getFilename().equals(placemarks.get(i).getFilename())) {
                                writer.println(PointEndingString);
                                writer.printf(PointStartingSpeedString, placemarks.get(i).getTimestamp2());
                                writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                            } else {
                                writer.println(PointEndingString);
                                writer.println(KMLFileFolderEnd);
                                writer.printf(KMLFileFolderStart, placemarks.get(i).getFilename());
                                writer.printf(PointStartingSpeedString, placemarks.get(i).getTimestamp2());
                                writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                            }

                        } else {
                           writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                        }
                    } else {
                        writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                    }
                }
                writer.println(PointEndingString);
                writer.println(KMLFileFooter);
                writer.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(KMLGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(KMLGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No speeding, no correct CameraSpeed.kml file. :)");
        }
    }
    
    public static boolean isDateChanged(LocalDateTime previous, LocalDateTime current) {
        return !(previous.getDayOfYear() == current.getDayOfYear() && previous.getYear() == current.getYear());
    }
    
    
}
