package com.google.sps.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.sps.Objects.TFIDFStringHelper;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.Challenge;

/**
 * Unit tests for {@link TagsTFIDFServlet}.
 */
 @RunWith(JUnit4.class)
public class TagsTFIDFServletTest {
  private final long GROUP_ID_1 = 1;
  private final long GROUP_ID_2 = 2;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig())
          .setEnvIsLoggedIn(true);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private LinkedHashMap<Long, LinkedHashMap<String, Integer>> groupMap;
  private TagsTFIDFServlet tagsTFIDFServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    datastore = DatastoreServiceFactory.getDatastoreService();
    populateDatabase(datastore);
    setUpGroupMap();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    tagsTFIDFServlet = new TagsTFIDFServlet();
  }

  @After
  public void tearDown() {
    // tear down work
  }

  @Test
  public void checkIfDuplicate_test() throws Exception {
    ArrayList<Tag> topTags = new ArrayList<Tag>();
    topTags.add(new Tag("world", 0.5));
    topTags.add(new Tag("hello world", 2.5));
    topTags.add(new Tag("greetings pals", 2.0));
    Tag next = new Tag("hello", 0.3);

    assertTrue(tagsTFIDFServlet.checkIfDuplicate(topTags, next));
  }

  @Test
  public void getTotalOccurences_test() throws Exception {
    LinkedHashMap<String, Integer> expectedMap = new LinkedHashMap<>();
    expectedMap.put("hello", 2);
    expectedMap.put("my friends", 2);
    expectedMap.put("why hello", 1);
    expectedMap.put("nice to see", 1);

    LinkedHashMap<String, Integer> occurenceMap = tagsTFIDFServlet.getTotalOccurences(groupMap);

    assertEquals(occurenceMap, expectedMap);
  }

  @Test
  public void calculateTFIDF_test() throws Exception {
    LinkedHashMap<String, Integer> occurenceMap = tagsTFIDFServlet.getTotalOccurences(groupMap);

    tagsTFIDFServlet.calculateTFIDF(groupMap, occurenceMap, mockResponse);

    Key groupKey = KeyFactory.createKey("Group", GROUP_ID_1);
    Entity groupEntity = datastore.get(groupKey);

    ArrayList<Tag> tags = new ArrayList<>();
    for (EmbeddedEntity tag : (ArrayList<EmbeddedEntity>) groupEntity.getProperty("tags")) {
      tags.add(Tag.fromEntity(tag));
    }
    String tagsJson = new Gson().toJson(tags);

    // expected score for "why hello" is tf * idf * weight =
    // (1/3) * (natural log of 2) * (1 + (9/11) + (2/3.1)) = 0.56915311013...

    assertTrue(tagsJson.contains("why hello") && tagsJson.contains("0.56915311013"));
  }

  private void populateDatabase(DatastoreService datastore) {
    datastore.put(createGroupEntity(GROUP_ID_1));
    datastore.put(createGroupEntity(GROUP_ID_2));
  }

  private Entity createGroupEntity(Long id) {
    Entity groupEntity = new Entity("Group", id);
    groupEntity.setProperty("groupName", "Name");
    groupEntity.setProperty("headerImg", "");
    groupEntity.setProperty("memberIds", new ArrayList<String>());
    groupEntity.setProperty("posts", new ArrayList<Long>());
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    groupEntity.setProperty("tags", new ArrayList<Tag>());
    return groupEntity;
  }

  private void setUpGroupMap() {
    groupMap = new LinkedHashMap<Long, LinkedHashMap<String, Integer>>();

    LinkedHashMap<String, Integer> ngramMap1 = new LinkedHashMap<>();
    ngramMap1.put("hello", 1);
    ngramMap1.put("my friends", 1);
    ngramMap1.put("why hello", 1);

    LinkedHashMap<String, Integer> ngramMap2 = new LinkedHashMap<>();
    ngramMap2.put("hello", 1);
    ngramMap2.put("my friends", 1);
    ngramMap2.put("nice to see", 1);

    groupMap.put(GROUP_ID_1, ngramMap1);
    groupMap.put(GROUP_ID_2, ngramMap2);
  }
} 