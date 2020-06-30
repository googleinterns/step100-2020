package com.google.sps.Objects;

import java.util.List;
import com.google.sps.Objects.Post;

public final class PostResponse {

  private final List<Post> posts;
  private final List<Long> likedPosts;
  private final String userId;

  public PostResponse(List<Post> posts, List<Long> likedPosts, String userId) {
    this.posts = posts;
    this.likedPosts = likedPosts;
    this.userId = userId;
  }

  public List<Post> getPosts() {
    return this.posts;
  }

  public List<Long> getLikedPosts() {
    return this.likedPosts;
  }
}

