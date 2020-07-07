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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import error.ErrorHandler;
import com.google.gson.Gson;
import com.google.sps.Objects.response.MemberResponse;

@WebServlet("/all-group-members")

public class AllGroupMembersServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity member = this.getUserFromId(response, userId, datastore);
    MemberResponse memResponse = MemberResponse.fromEntity(member);

    // Convert to json
    response.setContentType("application/json;");
    System.out.println(memResponse);
    response.getWriter().println(new Gson().toJson(memResponse));  
  }
}