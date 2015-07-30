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
    private final String fileName; 
    

    public Datapoint(double latitude, double longitude, LocalDateTime time, String fileName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
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
    public String getFilename() {
        return fileName;
    }    

}
