package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.Objects.TFIDFStringHelper;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.Comment;
import error.ErrorHandler;

/** 
 * This servlet generates groups "tags" for all groups - calculated using TF-IDF. 
 * It is called every day via Cloud Scheduler.
 */
@WebServlet("/tags-tfidf")
public class TagsTFIDFServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private int longestNgramLength = 0;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Long> groupIds = getGroupIds();

    LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap = getGroupMap(groupIds, response);
    LinkedHashMap<String, Integer> occurenceMap = getTotalOccurences(groupMap);

    calculateTFIDF(groupMap, occurenceMap, response);
  }

  /** Queries the database to return a list of all groupIds. */
  private List<Long> getGroupIds() {
    // Preparing query instance to retrieve all Groups
    Query query = new Query("Group");
    PreparedQuery results = datastore.prepare(query);

    List<Long> groupIds = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long groupId = entity.getKey().getId();
      groupIds.add(groupId);
    }

    return groupIds;
  }

  /** Returns a mapping of all groupIds to their n-grams (and occurences). */
  private LinkedHashMap<Long, LinkedHashMap<String, Integer>> getGroupMap(
      List<Long> groupIds, HttpServletResponse response) throws IOException {

    LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap =
        new LinkedHashMap<Long, LinkedHashMap<String, Integer>>();

    for (long groupId : groupIds) {
      Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");

      List<String> textData = getGroupPostsText(groupEntity, response);
      addChallengeText(groupEntity, textData, response);

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
   * Returns a list of Post content Strings, given a Group entity. 
   */
  private List<String> getGroupPostsText(Entity groupEntity, HttpServletResponse response) 
      throws IOException {

    List<Long> postIds = (ArrayList<Long>) groupEntity.getProperty("posts");
    List<String> textData = new ArrayList<>();
    
    if (postIds != null) {
      for (long postId : postIds) {
        Entity postEntity = ServletHelper.getEntityFromId(response, postId, datastore, "Post");
        String postText = (String) postEntity.getProperty("postText");
        textData.add(postText);
        addCommentsText(postEntity, textData);
      }
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
   * Add's a group's challenge names into a list of group text data.
   */
  private void addChallengeText(Entity groupEntity, List<String> textData,
      HttpServletResponse response) throws IOException {
    List<Long> challengeIds = (ArrayList<Long>) groupEntity.getProperty("challenges");

    if (challengeIds != null) {
      for (long challengeId : challengeIds) {
        Entity challengeEntity =
          ServletHelper.getEntityFromId(response, challengeId, datastore, "Challenge");
        String challengeName = (String) challengeEntity.getProperty("name");
        textData.add(challengeName);
      }
    }
  }


  /**
   * Creates and returns a map of each ngram along with its occurence count among all groups.
   * Also checks the length of each ngram in order to determine the longest string.
   */
  public LinkedHashMap<String, Integer> getTotalOccurences(
      LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap) {

    LinkedHashMap<String, Integer> occurences = new LinkedHashMap<String, Integer>();
    for (Map.Entry<Long, LinkedHashMap<String, Integer>> groupEntry : groupMap.entrySet()) {
      LinkedHashMap<String, Integer> ngramMap = groupEntry.getValue();

      for (Map.Entry<String, Integer> ngramEntry : ngramMap.entrySet()) {
        String ngram = ngramEntry.getKey();
        Integer count = occurences.getOrDefault(ngram, 0);
        occurences.put(ngram, count + 1);

        if (ngram.length() > longestNgramLength) {
          longestNgramLength = ngram.length();
        }
      }
    }
    return occurences;
  }

  public void calculateTFIDF(
      LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap,
      LinkedHashMap<String, Integer> occurenceMap,
      HttpServletResponse response)
      throws IOException {

    for (Map.Entry<Long, LinkedHashMap<String, Integer>> groupEntry : groupMap.entrySet()) {
      Long groupId = groupEntry.getKey();
      LinkedHashMap<String, Integer> ngramMap = groupEntry.getValue();
      PriorityQueue<Tag> tagQueue = new PriorityQueue<>(Collections.reverseOrder());

      for (Map.Entry<String, Integer> ngramEntry : ngramMap.entrySet()) {
        String ngram = ngramEntry.getKey();

        double tf = (double) ngramEntry.getValue() / ngramMap.size();
        double idf = Math.log((double) groupMap.size() / occurenceMap.get(ngram));
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
  private double weightScore(double score, String ngram) {
    double wordsWeight = (ngram.split(" ").length / 3.1);
    double lengthWeight = ((double) ngram.length() / longestNgramLength);

    double totalWeight = 1 + (0.5 * wordsWeight) + (0.5 * lengthWeight);
    score *= totalWeight;

    return score;
  }

  /** Given a priority queue of tags, stores the top 3 tag values in the Group's datastore. */
  public void putTagsInDatastore(
      PriorityQueue<Tag> tagQueue, long groupId, HttpServletResponse response) throws IOException {

    ArrayList<Tag> topTags = new ArrayList<>();
    int tagsLength = (tagQueue.size() >= 3) ? 3 : tagQueue.size();
    for (int i = 0; i < tagsLength; i++) {
      Tag next = tagQueue.poll();
      if (next != null) {
        if (checkIfDuplicate(topTags, next)) {
          i--;
        } else {
          topTags.add(next);
        }
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
    String[] nextWords = next.getText().split(" ");

    for (Tag tag : topTags) {
      String[] existingWords = tag.getText().split(" ");

      int matches = 0;
      for (String existingWord : existingWords) {
        for (String word : nextWords) {
          if (word.equals(existingWord)) {
            matches++;
          }
        }
      }

      if (existingWords.length >= 2 && nextWords.length >= 2) {
        if (matches > 1) {
          return true;
        }
      } else {
        if (matches > 0) {
          return true;
        }
      }
    }
    return false;
  }

}
