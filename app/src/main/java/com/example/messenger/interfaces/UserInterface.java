package com.example.messenger.interfaces;

import java.util.List;

public interface UserInterface {
    String getAge();
    String getName();
    String getCity();
    String getEmail();
    String getSex();
    String getStatus();

    void setAge(String age);
    void setCity(String city);
    void setEmail(String email);
    void setName(String name);
    void setSex(String sex);
    void setStatus(String status);
}
