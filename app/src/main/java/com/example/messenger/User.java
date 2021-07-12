package com.example.messenger;

import com.example.messenger.interfaces.UserInterface;

import java.util.List;

public class User implements UserInterface {
    private String name;
    private String avatarString;
    private String email;
    private String password;
    private String sex;
    private String status;
    private String city;
    private String age;
    private List<String> chatIds;

    private static User currentUser = new User();

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    @Override
    public String getAge() {
        return age;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getCity() {
        return city;
    }
    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public String getSex() {
        return sex;
    }
    @Override
    public String getStatus() {
        return status;
    }
    public String getAvatarString() {
        return avatarString;
    }

    public List<String> getMyChatIds() {
        return chatIds;
    }

    @Override
    public void setAge(String age) {
        this.age = age;
    }
    @Override
    public void setCity(String city) {
        this.city = city;
    }
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setSex(String sex) {
        this.sex = sex;
    }
    @Override
    public void setStatus(String status) {
        this.status = status;
    }
    public void setAvatarString(String avatarString) {
        this.avatarString = avatarString;
    }

    public void setMyChatIds(List<String> chatIds) {
        this.chatIds = chatIds;
    }
}
