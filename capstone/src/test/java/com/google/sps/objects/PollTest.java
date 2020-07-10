package com.google.sps.objects;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.sps.Objects.Option;
import com.google.sps.Objects.Poll;

/**
 * Unit tests for {@link Poll}.
 *
 * @author lucyqu
 */
public class PollTest {

  private Poll poll;
  private final String OPTION_NAME = "Swim";
  private final long ID = 100;

  @Before
  public void setUp() {
    poll = new Poll();
  }

  @After
  public void tearDown() {
    poll = null;
  }

  @Test
  public void getOptionTest() {
    assertEquals(poll.getOptions().size(), 0);
  }

  @Test
  public void addOptionTest() {
    Option option = new Option(ID, OPTION_NAME, new ArrayList<String>());
    Option option2 = new Option(ID, OPTION_NAME, new ArrayList<String>());
    poll.addOption(option);
    poll.addOption(option2);

    List<Option> options = poll.getOptions();

    assertEquals(options.size(), 2);
    assertEquals(options.get(0), option);
    assertEquals(options.get(1), option2);
  }
}
