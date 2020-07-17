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
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.EntityNotFoundException;
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
import com.google.sps.Objects.Group;
import com.google.sps.Objects.response.PostResponse;
import com.google.sps.servlets.ServletHelper;
import error.ErrorHandler;

@WebServlet("/group-post")
public class GroupPostDataServlet extends AuthenticatedServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    ArrayList<Long> postIds = 
        (ArrayList<Long>) groupEntity.getProperty("posts");

    List<Post> posts = new ArrayList<>();
    List<Long> likedPosts = new ArrayList<>();
    for (Long id : postIds) {
      Entity entity = ServletHelper.getEntityFromId(response, id, datastore, "Post");
      posts.add(Post.fromEntity(entity));
      ArrayList<String> likes = (ArrayList<String>) entity.getProperty("likes");
      if (likes != null && likes.contains(userId)) {
        likedPosts.add(entity.getKey().getId());
      }
    }
    // Convert to json
    PostResponse postsRes = new PostResponse(posts, likedPosts, userId);
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(postsRes));
  }

  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    // Receives submitted post 
    Long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity userEntity = 
        ServletHelper.getUserFromId(response, userId, datastore);
    String authorName = userEntity.getProperty("firstName") + " " + userEntity.getProperty("lastName");
    String postText = request.getParameter("post-input");
    String challengeName = "Challenge Name";
    String img = getUploadedFileUrl(request, "image");
    HashSet<String> likes = new HashSet<>();
    ArrayList<Comment> comments = new ArrayList<>();

    // Creates entity with submitted data and add to database
    Post post = new Post(0, authorName, postText, comments, challengeName, System.currentTimeMillis(), img, likes);
    Entity postEntity = post.toEntity();
    datastore.put(postEntity);

    // Add postsId to group
    addPostToGroup(response, groupId, datastore, postEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/group.html?groupId=" + groupId);
  }

  private void addPostToGroup(HttpServletResponse response, Long groupId, DatastoreService datastore, Entity postEntity) throws IOException {
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    ArrayList<Long> postIds = 
        (ArrayList<Long>) groupEntity.getProperty("posts");
    if (postIds == null) {
      postIds = new ArrayList<Long>();
    }
    Post storedPost = Post.fromEntity(postEntity);
    postIds.add(storedPost.getPostId());
    groupEntity.setProperty("posts", postIds);
    datastore.put(groupEntity);
  }

   /** Returns a key that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    String blobKey;
    if (blobKeys == null || blobKeys.isEmpty()) {
      blobKey = null;
    } else {
      blobKey = blobKeys.get(0).getKeyString();
    }
    return blobKey;
  }
}
