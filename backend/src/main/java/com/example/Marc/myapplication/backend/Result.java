package com.example.Marc.myapplication.backend;

/**
 * Created by Marc on 02/05/2015.
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
