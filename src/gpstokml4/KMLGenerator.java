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
    public static boolean SPEED = true;
    
    private static final String KMLFileHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                + "<Document id=\"\"><name>GPStoKML Map v.4</name>\n" 
                + "<description>Draw lines to map.</description>";
    private static final String KMLSpeedFileHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                + "<Document id=\"\"><name>GPStoKML Speed v.4</name>\n" 
                + "<description>Speed bigger than " + (ReadingFolder.SPEED_LIMIT_KNOTS * 1.852) + " km/h.</description>";
    private static final String KMLFileFooter = ""
            + "</Folder>\n"
            + "</Folder>\n"
            + "</Document>\n"
            + "</kml>";
    private static final String KMLFileMonthStart = "<Folder>\n"
            + "<name>%s</name>\n";
    private static final String KMLFileMonthEnd = "</Folder>";
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
            writer.printf(KMLFileMonthStart, placemarks.get(0).getYearWithMonth());
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
                            // Kui eelmine ja praegune punkt on sama
                            writer.println(PointEndingString);
                            writer.printf(PointStartingString, placemarks.get(i).getTimestamp2());
                            writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                        } else {
                            // Punktid ei ole samad
                            writer.println(PointEndingString);
                            writer.println(KMLFileFolderEnd);
                            if(placemarks.get(i).getYearWithMonth() == null ? placemarks.get((i-1)).getYearWithMonth() != null : !placemarks.get(i).getYearWithMonth().equals(placemarks.get((i-1)).getYearWithMonth())) {
                                writer.println(KMLFileMonthEnd);
                                writer.printf(KMLFileMonthStart, placemarks.get(i).getYearWithMonth());
                            }
                            writer.printf(KMLFileFolderStart, placemarks.get(i).getFilename());                            
                            writer.printf(PointStartingString, placemarks.get(i).getTimestamp2());
                            writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                        }
                    } else {
                        // Vahe ei ole üle viie minuti
                        writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                    }
                    
                } else {
                    // Only first time
                    writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
                }
            }            
            writer.println(PointEndingString);
            writer.println(KMLFileFooter);
            
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(KMLGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public static void speed(List<Datapoint> placemarks) {
        if(placemarks.size() > 0) {
            try {
                PrintWriter writer = new PrintWriter(SOURCE_DIRECTORY+"/"+OUTPUT_SPEED_FILENAME, "UTF-8");
                writer.println(KMLSpeedFileHeader);   
                writer.printf(KMLFileMonthStart, placemarks.get(0).getYearWithMonth());
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
                                if(placemarks.get(i).getYearWithMonth() == null ? placemarks.get((i-1)).getYearWithMonth() != null : !placemarks.get(i).getYearWithMonth().equals(placemarks.get((i-1)).getYearWithMonth())) {
                                    writer.println(KMLFileMonthEnd);
                                    writer.printf(KMLFileMonthStart, placemarks.get(i).getYearWithMonth());
                                }
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
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(KMLGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No speeding! ");
            SPEED = false;
        }
    }
    
    public static boolean isDateChanged(LocalDateTime previous, LocalDateTime current) {
        return !(previous.getDayOfYear() == current.getDayOfYear() && previous.getYear() == current.getYear());
    }
    
    
}
