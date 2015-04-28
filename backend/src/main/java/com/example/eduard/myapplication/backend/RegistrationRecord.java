package com.example.eduard.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.Key;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class RegistrationRecord {

    @Id
    Long id;

    @Index
    private String regId;

    public RegistrationRecord() {
    }

    public String getRegId() {
        return regId;

    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}