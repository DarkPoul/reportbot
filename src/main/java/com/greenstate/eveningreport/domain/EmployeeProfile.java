package com.greenstate.eveningreport.domain;

public class EmployeeProfile {
    private Long telegramUserId;
    private String fullName;
    private String city;
    private String address;
    private String brandName = "Green State";

    public EmployeeProfile() {}

    public EmployeeProfile(Long telegramUserId, String fullName, String city, String address, String brandName) {
        this.telegramUserId = telegramUserId;
        this.fullName = fullName;
        this.city = city;
        this.address = address;
        this.brandName = brandName;
    }

    public Long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(Long telegramUserId) { this.telegramUserId = telegramUserId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
}
