package com.example.Marc.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class AlertRecord {

    @Id
    Long id;

    @Index
    private String alertId;
    private Double lat;
    private Double lng;

    public AlertRecord() {
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public void setLatLng(Double latitude, Double longitude) {
        this.lat=latitude;
        this.lng=longitude;
    }
    public Double getLat(){
        return this.lat;
    }
    public Double getLng(){
        return this.lng;
    }
}