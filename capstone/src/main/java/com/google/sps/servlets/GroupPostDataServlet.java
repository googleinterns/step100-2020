package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.util.Map;
import java.util.HashSet;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.PostResponse;

@WebServlet("/group-post")
public class GroupPostDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String userId = "";
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userId = userService.getCurrentUser().getUserId();
    }

    Query query = new Query("Post").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Post> posts = new ArrayList<>();
    List<Long> likedPosts = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      posts.add(getPostEntity(entity));
      ArrayList<String> likes = (ArrayList<String>) entity.getProperty("likes");
      if(likes != null && likes.contains(userId)) likedPosts.add(entity.getKey().getId());
    }

    // Convert to json
    PostResponse postsRes = new PostResponse(posts, likedPosts, userId);
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(postsRes));
  }

  private Post getPostEntity(Entity entity) {
    long postId = entity.getKey().getId();
    long timestamp = (long) entity.getProperty("timestamp");
    String authorId = (String) entity.getProperty("authorId");
    String postText = (String) entity.getProperty("postText");
    String challengeName = (String) entity.getProperty("challengeName");
    String img = (String) entity.getProperty("img");
    HashSet<String> likes = (entity.getProperty("likes") == null) ? new HashSet<>() : new HashSet<String>((ArrayList<String>) entity.getProperty("likes"));   
    ArrayList<Comment> comments = new ArrayList<>();
  
    // Create list of comment objects from list of comment entities 
    if (entity.getProperty("comments") != null) {
      createCommentObjectList(comments, entity);
    }

    Post userPost = new Post(postId, authorId, postText, comments, challengeName, timestamp, img, likes);
    return userPost;
  }

  private void createCommentObjectList(ArrayList<Comment> comments, Entity entity) {
    ArrayList<EmbeddedEntity> commentEntitys = (ArrayList<EmbeddedEntity>) entity.getProperty("comments");
    for(EmbeddedEntity comment: commentEntitys) {
      comments.add(
        new Comment((long) comment.getProperty("timestamp"), 
          (String)comment.getProperty("commentText"), 
          (String) comment.getProperty("userId"))
      );
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Receives submitted post 
    long timestamp = System.currentTimeMillis();
    String authorId = "Jane Doe";
    String postText = request.getParameter("post-input");
    String challengeName = "Challenge Name";
    String img = "";
    ArrayList<String> likes = new ArrayList<>();
    ArrayList<Comment> comments = new ArrayList<>();

    // Creates entity with submitted data and add to database
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(createPostEntity(timestamp, authorId, postText, challengeName, img, likes, comments));

    // Redirect back to the HTML page.
    response.sendRedirect("/group.html");
  }

  private Entity createPostEntity(long timestamp, String authorId, String postText, String challengeName, String img, ArrayList<String> likes, ArrayList<Comment> comments) {
    Entity taskEntity = new Entity("Post");
    taskEntity.setProperty("authorId", authorId);
    taskEntity.setProperty("timestamp", timestamp);
    taskEntity.setProperty("postText", postText);
    taskEntity.setProperty("challengeName", challengeName);
    taskEntity.setProperty("img", img);
    taskEntity.setProperty("likes", likes);
    taskEntity.setProperty("comments", createCommentEntities(comments));
    return taskEntity;
	}

  // Create list of comment entities from list of comment objects
  private ArrayList<EmbeddedEntity> createCommentEntities(ArrayList<Comment> comments) {
    ArrayList<EmbeddedEntity> allComments = new ArrayList<>();
    for(Comment comment: comments) {
      EmbeddedEntity commentEntity = new EmbeddedEntity();
      commentEntity.setProperty("timestamp", comment.getTimestamp());
      commentEntity.setProperty("commentText", comment.getCommentText());
      commentEntity.setProperty("userId", comment.getUser());
      allComments.add(commentEntity);
    } 
    return allComments;
  }

   /** Returns a key that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    String blobKey;
    if (blobKeys == null || blobKeys.isEmpty()) {
      blobKey = null;
    } else {
      blobKey = blobKeys.get(0).getKeyString();
    }
    return blobKey;
  }
}
