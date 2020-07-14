package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;

import error.ErrorHandler;

/**
 * This class writes the JSON object to the response object's outut stream.
 *
 * @author lucyqu
 */
public class ServletHelper {

  /**
   * Converts JSON object to String and write to response writer along with content type.
   *
   * @param response HttpServletResponse
   * @param servletResponse JSON object
   * @param type content type
   * @throws IOException exception thrown if cannot write to response
   */
  public static void write(HttpServletResponse response, Object servletResponse, String type)
      throws IOException {
    String json = "";
    if (servletResponse != null) {
      json = new Gson().toJson(servletResponse);
    } else {
      json = new Gson().toJson("");
    }
    response.setContentType(type);
    try {
      response.getWriter().println(json);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      ErrorHandler.sendError(response, "Cannot write to response");
    }
  }

  public static Entity getGroupEntity(
      HttpServletRequest request, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    String groupIdString = request.getParameter("id");
    long groupId = Long.parseLong(groupIdString);
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    return groupEntity;
  }

  /**
   * Retrieves the entity from the database based on id.
   *
   * @param response HttpServletResponse
   * @param id id of entity
   * @param datastore datastore holding all data
   * @return Entity
   * @throws IOException error thrown from sendError method
   */
  public static Entity getEntityFromId(
      HttpServletResponse response, long id, DatastoreService datastore, String type)
      throws IOException {
    try {
      return datastore.get(KeyFactory.createKey(type, id));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "Cannot get entity from datastore");
      return null;
    }
  }
}
