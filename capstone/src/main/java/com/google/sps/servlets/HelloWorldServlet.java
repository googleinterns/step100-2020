package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.Objects.HelloWorld;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@WebServlet("/helloWorld")
public class HelloWorldServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    HelloWorld helloWorldObject = new HelloWorld(getHelloWorld(datastore),getUsers(datastore));

    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(helloWorldObject));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    String user = request.getParameter("user");
    addHelloWorld(datastore);
    addUser(datastore, user);
  }

  private String getHelloWorld(DatastoreService datastore) throws IOException {
    Key helloWorldKey = KeyFactory.createKey("Hello World", "helloWorld");
    try {
      Entity helloWorld = datastore.get(helloWorldKey);
      return (String) helloWorld.getProperty("id");
    } catch (EntityNotFoundException e) {}
    return "";
  }

  private ArrayList<String> getUsers(DatastoreService datastore) {
    ArrayList<String> users = new ArrayList<>();
    Query query = new Query("User");    
    PreparedQuery userEntities = datastore.prepare(query);
    for (Entity userEntity : userEntities.asIterable()) {
      users.add((String) userEntity.getProperty("name"));
    }
    return users;
  }

  private void addHelloWorld(DatastoreService datastore) {
    Entity helloWorldEntity = new Entity("Hello World");
    helloWorldEntity.setProperty("id", "helloWorld");
    datastore.put(helloWorldEntity);
  }

  private void addUser(DatastoreService datastore, String user) {
    Entity userEntity = new Entity("User", user);
    userEntity.setProperty("name", user);
    datastore.put(userEntity);
  }
}