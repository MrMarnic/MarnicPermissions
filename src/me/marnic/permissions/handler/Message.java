package me.marnic.permissions.handler;

/**
 * Copyright (c) 03.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class Message {
    private String msg;
    private boolean isError;

    public Message(String msg, boolean isError) {
        this.msg = msg;
        this.isError = isError;
    }

    public Message(String msg) {
        this.msg = msg;
        this.isError = true;
    }

    public Message(boolean isError) {
        this.isError = isError;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isError() {
        return isError;
    }
}
