package com.google.sps.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Collections;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.sps.servlets.ServletHelper;
import com.google.sps.Objects.TFIDFStringHelper;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.Comment;
import error.ErrorHandler;

/**
 * This servlet generates groups "tags" for all groups - calculated using TF-IDF.
 */
@WebServlet("/tags-tfidf")
public class TagsTFIDFServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Long> groupIds = getGroupIds();

    LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap = getGroupMap(groupIds, response);
    LinkedHashMap<String, Integer> occurenceMap = getTotalOccurences(groupMap);

    calculateTFIDF(groupMap, occurenceMap, response);
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
   * Returns a mapping of all groupIds to their n-grams (and occurences). 
   */
  private LinkedHashMap<Long, LinkedHashMap<String, Integer>> getGroupMap(
      List<Long> groupIds, HttpServletResponse response) throws IOException {

    LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap = 
        new LinkedHashMap<Long, LinkedHashMap<String, Integer>>();
        
    for (long groupId : groupIds) {
      Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
      List<Long> postIds = (ArrayList<Long>) groupEntity.getProperty("posts");
      List<String> textData = getGroupPostsText(postIds, response);
      addChallengeText(textData);

      // TODO: Parallelize this process to occur for each string concurrently.
      ArrayList<LinkedHashMap<String, Integer>> ngramsList = new ArrayList<>();
      for (String text : textData) {
        ngramsList.add(TFIDFStringHelper.ngramTokenizer(text));
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

    List<String> textData = new ArrayList<>();
    for (long postId : postIds) {
      Entity postEntity = ServletHelper.getEntityFromId(response, postId, datastore, "Post");
      String postText = (String) postEntity.getProperty("postText");
      textData.add(postText);
      addCommentsText(postEntity, textData);
    }

    return textData;
  }

  /**
   * Given a post, adds that post's comments to a list of group text data.
   */
  private void addCommentsText(Entity postEntity, List<String> textData) {
    
    ArrayList<EmbeddedEntity> comments = 
        (ArrayList<EmbeddedEntity>) postEntity.getProperty("comments");

    if (comments != null) {
      for (EmbeddedEntity commentEntity : comments) {
        String commentText = (String) commentEntity.getProperty("commentText");
        textData.add(commentText);
      }
    }
  }

  /**
   * Given a post, adds that post's comments to a list of group text data.
   */
  private void addChallengeText() {

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

  public void calculateTFIDF(LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap, 
      LinkedHashMap<String, Integer> occurenceMap, HttpServletResponse response) 
      throws IOException {
    
    for (Map.Entry<Long, LinkedHashMap<String, Integer>> groupEntry : groupMap.entrySet()) {
      Long groupId = groupEntry.getKey();
      LinkedHashMap<String, Integer> ngramMap = groupEntry.getValue();
      PriorityQueue<Tag> tagQueue = new PriorityQueue<>(Collections.reverseOrder());

      for (Map.Entry<String, Integer> ngramEntry : ngramMap.entrySet()) {
        String ngram = ngramEntry.getKey(); 

        double tf = (double) ngramEntry.getValue() / ngramMap.size();
        double idf = Math.log( (double) groupMap.size() / occurenceMap.get(ngram) );
        double score = tf * idf;
        score = weightScore(score, ngram);

        Tag tag = new Tag(ngram, score);
        tagQueue.add(tag);
      }
      
      putTagsInDatastore(tagQueue, groupId, response);
    }
  }

  /**
   * Weights the tf-idf score of a given ngram by taking into account its length.
   */
  public double weightScore(double score, String ngram) {
    int n = ngram.split(" ").length;
    score *= 1 + (n / 4.0);
    score *= 1 + ((double) ngram.length() / Integer.MAX_VALUE);
    return score;
  }

  /**
   * Given a priority queue of tags, stores the top 3 tag values in the Group's datastore.
   */
  public void putTagsInDatastore(PriorityQueue<Tag> tagQueue, long groupId, 
      HttpServletResponse response) throws IOException {

    ArrayList<Tag> topTags = new ArrayList<>();
    int tagsLength = (tagQueue.size() >= 3) ? 3 : tagQueue.size();
    for (int i = 0; i < tagsLength; i++) {
      Tag next = tagQueue.poll();
      if (checkIfDuplicate(topTags, next)) {
        i--;
      } else {
        topTags.add(next);
      }
    }
    ArrayList<EmbeddedEntity> tags = Tag.createTagEntities(topTags);

    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    groupEntity.setProperty("tags", tags);
    datastore.put(groupEntity);
  }

  /**
   * Checks if a potential group tag is redundant.
   */
  public boolean checkIfDuplicate(ArrayList<Tag> topTags, Tag next) {
    for (Tag tag : topTags) {
      String[] existingWords = tag.getText().split(" ");
      String[] nextWords = next.getText().split(" ");
      for (String existingWord : existingWords) {
        for (String word : nextWords) {
          if (word.equals(existingWord)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}