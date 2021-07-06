package com.example.messenger;

public class User {
    private String name;
    private String email;
    private String password;
    private String sex;
    private String city;
    private Long age;

    public Long getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSex() {
        return sex;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
