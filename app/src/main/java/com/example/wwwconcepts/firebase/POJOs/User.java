package com.example.wwwconcepts.firebase.POJOs;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class User {

    public String userId;
    public HashMap<String, HashMap<String, String>> reviews;
    private boolean admin;
    private int points;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String userId, HashMap<String, HashMap<String, String>> reviews) {
        this.userId = userId;
        this.reviews = reviews;
        this.admin = false;
        this.points = 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<String, HashMap<String, String>> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, HashMap<String, String>> reviews) {
        this.reviews = reviews;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void addPoints(int addedPts){
        this.points+=addedPts;
    }

    public int getPoints() {
        return points;
    }
}