package com.google.sps.Objects.response;

import java.util.List;
import com.google.sps.Objects.Post;

/**
 * Contains information for the response sent by GroupPostDataServlet.
 */
public final class PostResponse {

  private final List<Post> posts;
  private final List<Long> likedPosts;
  private final String userId;

  /**
   * Constructs a PostResponse object.
   *
   * @param posts List of all posts in group
   * @param likedPosts List of posts liked by user 
   * @param userId user id 
   * 
   */
  public PostResponse(List<Post> posts, List<Long> likedPosts, String userId) {
    this.posts = posts;
    this.likedPosts = likedPosts;
    this.userId = userId;
  }

  /**
   * Gets list of Post objects.
   *
   * @return List of Post objects
   */
  public List<Post> getPosts() {
    return this.posts;
  }

  /**
   * Gets list of likes post ids.
   *
   * @return List of liked posts 
   */
  public List<Long> getLikedPosts() {
    return this.likedPosts;
  }
}

