package com.example.messenger;

import com.example.messenger.interfaces.UserInterface;

public class User implements UserInterface {
    private String name;
    private String email;
    private String password;
    private String sex;
    private String status;
    private String city;
    private Long age;
    private static final User currentUser = new User();

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    @Override
    public Long getAge() {
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
    @Override
    public void setAge(Long age) {
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
}
