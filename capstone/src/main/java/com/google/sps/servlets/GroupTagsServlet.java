package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.Objects.Tag;

/** This servlet returns a given group's group tag data to the frontend. */
@WebServlet("/group-tags")
public class GroupTagsServlet extends AuthenticatedServlet {

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");

    ArrayList<Tag> tags = new ArrayList<>();
    ArrayList<EmbeddedEntity> tagsList =
        (groupEntity.getProperty("tags") == null)
            ? new ArrayList<EmbeddedEntity>()
            : (ArrayList<EmbeddedEntity>) groupEntity.getProperty("tags");
    for (EmbeddedEntity tag : tagsList) {
      tags.add(Tag.fromEntity(tag));
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(tags));
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) {}
}
