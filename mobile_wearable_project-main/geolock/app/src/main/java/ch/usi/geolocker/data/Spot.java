package ch.usi.geolocker.data;

import java.util.Date;


public class Spot {
    private int id;
    private String message;
    private String imageString;
    private double longitude;
    private double latitude;
    private int visibilityRangeRadiusInMeters;
    private Date expirationDateTime;
    private float distance = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getVisibilityRangeRadiusInMeters() {
        return visibilityRangeRadiusInMeters;
    }

    public void setVisibilityRangeRadiusInMeters(int visibilityRangeRadiusInMeters) {
        this.visibilityRangeRadiusInMeters = visibilityRangeRadiusInMeters;
    }

    public Date getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(Date expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

}
