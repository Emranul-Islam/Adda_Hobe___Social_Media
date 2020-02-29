package com.muhammad_sohag.socialmedia.model;

public class UsersModel {
    private String profileImage;
    private String coverImage;
    private String name;
    private String batch;
    private String department;
    private String userId;
    private String bio;

    public UsersModel() {
    }

    public UsersModel(String profileImage, String coverImage, String name, String batch, String department, String userId,String bio) {
        this.profileImage = profileImage;
        this.coverImage = coverImage;
        this.name = name;
        this.batch = batch;
        this.department = department;
        this.userId = userId;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
