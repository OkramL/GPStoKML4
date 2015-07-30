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
    private static final String OUTPUT_FILENAME = "Kaameraga.kml";
    private static final String TXT_OUTPUT_FILENAME = "Kaameraga.txt";
    
    private static final String KMLFileHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                + "<Document id=\"\"><name>Kaameraga v.4</name>\n" 
                + "<description>Autoga tehtud sõidud, kui kaamera töötab</description>";
    private static final String KMLFileFooter = "</Document>\n"
                + "</kml>";
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
    private static final String PointEndingString = "</coordinates>\n"
                + "</LineString>\n"
                + "</Placemark>";
    
    public static void build(List<Datapoint> placemarks) {
        try {
            PrintWriter writer = new PrintWriter(SOURCE_DIRECTORY+"/"+OUTPUT_FILENAME, "UTF-8");
            writer.println(KMLFileHeader);            
            writer.printf(PointStartingString, placemarks.get(0).getTimestamp());
            for(int i = 0; i < placemarks.size(); i++) {
                if(i > 0) {
                    LocalDateTime previousTime = placemarks.get(i-1).getTimestamp();
                    LocalDateTime currentTime = placemarks.get(i).getTimestamp();
                    if(previousTime.plusMinutes(5).isBefore(currentTime)) {
                        //System.out.println(previousTime+" "+currentTime);
                        writer.println(PointEndingString);
                        writer.printf(PointStartingString, placemarks.get(i).getTimestamp());
                        writer.print(placemarks.get(i).getLongitude()+","+placemarks.get(i).getLatitude()+" ");
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
    
    
    public static boolean isDateChanged(LocalDateTime previous, LocalDateTime current) {
        return !(previous.getDayOfYear() == current.getDayOfYear() && previous.getYear() == current.getYear());
    }
}
