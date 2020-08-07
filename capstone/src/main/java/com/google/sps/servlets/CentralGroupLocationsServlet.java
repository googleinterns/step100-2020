package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.Objects.Location;
import com.google.sps.error.ErrorHandler;

@WebServlet("/central-group-locations")
public class CentralGroupLocationsServlet extends AuthenticatedServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    ArrayList<Long> locationIds =
        (groupEntity.getProperty("locationIds") == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) groupEntity.getProperty("locationIds");

    List<Location> locations = new ArrayList<>();
    for (Long id : locationIds) {
      Entity entity = ServletHelper.getEntityFromId(response, id, datastore, "Location");
      locations.add(Location.fromEntity(entity));
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(locations));
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
