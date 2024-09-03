package com.hanynemr.s7dquizapp;

public class Question {
    private String country,city;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Question() {
    }

    public Question(String country, String city) {
        this.country = country;
        this.city = city;
    }
}
