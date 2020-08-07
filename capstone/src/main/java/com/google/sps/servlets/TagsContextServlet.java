package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.Objects.TFIDFStringHelper;
import com.google.sps.Objects.response.TagContextResponse;

/**
 * Given a group and group tag, this servlet finds and returns all posts, comments, challenges etc.
 * in which that text appeared.
 */
@WebServlet("/tags-context")
public class TagsContextServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Long groupId = Long.parseLong(request.getParameter("groupId"));
    String tagText = request.getParameter("tag");

    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");

    ArrayList<TagContextResponse> contexts = new ArrayList<>();

    addMatchingPosts(groupEntity, contexts, tagText, response);
    addMatchingChallenges(groupEntity, contexts, tagText, response);

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(contexts));
  }

  /**
   * Queries the database to find all Posts within Group that contain the tag, and add it to the
   * list of contexts.
   */
  private void addMatchingPosts(
      Entity groupEntity,
      ArrayList<TagContextResponse> contexts,
      String tagText,
      HttpServletResponse response)
      throws IOException {
    List<Long> postIds = (ArrayList<Long>) groupEntity.getProperty("posts");

    if (postIds != null) {
      for (long postId : postIds) {
        Entity postEntity = ServletHelper.getEntityFromId(response, postId, datastore, "Post");
        String postText = (String) postEntity.getProperty("postText");

        if (TFIDFStringHelper.sanitize(postText).contains(tagText)) {
          contexts.add(new TagContextResponse("Post", postText, tagText));
        }

        addMatchingComments(postEntity, contexts, tagText);
      }
    }
  }

  /**
   * Queries the database to find all comments within Group that contain the tag, and add it to the
   * list of contexts.
   */
  private void addMatchingComments(
      Entity postEntity, ArrayList<TagContextResponse> contexts, String tagText) {

    ArrayList<EmbeddedEntity> comments =
        (ArrayList<EmbeddedEntity>) postEntity.getProperty("comments");

    if (comments != null) {
      for (EmbeddedEntity commentEntity : comments) {
        String commentText = (String) commentEntity.getProperty("commentText");

        if (TFIDFStringHelper.sanitize(commentText).contains(tagText)) {
          contexts.add(new TagContextResponse("Comment", commentText, tagText));
        }
      }
    }
  }

  /**
   * Queries the database to find all Challenges within Group that contain the tag, and add it to
   * the list of contexts.
   */
  private void addMatchingChallenges(
      Entity groupEntity,
      ArrayList<TagContextResponse> contexts,
      String tagText,
      HttpServletResponse response)
      throws IOException {
    List<Long> challengeIds = (ArrayList<Long>) groupEntity.getProperty("challenges");

    if (challengeIds != null) {
      for (long challengeId : challengeIds) {
        Entity challengeEntity =
            ServletHelper.getEntityFromId(response, challengeId, datastore, "Challenge");
        String challengeName = (String) challengeEntity.getProperty("name");

        if (TFIDFStringHelper.sanitize(challengeName).contains(tagText)) {
          contexts.add(new TagContextResponse("Challenge", challengeName, tagText));
        }
      }
    }
  }
}
