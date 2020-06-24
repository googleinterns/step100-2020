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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;

@WebServlet("/post")
public class PostDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query("Post").addSort(SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Post> posts = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
    
      long timestamp = (long) entity.getProperty("timestamp");
      String authorId = (String) entity.getProperty("authorId");
      String postText = (String) entity.getProperty("postText");
      String challengeName = (String) entity.getProperty("challengeName");
      String img = (String) entity.getProperty("img");
      ArrayList<String> likes = entity.getProperty("likes");
      ArrayList<Comment> commetns = entity.getProperty("comments");
      Post userPost = new Post(authorId, postText, comments, challengeName, timestamp, img, likes);
      posts.add(userPost);
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(posts));
  }


   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Receives submitted post 
    long timestamp = System.currentTimeMillis();
    String authorId = null;
    String postText = "here is some text";
    String challengeName = "Challenge Name";
    String img = "";
    ArrayList<String> likes = new ArrayList<>();
    ArrayList<Comment> comments = new ArrayList<>();

    // Creates entity with submitted data
    Entity taskEntity = new Entity("Post");
    taskEntity.setProperty("authorId", authorId);
    taskEntity.setProperty("timestamp", timestamp);
    taskEntity.setProperty("postText", postText);
    taskEntity.setProperty("challengeName", challengeName);
    taskEntity.setProperty("img", img);
    taskEntity.setProperty("likes", likes);
    taskEntity.setProperty("comments", comments);

    // Adds entity to database 
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(taskEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/group.html");
  }
}