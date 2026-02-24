package com.greenstate.eveningreport.domain;

public class EmployeeProfile {
    private long telegramUserId;
    private String fullName;
    private String city;
    private String address;
    private String storeName = "Green State";

    public EmployeeProfile() {
    }

    public long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(long telegramUserId) { this.telegramUserId = telegramUserId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}
