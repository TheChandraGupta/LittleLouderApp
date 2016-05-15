package com.gupta.littlelouder.bean;

/**
 * Created by GUPTA on 14-May-16.
 */
public class Post {

    private int postId;
    private String post;
    private int upVote;
    private int downVote;
    private int userId;
    private String date;
    private boolean like;

    public Post() {
    }

    public Post(int postId, String post, int upVote, int downVote, int userId, String date, boolean like) {
        this.postId = postId;
        this.post = post;
        this.upVote = upVote;
        this.downVote = downVote;
        this.userId = userId;
        this.date = date;
        this.like = like;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
    }

    public int getDownVote() {
        return downVote;
    }

    public void setDownVote(int downVote) {
        this.downVote = downVote;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
