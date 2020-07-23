package com.google.sps.objects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.LinkedHashMap;
import com.google.sps.Objects.TFIDFStringHelper;

/**
 * Unit tests for {@link TFIDFStringHelper}.
 */
 @RunWith(JUnit4.class)
public class TFIDFStringHelperTest {
  private final String testString = "Don't you dare!!";
  private final String expectedString = "don't you dare";
  private final String testStringNumber = "D.on't you 2 dare!,!";
  private final String expectedStringNumber = "don't you 2 dare";

  private final String testNgrams = "how about them how";
  private LinkedHashMap<String, Integer> expectedNgramsMap;
  private final String testNgrams2 = "No not no Not";
  private LinkedHashMap<String, Integer> expectedNgramsMap2;
  private final String testNgrams3 = "how about";
  private LinkedHashMap<String, Integer> expectedNgramsMap3;

  @Before
  public void setUp() {
    expectedNgramsMap = new LinkedHashMap<String, Integer>();
    expectedNgramsMap.put("how", 2);
    expectedNgramsMap.put("about", 1);
    expectedNgramsMap.put("them", 1);
    expectedNgramsMap.put("how about", 1);
    expectedNgramsMap.put("about them", 1);
    expectedNgramsMap.put("them how", 1);
    expectedNgramsMap.put("how about them", 1);
    expectedNgramsMap.put("about them how", 1);


    expectedNgramsMap2 = new LinkedHashMap<String, Integer>();
    expectedNgramsMap2.put("no", 2);
    expectedNgramsMap2.put("not", 2);
    expectedNgramsMap2.put("no not", 2);
    expectedNgramsMap2.put("not no", 1);
    expectedNgramsMap2.put("no not no", 1);
    expectedNgramsMap2.put("not no not", 1);

    expectedNgramsMap3 = new LinkedHashMap<String, Integer>();
    expectedNgramsMap3.put("how", 1);
    expectedNgramsMap3.put("about", 1);
    expectedNgramsMap3.put("how about", 1);
  }

  @After
  public void tearDown() {
    // tear down work
  }

  @Test
  public void sanitizeString() {
    assertEquals(TFIDFStringHelper.sanitize(testString), expectedString);
  }

  @Test
  public void sanitize_StringWithNumbers() {
    assertEquals(TFIDFStringHelper.sanitize(testStringNumber), expectedStringNumber);
  }

  @Test
  public void sanitize_nullString() {
    assertEquals(TFIDFStringHelper.sanitize(null), null);
  }

  @Test
  public void ngramTokenizer() {
    assertEquals(TFIDFStringHelper.ngramTokenizer(testNgrams), expectedNgramsMap);
  }

  @Test
  public void ngramTokenizer_notSanitized() {
    assertEquals(TFIDFStringHelper.ngramTokenizer(testNgrams2), expectedNgramsMap2);
  }

  @Test
  public void ngramTokenizer_twoWords() {
    assertEquals(TFIDFStringHelper.ngramTokenizer(testNgrams3), expectedNgramsMap3);
  }

  @Test
  public void ngramTokenizer_emptyString() {
    assertEquals(TFIDFStringHelper.ngramTokenizer(""), new LinkedHashMap<String, Integer>());
  }
}