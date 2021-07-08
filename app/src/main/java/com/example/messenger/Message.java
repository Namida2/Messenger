package com.example.messenger;

public class Message {

    private String authorEmail;
    private String message;
    private String time;

    public String getAuthorEmail() {
        return authorEmail;
    }
    public String getMessage() {
        return message;
    }
    public String getTime() {
        return time;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
