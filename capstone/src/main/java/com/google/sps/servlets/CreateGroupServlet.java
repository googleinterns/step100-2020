package com.google.sps.servlets;

import com.google.sps.Objects.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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

@WebServlet("/createGroup")
public class CreateGroupServlet extends AuthenticatedServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String groupName = request.getParameter("groupName");
    
    
    ArrayList<String> members = new ArrayList<String>();
    members.add(userId);
    Group group = new Group(members,
                        new ArrayList<Challenge>(),
                        new ArrayList<Post>(), 
                        new Poll(),
                        groupName, 
                        /* headerImg= */ "",
                        /* groupId=??? */);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(group.toEntity());
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
