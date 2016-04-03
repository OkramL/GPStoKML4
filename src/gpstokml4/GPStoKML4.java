/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

import java.text.DecimalFormat;
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
        if (args.length > 0) {
            switch (args[0]) {
                case "map":
                    generateKML(true, false);
                    break;
                case "speed":
                    if (args.length == 2) {
                        // Uus kiirus km/h on määratud. 1 knot (kt) = 1.85200 kilometer per hour (kph)
                        double newSpeed = Double.parseDouble(args[1]) / 1.852;
                        ReadingFolder.SPEED_LIMIT_KNOTS = newSpeed;
                    }
                    generateKML(false, true);
                    break;
                case "both":
                    if (args.length == 2) {
                        // Uus kiirus km/h on määratud. 1 knot (kt) = 1.85200 kilometer per hour (kph)
                        double newSpeed = Double.parseDouble(args[1]) / 1.852;
                        ReadingFolder.SPEED_LIMIT_KNOTS = newSpeed;
                    }
                    generateKML(true, true);
                    break;
                case "-h":
                case "-?":
                    System.out.println("GPStoKML version 4.1");
                    System.out.println("(c) 2015-2016 Marko Livental. Thanks Mikk36 and Mart :)");
                    System.out.println("Generates map or speed KML file from camera Mini 0803 log files.");
                    System.out.println("\n\rSwitces:");
                    System.out.println("map         - default. Generates map file. Show distance in kilometers.");
                    System.out.println("speed       - generates speed file where speed >= 97 km/h (default). Show distance with this speed. No all kilometers count.");
                    System.out.println("speed 100   - generates speed file where speed >= 100 km/h. Show distance with this speed. No all kilometers count.");
                    System.out.println("both        - generates map and speed files once. Show kilometers count.");
                    System.out.println("both 100    - generates map and speed files once. Show kilometers count. Speed is >= 100 km/h. ");
                    System.out.println("-h          - help");
                    System.out.println("-v          - version info");
                    System.out.println("\n\rNB 1! Put camera log files to folder data_files. "
                            + "Files CameraMap.kml and CameraSpeed.kml generated into program root folder.");
                    System.out.println("\n\rNB 2! Every time overwrites file CameraMap.kml and/or CameraSpeed.kml.");
                    System.out.println("\n\rNB 3! All time show max speed and when this happend.");
                    break;
                case "-v":
                    System.out.println("GPStoKML version 4.1");
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
        if (myFiles.size() > 0) {
            if (doMap) {
                List<Datapoint> myFullList = sp.sourceFileBothReader(myFiles, 0);  // All correct lines from source files
                KMLGenerator.build(myFullList);
                System.out.print("Max speed was " + customFormat("###.##", ReadingFolder.MAX_SPEED_FIND * 1.852) + " km/h. ");
                System.out.println("This happend " + ReadingFolder.MAX_SPEED_TIME);
                System.out.println("Kilometers count: " + customFormat("####.###", ReadingFolder.MAX_LENGTH));
            }
            if (doSpeed) {
                if(doMap) {
                    ReadingFolder.MAX_LENGTH = 0;                    
                }
                List<Datapoint> myFullSpeedList = sp.sourceFileBothReader(myFiles, ReadingFolder.SPEED_LIMIT_KNOTS);  // All correct lines from source files
                KMLGenerator.speed(myFullSpeedList);
                if(!doMap) {
                    System.out.print("Max speed was " + customFormat("###.##", ReadingFolder.MAX_SPEED_FIND * 1.852) + " km/h. ");
                    System.out.println("This happend " + ReadingFolder.MAX_SPEED_TIME);
                }
                System.out.println("Kilometers count with this speed >= " + customFormat("###.##", ReadingFolder.SPEED_LIMIT_KNOTS * 1.852) + " km/h.: " + customFormat("####.###", ReadingFolder.MAX_LENGTH) + " km.");
            }
        } else {
            System.out.println("No log files found!");
        }
    }

    static public String customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        //System.out.println(value + "  " + pattern + "  " + output);
        return output;
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
