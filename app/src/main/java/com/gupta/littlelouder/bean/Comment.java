package com.gupta.littlelouder.bean;

/**
 * Created by GUPTA on 16-May-16.
 */
public class Comment {

    private int commentId;
    private String message;
    private int userId;
    private String userName;
    private int postId;
    private String date;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Comment() {
    }

    public Comment(int commentId, String message, int userId, String userName, int postId, String date) {
        this.commentId = commentId;
        this.message = message;
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
        this.date = date;
    }
}
