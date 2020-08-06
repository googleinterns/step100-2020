package com.google.sps.servlets;

import com.google.sps.Objects.Badge;
import com.google.sps.Objects.User;
import com.google.sps.error.ErrorHandler;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

@WebServlet("/editProfile")
public class EditProfileServlet extends AuthenticatedServlet {

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
    String profilePic = getUploadedFileUrl(request, "image");
    String first = request.getParameter("first");
    String last = request.getParameter("last");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");
    ArrayList<String> interests = getInterests(request);

    updateProfile(userId, first, last, email, phone, address, profilePic, interests, response);

    response.sendRedirect("/profile.html");
  }

  /**
   * Gets the list of interests entered by user.
   */
  private ArrayList<String> getInterests(HttpServletRequest request) {
    // Get input from the form.
    String interests = request.getParameter("interests");
    // Trim any whitespace immediately following a comma.
    interests = interests.replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

    // Convert the input to an array list.
    String[] interestsArray = interests.split(",");
    ArrayList<String> interestsList = new ArrayList<String>();
    interestsList.addAll(Arrays.asList(interestsArray));

    return interestsList;
  }

  /** 
   * Updates a user's profile information.
   */
  private void updateProfile(String userId, String first, String last, String email, String phone, String address, String pic,
      ArrayList<String> interests, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity userEntity = getExistingUser(userId, response, datastore);
    User user = getUpdatedUser(userEntity, first, last, email, phone, address, pic, interests, response);
    datastore.put(user.toEntity());
  }

  /** 
   * Retrieves existing user entity from datastore.
   */
  private Entity getExistingUser(String userId, HttpServletResponse response, 
      DatastoreService datastore) throws IOException {
    Key entityKey = KeyFactory.createKey("User", userId);
    Entity userEntity;
    try {
      userEntity = datastore.get(entityKey);
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User not found.");
      userEntity = null;
    }
    return userEntity;
  }

  /**
   * Creates updated user object to return.
   */
  private User getUpdatedUser(Entity entity, String first, String last, String email, String phone, String address, String pic,
      ArrayList<String> interests, HttpServletResponse response) throws IOException {
    User user;
    try {
      user = User.fromEntity(entity);
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "Unable to get " + e.getKey().getKind());
      return null;
    }

    if (pic != null && pic != "") user.setProfilePic(pic);
    user.setFirstName(first);
    user.setLastName(last);
    user.setEmail(email);
    user.setPhoneNumber(phone);
    user.setAddress(address);
    user.setInterests(interests);
    return user;
  }

  /** Returns a key that points to the uploaded file, or null if the user didn't upload a file. */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    if (blobs.isEmpty()) return null;
    List<BlobKey> blobKeys = blobs.get(formInputElementName);
    
    String blobKey;
    if (blobKeys == null || blobKeys.isEmpty()){
      blobKey = null;
    } else {
      blobKey = blobKeys.get(0).getKeyString();
    }
    return blobKey;
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
