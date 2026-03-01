package com.example.yinpin.entity;

public class User {
    private String name;
    private String psw;
    private String address;

    public User() {
    }

    public User(String name, String psw, String address) {
        this.name = name;
        this.psw = psw;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
