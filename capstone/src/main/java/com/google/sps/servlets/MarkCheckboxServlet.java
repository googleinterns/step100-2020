package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import error.ErrorHandler;

@WebServlet("/mark-checkbox")
public class MarkCheckboxServlet extends AuthenticatedServlet {

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String idString = request.getParameter("id");
    boolean isChecked = Boolean.parseBoolean(request.getParameter("checked"));
    // type representing whether checkbox is for Option or for Challenge
    String type = request.getParameter("type");
    long id = this.parseToLong(response, idString);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = ServletHelper.getEntityFromId(response, id, datastore, type);
    if (entity == null) {
      entity = new Entity(type);
    }
    Set<String> votesSet = this.getUpdatedVotes(entity, isChecked, userId);

    // Update datastore
    entity.setProperty("votes", votesSet);
    datastore.put(entity);
  }

  /**
   * Parses string to long.
   *
   * @param response HttpServletResponse
   * @param idString id of the current checkbox in the form of a String
   * @return long representing the id of current checkbox
   * @throws IOException
   */
  private long parseToLong(HttpServletResponse response, String idString) throws IOException {
    try {
      return Long.parseLong(idString);
    } catch (NumberFormatException e) {
      ErrorHandler.sendError(response, "Cannot parse to long.");
      return 0;
    }
  }

  /**
   * Retrieves the entity from the database based on id.
   *
   * @param response HttpServletResponse
   * @param id of current checkbox
   * @param datastore datastore holding all data
   * @return Entity
   * @throws IOException error thrown from sendError method
   */
  private Entity getEntityFromId(
      HttpServletResponse response, long id, DatastoreService datastore, String type)
      throws IOException {
    try {
      return datastore.get(KeyFactory.createKey(type, id));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "Cannot get entity from datastore");
      return null;
    }
  }

  /**
   * Updates the people who have checked a particular checkbox. Gets an ArrayList from the database
   * representing the list of people who have checked a certain checkbox, which is passed in as an
   * Entity. Converts this ArrayList to a Set and then checks whether the checkbox is checked and
   * whether the set already contains the current user id and then updates the set accordingly.
   *
   * @param entity entity from database
   * @param isChecked boolean whether checkbox is checked
   * @param userId id of user
   * @return set representing users who have checked current checkbox
   */
  private Set<String> getUpdatedVotes(Entity entity, boolean isChecked, String userId) {
    /*
     * Using ArrayList here because datastore will only return type ArrayList.
     * Casting it to a HashSet will still have O(n) time complexity, so ArrayLists
     * seem to be the best option in this scenario.
     */
    List<String> votes =
        (entity.getProperty("votes") == null)
            ? new ArrayList<>()
            : (ArrayList<String>) entity.getProperty("votes");
    Set<String> votesSet;
    if (votes == null) {
      votesSet = new HashSet<String>();
    } else {
      votesSet = new HashSet<String>(votes);
    }
    /*
     * If checkbox is unchecked and list of votes contains user, remove user id from
     * list of people who have checked current checkbox.
     */
    if (!isChecked && votesSet.contains(userId)) {
      votesSet.remove(userId);
    } else if (isChecked && !votesSet.contains(userId)) {
      /*
       * If checkbox is checked and list of votes does not contain user, add user id
       * to list
       */
      votesSet.add(userId);
    }
    return votesSet;
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
