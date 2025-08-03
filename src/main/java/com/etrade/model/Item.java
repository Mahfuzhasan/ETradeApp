package com.etrade.model;

public class Item {
    private int itemId;
    private String title;
    private String description;
    private double price;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private boolean isSold;
    private String imagePath;
    private int userId;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }


    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public boolean isSold() { return isSold; }
    public void setSold(boolean sold) { isSold = sold; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
