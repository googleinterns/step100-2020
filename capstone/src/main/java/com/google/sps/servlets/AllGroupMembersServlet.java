package com.google.sps.servlets;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.sps.Objects.response.MemberResponse;
import error.ErrorHandler;

@WebServlet("/all-group-members")

public class AllGroupMembersServlet extends AuthenticatedServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {

    Long groupId = Long.parseLong(request.getParameter("groupId"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity groupEntity = getGroupFromId(response, groupId, datastore);
    if (groupEntity == null) return;

    ArrayList<String> allGroupMembers = (ArrayList<String>) groupEntity.getProperty("memberIds");

    List<MemberResponse> basicMemberProfiles = new ArrayList<>();
    for (String memberId : allGroupMembers) {
      Entity userEntity = getUserFromId(response, memberId, datastore);
      if (userEntity != null && !memberId.equals(userId)) {
      MemberResponse member = MemberResponse.fromEntity(
        userEntity, /* includeBadges= */ false);
      basicMemberProfiles.add(member);
      }
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(basicMemberProfiles)); 
  }

  private Entity getGroupFromId(
    HttpServletResponse response, long groupId, DatastoreService datastore)  throws IOException {
      try {
        return datastore.get(KeyFactory.createKey("Group", groupId));
      } catch (EntityNotFoundException e) {
        errorHandler.sendError(response, "Group does not exist.");
        return null;
      }
  }

  private Entity getUserFromId(
    HttpServletResponse response,String userId, DatastoreService datastore)  throws IOException {
      try {
        return datastore.get(KeyFactory.createKey("User", userId));
      } catch (EntityNotFoundException e) {
        errorHandler.sendError(response, "Group does not exist.");
        return null;
      }
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {}
}