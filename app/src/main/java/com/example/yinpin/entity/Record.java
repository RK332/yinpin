package com.example.yinpin.entity;

public class Record {
    private String username;
    private String id;//商品id
    private String name;//商品名称
    private String price;
    private String address;

    @Override
    public String toString() {
        return "Record{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public Record(String username, String id, String name, String price, String address) {
        this.username = username;
        this.id = id;
        this.name = name;
        this.price = price;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
