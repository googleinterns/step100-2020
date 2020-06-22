package com.google.sps.data;


public final class Comment{

    private final long timestamp;
    private final String commentText;
    private final User user;

    public Comment(long timestamp, String commentText, User user) {
        this.timestamp = timestamp;
        this.commentText = commentText;
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

    public User getUser() {
        return user;
    }
}
 