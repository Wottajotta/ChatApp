package com.example.myapplication.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phome;
    private String username;
    private Timestamp createTimestamp;

    public UserModel() {
    }

    public UserModel(String phome, String username, Timestamp createTimestamp) {
        this.phome = phome;
        this.username = username;
        this.createTimestamp = createTimestamp;
    }

    public String getPhome() {
        return phome;
    }

    public void setPhome(String phome) {
        this.phome = phome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Timestamp createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}
