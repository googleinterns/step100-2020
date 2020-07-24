package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.sps.servlets.ServletHelper;
import error.ErrorHandler;
import com.google.sps.Objects.TFIDFStringHelper;

/**
 * This servlet generates groups "tags" for all groups - calculated using TF-IDF.
 */
@WebServlet("/group-tags")
public class GroupTagsServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    List<Long> groupIds = getGroupIds();
    LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap = getGroupMap(groupIds, response);
    LinkedHashMap<String, Integer> occurenceMap = getTotalOccurences(groupMap);

    // for each group in the map, for each term in group
    // calculate tf: value(occurences) of term / hashmap.size 
    // calculate idf: log ( total number of groups / count of groups containing term )
    // store Token(term,score) in priority queue (descending order) by score
    // use priority queue's top 3-4 values to create entities and post to datastore.
  }

  /** 
   * Queries the database to return a list of all groupIds. 
   */
  private List<Long> getGroupIds() {
     // Preparing query instance to retrieve all Groups
    Query query = new Query("Group");
    PreparedQuery results = datastore.prepare(query);

    List<Long> groupIds = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long groupId = (long) entity.getKey().getId();
      groupIds.add(groupId);
    }
    return groupIds;
  }

  /** 
   * Returns a mapping of all groups to their n-grams, given a list of groupIds. 
   */
  private LinkedHashMap<Long, LinkedHashMap<String, Integer>> getGroupMap(
      List<Long> groupIds, HttpServletResponse response) throws IOException {

    LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap = 
        new LinkedHashMap<Long, LinkedHashMap<String, Integer>>();
        
    for (long groupId : groupIds) {
      Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
      List<Long> postIds = (ArrayList<Long>) groupEntity.getProperty("posts");
      List<String> postTexts = getGroupPostsText(postIds, response);

      // TODO: Parallelize this process to occur for posts concurrently.
      ArrayList<LinkedHashMap<String, Integer>> ngramsList = new ArrayList<>();
      for (String postText : postTexts) {
        ngramsList.add(TFIDFStringHelper.ngramTokenizer(postText));
      }
      groupMap.put(groupId, TFIDFStringHelper.combineMaps(ngramsList));
    }
    return groupMap;
  }

  /** 
   * Returns a list of Post content Strings, given a list of postIds. 
   */
  private List<String> getGroupPostsText(List<Long> postIds, HttpServletResponse response) 
      throws IOException {

    List<String> postTexts = new ArrayList<>();
    for (long postId : postIds) {
      Entity postEntity = ServletHelper.getEntityFromId(response, postId, datastore, "Post");
      String postText = (String) postEntity.getProperty("postText");
      postTexts.add(postText);
    }
    return postTexts;
  }

  /**
   * Creates and returns a map of each ngram along with its occurence count among all groups.
   */
  private LinkedHashMap<String, Integer> getTotalOccurences(
      LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap) {

    LinkedHashMap<String, Integer> occurences = new LinkedHashMap<String, Integer>();
    for (Map.Entry<Long, LinkedHashMap<String, Integer>> groupEntry : groupMap.entrySet()) {
      LinkedHashMap<String, Integer> ngramMap = groupEntry.getValue();

      for (Map.Entry<String, Integer> ngramEntry : ngramMap.entrySet()) {
        String ngram = ngramEntry.getKey();
        Integer count = occurences.getOrDefault(ngram, 0);
        occurences.put(ngram, count + 1);
      }
    }
    return occurences;
  }


  /**
   * Returns group tag data to frontend.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Not yet implemented: Return tag data to frontend.
  }
}