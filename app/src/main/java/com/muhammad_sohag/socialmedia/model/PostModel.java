package com.muhammad_sohag.socialmedia.model;

public class PostModel {
    private String userId;
    private String postId;
    private String name;
    private String profileLink;
    private String postCaption;
    private String postImageLink;
    private String timestamp;

    public PostModel() {
    }

    public PostModel(String userId, String postId, String name, String profileLink, String postCaption, String postImageLink, String timestamp) {
        this.userId = userId;
        this.postId = postId;
        this.name = name;
        this.profileLink = profileLink;
        this.postCaption = postCaption;
        this.postImageLink = postImageLink;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostImageLink() {
        return postImageLink;
    }

    public void setPostImageLink(String postImageLink) {
        this.postImageLink = postImageLink;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
