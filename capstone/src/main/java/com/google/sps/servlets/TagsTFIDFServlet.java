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

/** This servlet generates groups "tags" for all groups - calculated using TF-IDF. */
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
      List<Long> postIds =
          (groupEntity.getProperty("posts") == null)
              ? new ArrayList<Long>()
              : (ArrayList<Long>) groupEntity.getProperty("posts");
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

  /** Returns a list of Post content Strings, given a list of postIds. */
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

  /** Creates and returns a map of each ngram along with its occurence count among all groups. */
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

        Tag tag = new Tag(ngram, score);
        tagQueue.add(tag);
      }

      putTagsInDatastore(tagQueue, groupId, response);
    }
  }

  /** Given a priority queue of tags, stores the top 3 tag values in the Group's datastore. */
  public void putTagsInDatastore(
      PriorityQueue<Tag> tagQueue, long groupId, HttpServletResponse response) throws IOException {

    ArrayList<Tag> topTags = new ArrayList<>();
    int tagsLength = (tagQueue.size() >= 3) ? 3 : tagQueue.size();
    for (int i = 0; i < tagsLength; i++) {
      topTags.add(tagQueue.poll());
    }
    ArrayList<EmbeddedEntity> tags = Tag.createTagEntities(topTags);

    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    groupEntity.setProperty("tags", tags);
    datastore.put(groupEntity);
  }
}