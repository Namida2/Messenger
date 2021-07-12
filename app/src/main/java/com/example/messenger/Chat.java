package com.example.messenger;

import androidx.annotation.Nullable;

import com.example.messenger.interfaces.UserInterface;

import java.util.ArrayList;

public class Chat {

    private String chatName;
    private String lastMessageAt;
    private Long messagesInChat;
    private String type;
    private String chatId;
    private ArrayList<UserInterface> users;
    private ArrayList<Message> messages;

    public Chat() {
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public ArrayList<UserInterface> getUsers() {
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
    public String getChatId() {
        return chatId;
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
    public void setUsers(ArrayList<UserInterface> users) {
        this.users = users;
    }
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public int hashCode() {
        return chatId.hashCode();
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if(obj == null || obj.getClass() != Chat.class) return false;
        Chat chat = (Chat) obj;
        return chat.getChatId().equals(this.chatId);
    }
}
