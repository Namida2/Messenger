package com.example.messenger;

import androidx.annotation.Nullable;

public class Message {

    private String authorEmail;
    private String authorName;
    private String message;
    private String time;
    private Long id;

    public String getAuthorEmail() {
        return authorEmail;
    }
    public String getMessage() {
        return message;
    }
    public String getTime() {
        return time;
    }
    public Long getId() {
        return id;
    }
    public String getAuthorName() {
        return authorName;
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
    public void setId(Long id) {
        this.id = id;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if(obj == null || obj.getClass() != Message.class) return false;
        Message message = (Message) obj;
        return message.getId().equals(this.id);
    }
}
