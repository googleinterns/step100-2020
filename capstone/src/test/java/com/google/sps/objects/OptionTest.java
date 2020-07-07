package com.google.sps.objects;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Option;

/**
 * This class tests the methods in the Option class.
 *
 * @author lucyqu
 */
public class OptionTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private Option option;

  @Before
  public void setUp() {
    helper.setUp();
    option = new Option(100, "Swim", new ArrayList<String>());
  }

  @After
  public void tearDown() {
    helper.tearDown();
    option = null;
  }

  @Test
  public void getTextTest() {
    assertEquals(option.getText(), "Swim");
  }

  @Test
  public void addVoteGetVotesTest() {
    option.addVote("1");
    option.addVote("2");

    assert option.getVotes().size() == 2;
    assertEquals(option.getVotes().get(0), "1");
    assertEquals(option.getVotes().get(1), "2");
  }

  @Test
  public void getIdTest() {
    assert option.getId() == 100;
  }

  @Test
  public void toEntityTest() {
    Entity entity = option.toEntity();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entity);

    ArrayList<String> votes = (ArrayList<String>) entity.getProperty("votes");

    assertEquals(entity.getProperty("text"), "Swim");
    assert votes.size() == 0;
  }

  @Test
  public void fromEntityTest() {
    Entity entity = new Entity("Option");
    entity.setProperty("text", "Run");
    List<String> votes = new ArrayList<String>();
    votes.add("1");
    votes.add("2");
    votes.add("3");
    entity.setProperty("votes", votes);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entity);

    Option returnedOption = Option.fromEntity(entity);
    List<String> returnedVotes = (ArrayList<String>) entity.getProperty("votes");

    assertEquals(returnedOption.getText(), "Run");
    assert returnedVotes.size() == 3;
    assertEquals(returnedVotes.get(0), "1");
    assertEquals(returnedVotes.get(1), "2");
    assertEquals(returnedVotes.get(2), "3");
  }
}
