package com.example.eduard.myapplication.backend;

/**
 * Created by eduard on 29/04/15.
 */
public class Result {

    Boolean code;
    String message;

    public Result(Boolean code, String message) {
        this.code = code;
        this.message = message;
    }

    public Boolean getCode() {
        return code;
    }

    public void setCode(Boolean code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
