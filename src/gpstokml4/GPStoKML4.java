/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marko
 */
public class GPStoKML4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        final long startTime = System.currentTimeMillis();
        
        if(args.length > 0) {
            if(args[0].equals("map")) {
                ReadingFolder sp = new ReadingFolder();
                List<Datapoint> myFiles = sp.importSourceFiles();           // My files
                if(myFiles.size() > 0) {
                    List<Datapoint> myFullList = sp.sourceFileReader(myFiles);  // All correct lines from source files
                    KMLGenerator.build(myFullList);                    
                } else {
                    System.out.println("No log files found!");
                }      
            } else if(args[0].equals("speed")) {
                System.out.println("Speed argument not work.");
            } else {
                    System.out.println("Wrong argument.");
            }
        } else {
            System.out.println("No command line arguments!");
            System.out.println("Create map kml file.");
            ReadingFolder sp = new ReadingFolder();
            List<Datapoint> myFiles = sp.importSourceFiles();           // My files
            if(myFiles.size() > 0) {
                List<Datapoint> myFullList = sp.sourceFileReader(myFiles);  // All correct lines from source files
                KMLGenerator.build(myFullList);                
            } else {
                System.out.println("No log files found!");
            }
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Working time: " + (endTime - startTime) + "ms.");
        System.out.println("DONE!");
    }
    
}
