package com.example.pocopenstreetmap.model;

import java.util.List;

public class Post {
    private int id;
    private String message;
    private String GPS_location;
    //private List<Attachment> attachment;
    //private Creator creator;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getGPS_location() { return GPS_location; }
    public void setGPS_location(String GPS_location) { this.GPS_location = GPS_location; }
    /*
    public List<Attachment> getAttachment() { return attachment; }
    public void setAttachment(List<Attachment> attachment) { this.attachment = attachment; }

    public Creator getCreator() { return creator; }
    public void setCreator(Creator creator) { this.creator = creator; }
    */
}