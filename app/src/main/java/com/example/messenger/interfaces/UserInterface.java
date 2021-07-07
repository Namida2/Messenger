package com.example.messenger.interfaces;

import com.example.messenger.User;

public interface UserInterface {
    Long getAge();
    String getName();
    String getCity();
    String getEmail();
    String getSex();
    String getStatus();
    void setAge(Long age);
    void setCity(String city);
    void setEmail(String email);
    void setName(String name);
    void setSex(String sex);
    void setStatus(String status);
}
