package com.merc.gmall.bean;

import java.io.Serializable;

public class Result implements Serializable {

    private String message;

    private String state;

    private boolean success;

    public Result() {
    }

    public Result(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
