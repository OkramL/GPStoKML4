/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

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
        System.out.println("Waiting...");
        if(args.length > 0) {
            switch (args[0]) {
                case "map":
                    generateKML(true, false);
                    break;
                case "speed":
                    generateKML(false, true);
                    break;
                case "both":
                    generateKML(true, true);
                    break;
                case "-h":
                case "-?":
                    System.out.println("GPStoKML version 4.0");
                    System.out.println("(c) 2015 Marko Livental, Mikk36 and Mart :)");
                    System.out.println("Generates map or speed KML file from camera Mini 0803 log files.");
                    System.out.println("\n\rSwitces:");
                    System.out.println("map     - default. Generates map file");
                    System.out.println("speed   - generates speed file where speed bigger than 94 km/h");
                    System.out.println("both    - generates map and speed files once.");
                    System.out.println("-h      - help");
                    System.out.println("-v      - version info");                    
                    System.out.println("\n\rNB! Put camera log files to folder data_files. "
                            + "Files CameraMap.kml and CameraSpeed.kml in program root folder.");
                    System.out.println("\n\rNB 2! Every time overwrites file CameraMap.kml and/or CameraSpeed.kml.");
                    break;
                case "-v":
                    System.out.println("GPStoKML version 4.0");
                    break;
                default:
                    System.out.println("Wrong argument.");
                    break;
            }
        } else {
            System.out.println("No command line arguments!");
            System.out.println("Create map kml file.");
            generateKML(true, false);
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Working time: " + (endTime - startTime) + "ms.");
        System.out.println("DONE!");
    }    
    
    private static void generateKML(boolean doMap, boolean doSpeed) {
        ReadingFolder sp = new ReadingFolder();
        List<Datapoint> myFiles = sp.importSourceFiles();           // My files
        if(myFiles.size() > 0) {
            if(doMap) {
                List<Datapoint> myFullList = sp.sourceFileBothReader(myFiles, 0);  // All correct lines from source files
                KMLGenerator.build(myFullList);
            }
            if(doSpeed) {
                List<Datapoint> myFullSpeedList = sp.sourceFileBothReader(myFiles, ReadingFolder.SPEED_LIMIT_KNOTS);  // All correct lines from source files
                KMLGenerator.speed(myFullSpeedList);
            }
        } else {
            System.out.println("No log files found!");
        }        
    }
    
//        just in any case :)
//        if(args.length > 0) {  
//            System.out.println("Waiting...");
//            ReadingFolder sp = new ReadingFolder();
//            List<Datapoint> myFiles = sp.importSourceFiles();  // My files
//            switch (args[0]) {                
//                case "map": {
//                    if(myFiles.size() > 0) {
//                        List<Datapoint> myFullList = sp.sourceFileBothReader(myFiles,0);  // All correct lines from source files
//                        KMLGenerator.build(myFullList);
//                    } else {
//                        System.out.println("No log files found!");
//                    }  
//                }
//                break;
//                case "speed": {
//                    if(myFiles.size() > 0) {
//                        List<Datapoint> myFullList = sp.sourceFileBothReader(myFiles, ReadingFolder.SPEED_LIMIT_KNOTS);  // All correct lines from source files
//                        KMLGenerator.speed(myFullList);
//                    } else {
//                        System.out.println("No log files found!");
//                    }                       
//                }    
//                break;
//                case "both":
//                    if(myFiles.size() > 0) {
//                        List<Datapoint> myFullList = sp.sourceFileBothReader(myFiles, 0);  // All correct lines from source files
//                        KMLGenerator.build(myFullList);
//                        List<Datapoint> myFullSpeedList = sp.sourceFileBothReader(myFiles, ReadingFolder.SPEED_LIMIT_KNOTS);  // All correct lines from source files
//                        KMLGenerator.speed(myFullSpeedList);
//                    } else {
//                        System.out.println("No log files found!");
//                    }                       
//                break;
//                case "-h":
//                case "-v":
//                    System.out.println("GPStoKML version 4.0");
//                    System.out.println("(c) 2015 Marko Livental ");
//                    System.out.println("Generates map or speed KML file from camera Mini 0803 log files.");
//                    System.out.println("\n\rSwitces:");
//                    System.out.println("map     - default. Generates map file");
//                    System.out.println("speed   - generates speed file where speed bigger than 94 km/h");
//                    System.out.println("both    - generates map and speed files once.");
//                    System.out.println("-h      - help");
//                    System.out.println("-v      - version info");                    
//                    System.out.println("\n\rNB! Put camera log files to folder data_files. "
//                            + "Files CameraMap.kml and CameraSpeed.kml in program root folder.");
//                    System.out.println("\n\rNB 2! Every time overwrites file CameraMap.kml and/or CameraSpeed.kml.");
//                break;
//                default:
//                    System.out.println("Wrong argument.");
//                    break;
//            }
//        } else {
//            System.out.println("No command line arguments!");
//            System.out.println("Create map kml file.");
//            ReadingFolder sp = new ReadingFolder();
//            List<Datapoint> myFiles = sp.importSourceFiles();  // My files
//            if(myFiles.size() > 0) {
//                List<Datapoint> myFullList = sp.sourceFileBothReader(myFiles, 0);  // All correct lines from source files
//                KMLGenerator.build(myFullList);                
//            } else {
//                System.out.println("No log files found!");
//            }
//        }
}
