package com.matanhassin.matkonli.model;

public class User {
    private static User theUser = null;
    public String username;
    public String userEmail;
    public String userId;
    public String userPassword;
    public String userAddress;
    public String userprofileImageUrl;

    private User() {
        username = null;
        userEmail = null;
        userId = null;
        userPassword = null;
        userAddress = null;
        userprofileImageUrl = null;
    }

    public static User getInstance() {
        if (theUser == null)
            theUser = new User();
        return theUser;
    }

    public static User getTheUser() {
        return theUser;
    }

    public static void setTheUser(User theUser) {
        User.theUser = theUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserprofileImageUrl() {
        return userprofileImageUrl;
    }

    public void setUserprofileImageUrl(String userprofileImageUrl) {
        this.userprofileImageUrl = userprofileImageUrl;
    }
}
