package com.example.messenger;

import java.util.ArrayList;

public class Chat {

    private String chatName;
    private String lastMessageAt;
    private Long messagesInChat;
    private String type;
    private ArrayList<User> users;
    private ArrayList<Message> messages;

    public Chat() {
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }
    public Long getMessagesInChat() {
        return messagesInChat;
    }
    public String getChatName() {
        return chatName;
    }
    public String getLastMessageAt() {
        return lastMessageAt;
    }
    public String getType() {
        return type;
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
    public void setLastMessageAt(String lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    public void setMessagesInChat(Long messagesInChat) {
        this.messagesInChat = messagesInChat;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
