package com.google.sps.response;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.sps.Objects.Option;
import com.google.sps.Objects.response.PollResponse;

/**
 * This class tests the methods in the PollResponse class.
 *
 * @author lucyqu
 */
public class PollResponseTest {

  private PollResponse pollResponse;
  private Option option1;
  private Option option2;

  @Before
  public void setUp() {
    List<Option> options = new ArrayList<Option>();
    List<Long> votedOptions = new ArrayList<Long>();

    option1 = new Option(100, "Swim", new ArrayList<String>());
    option2 = new Option(200, "Run", new ArrayList<String>());
    options.add(option1);
    options.add(option2);
    votedOptions.add((long) 100);

    pollResponse = new PollResponse(options, votedOptions, "100");
  }

  @After
  public void tearDown() {
    pollResponse = null;
    option1 = null;
    option2 = null;
  }

  @Test
  public void getOptionsTest() {
    List<Option> options = pollResponse.getOptions();
    assert options.size() == 2;
    assertEquals(options.get(0), option1);
    assertEquals(options.get(1), option2);
  }

  @Test
  public void getVotedOptionsTest() {
    assert pollResponse.getVotedOptions().size() == 1;
  }
}
