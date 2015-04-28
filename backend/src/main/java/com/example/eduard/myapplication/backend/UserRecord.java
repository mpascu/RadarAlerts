package com.example.eduard.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.Key;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class UserRecord {

    @Id
    Long id;

    @Index
    private String user;
    private String password;

    @Index Key<AlertRecord> alerts;


    public UserRecord() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Key<AlertRecord> getAlerts() {
        return alerts;
    }

    public void setAlerts(Key<AlertRecord> alerts) {
        this.alerts = alerts;
    }
}