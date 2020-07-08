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
import java.util.List;;
import com.google.gson.Gson;
import com.google.sps.Objects.response.BasicMemberResponse;

@WebServlet("/all-group-members")

public class AllGroupMembersServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query("User");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<BasicMemberResponse> basicMemberProfiles = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      BasicMemberResponse member = BasicMemberResponse.fromEntity(entity);
      basicMemberProfiles.add(member);
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(basicMemberProfiles)); 
  }
}