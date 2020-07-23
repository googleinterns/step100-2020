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

  @Before
  public void setUp() {
    // set up work
  }

  @After
  public void tearDown() {
    // tear down work
  }

  @Test
  public void sanitizeString() {
    String testString = "Don't you dare!!";
    String expectedString = "don't you dare";

    assertEquals(expectedString, TFIDFStringHelper.sanitize(testString));
  }

  @Test
  public void sanitize_StringWithNumbers() {
    String testStringNumber = ".Don't you 2 dare!,!";
    String expectedStringNumber = "don't you 2 dare";

    assertEquals(expectedStringNumber, TFIDFStringHelper.sanitize(testStringNumber));
  }

  @Test
  public void sanitize_StringWithUrl() {
    String testStringNumber = "users posting urls https://www.google.com/search?q= yo";
    String expectedStringNumber = "users posting urls yo";
    
    assertEquals(expectedStringNumber, TFIDFStringHelper.sanitize(testStringNumber));
  }

  @Test
  public void sanitize_nullString() {
    assertEquals(TFIDFStringHelper.sanitize(null), null);
  }

  @Test
  public void ngramTokenizer() {
    String testNgrams = "how about them how";

    LinkedHashMap<String, Integer> expectedNgramsMap = new LinkedHashMap<String, Integer>();
    expectedNgramsMap.put("how", 2);
    expectedNgramsMap.put("about", 1);
    expectedNgramsMap.put("them", 1);
    expectedNgramsMap.put("how about", 1);
    expectedNgramsMap.put("about them", 1);
    expectedNgramsMap.put("them how", 1);
    expectedNgramsMap.put("how about them", 1);
    expectedNgramsMap.put("about them how", 1);

    assertEquals(expectedNgramsMap, TFIDFStringHelper.ngramTokenizer(testNgrams));
  }

  @Test
  public void ngramTokenizer_notSanitized() {
    String testNgrams2 = "No not no Not";

    LinkedHashMap<String, Integer> expectedNgramsMap2 = new LinkedHashMap<String, Integer>();
    expectedNgramsMap2.put("no", 2);
    expectedNgramsMap2.put("not", 2);
    expectedNgramsMap2.put("no not", 2);
    expectedNgramsMap2.put("not no", 1);
    expectedNgramsMap2.put("no not no", 1);
    expectedNgramsMap2.put("not no not", 1);

    assertEquals(expectedNgramsMap2, TFIDFStringHelper.ngramTokenizer(testNgrams2));
  }

  @Test
  public void ngramTokenizer_twoWords() {
    String testNgrams3 = "how about";

    LinkedHashMap<String, Integer> expectedNgramsMap3 = new LinkedHashMap<String, Integer>();
    expectedNgramsMap3.put("how", 1);
    expectedNgramsMap3.put("about", 1);
    expectedNgramsMap3.put("how about", 1);

    assertEquals(expectedNgramsMap3, TFIDFStringHelper.ngramTokenizer(testNgrams3));
  }

  @Test
  public void ngramTokenizer_emptyString() {
    assertEquals(TFIDFStringHelper.ngramTokenizer(""), new LinkedHashMap<String, Integer>());
  }

  @Test
  public void combineTwoMaps() {
    LinkedHashMap<String, Integer> ngramsMap1 = new LinkedHashMap<String, Integer>();
    ngramsMap1.put("how", 1);
    ngramsMap1.put("about", 1);
    ngramsMap1.put("how about", 1);

    LinkedHashMap<String, Integer> ngramsMap2 = new LinkedHashMap<String, Integer>();
    ngramsMap2.put("how", 2);
    ngramsMap2.put("about", 1);
    ngramsMap2.put("them", 1);
    ngramsMap2.put("how about", 1);
    ngramsMap2.put("about them", 1);
    ngramsMap2.put("them how", 1);
    ngramsMap2.put("how about them", 1);
    ngramsMap2.put("about them how", 1);

    LinkedHashMap<String, Integer> expectedMap = new LinkedHashMap<String, Integer>();
    expectedMap.put("how", 3);
    expectedMap.put("about", 2);
    expectedMap.put("them", 1);
    expectedMap.put("how about", 2);
    expectedMap.put("about them", 1);
    expectedMap.put("them how", 1);
    expectedMap.put("how about them", 1);
    expectedMap.put("about them how", 1);

    assertEquals(expectedMap, TFIDFStringHelper.combineMaps(ngramsMap1, ngramsMap2));
  }

  @Test
  public void combineThreeMaps() {
    LinkedHashMap<String, Integer> ngramsMap1 = new LinkedHashMap<String, Integer>();
    ngramsMap1.put("how", 1);
    ngramsMap1.put("about", 1);
    ngramsMap1.put("how about", 1);

    LinkedHashMap<String, Integer> ngramsMap2 = new LinkedHashMap<String, Integer>();
    ngramsMap2.put("how", 2);
    ngramsMap2.put("about", 1);
    ngramsMap2.put("them", 1);
    ngramsMap2.put("how about", 1);
    ngramsMap2.put("about them", 1);
    ngramsMap2.put("them how", 1);
    ngramsMap2.put("how about them", 1);
    ngramsMap2.put("about them how", 1);

    LinkedHashMap<String, Integer> expectedMap = new LinkedHashMap<String, Integer>();
    expectedMap.put("how", 4);
    expectedMap.put("about", 3);
    expectedMap.put("them", 1);
    expectedMap.put("how about", 3);
    expectedMap.put("about them", 1);
    expectedMap.put("them how", 1);
    expectedMap.put("how about them", 1);
    expectedMap.put("about them how", 1);

    assertEquals(expectedMap, TFIDFStringHelper.combineMaps(ngramsMap1, ngramsMap1, ngramsMap2));
  }
}