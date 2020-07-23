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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.io.PrintWriter;
import com.google.gson.Gson;
import com.google.sps.servlets.ServletHelper;
import error.ErrorHandler;

/**
 * This servlet generates groups "tags" for all groups - calculated using TF-IDF.
 */
@WebServlet("/group-tags")
public class GroupTagsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // query datastore for all groups
    // for each group, get all post text data and convert into hashmap of ngrams
    // getGroupMaps();

    // for each group in map of groups, for each term in group
    // add term to hashmap of terms and occurences. increment if key exists, set to 1 if not.

    // for each group in the map, for each term in group
    // calculate tf: value(occurences) of term / hashmap.size 
    // calculate idf: log ( total number of groups / count of groups containing term )
    // store Token(term,score) in priority queue (descending order) by score
    // use priority queue's top 3-4 values to create entities and post to datastore.
  }

  /** 
   * Returns a mapping of all groups to their n-grams, given a list of groupIds. 
   */
  private LinkedHashMap<Long, LinkedHashMap<String, Integer>> getGroupMaps(List<Long> groupIds) {
    return null;
    // for each groupId
      // groupId -> groupEntity -> postIds 
      List<String> postText = getGroupPostsText(postIds);
      // TODO: Parallelize this process to occur for posts concurrently
      // for each post text, ngramTokenizer(text)
      // combineMaps(list of maps) (change from varargs to list)
      // put groupId (or Group) in HashMap with map of terms
    // return map
  }

  /** 
   * Returns a list of Post strings, given a list of postIds. 
   */
  private List<String> getGroupPostsText(List<Long> postIds) {
    return null;
  }

  /**
   * Returns group tag data to frontend.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Not yet implemented: Return tag data to frontend.
  }
}