/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstokml4;

import java.time.LocalDateTime;

/**
 *
 * @author Marko
 */
public class Datapoint {
    private final double latitude;
    private final double longitude;
    private final LocalDateTime time;
    private final LocalDateTime time2; // no milliseconds
    private final String fileName; 
    

    public Datapoint(double latitude, double longitude, LocalDateTime time, LocalDateTime time2, String fileName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.time2 = time2;
        this.fileName = fileName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getTimestamp() {
        return time;
    }
    public LocalDateTime getTimestamp2() {
        return time2;
    }
    
    public String getFilename() {
        return fileName;
    }    

}
