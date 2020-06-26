package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;

@WebServlet("/group-post")
public class GroupPostDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query("Post").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Post> posts = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      posts.add(getPostEntity(entity));
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(posts));
  }

  public Post getPostEntity(Entity entity) {
    long timestamp = (long) entity.getProperty("timestamp");
    String authorId = (String) entity.getProperty("authorId");
    String postText = (String) entity.getProperty("postText");
    String challengeName = (String) entity.getProperty("challengeName");
    String img = (String) entity.getProperty("img");
    HashSet<String> likes = (HashSet<String>) entity.getProperty("likes");
    ArrayList<Comment> comments = (ArrayList<Comment>) entity.getProperty("comments");
    Post userPost = new Post(authorId, postText, comments, challengeName, timestamp, img, likes);
    return userPost;
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Receives submitted post 
    long timestamp = System.currentTimeMillis();
    String authorId = "Jane Doe";
    String postText = request.getParameter("post-input");
    String challengeName = "Challenge Name";
    String img = "";
    HashSet<String> likes = new HashSet<>();
    ArrayList<Comment> comments = new ArrayList<>();

    // Creates entity with submitted data and add to database
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(createPostEntity(timestamp, authorId, postText, challengeName, img, likes, comments));

    // Redirect back to the HTML page.
    response.sendRedirect("/group.html");
  }

  public Entity createPostEntity(long timestamp, String authorId, String postText, String challengeName, String img, HashSet<String> likes, ArrayList<Comment> comments) {
    Entity taskEntity = new Entity("Post");
    taskEntity.setProperty("authorId", authorId);
    taskEntity.setProperty("timestamp", timestamp);
    taskEntity.setProperty("postText", postText);
    taskEntity.setProperty("challengeName", challengeName);
    taskEntity.setProperty("img", img);
    taskEntity.setProperty("likes", likes);
    taskEntity.setProperty("comments", comments);
    return taskEntity;
	}
}
