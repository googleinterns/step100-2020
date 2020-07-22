package com.google.sps.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TrieTest {

  private Trie trie;

  @Before
  public void setUp() {
    trie = new Trie();
  }

  /** Test that inserting one name will result in the correct trie structure. */
  @Test
  public void insertNameTest() {
    trie.insert("Lucy", "Lucy Qu");
    Set<String> keySet = trie.getChildren().keySet();
    String firstLetter = keySet.iterator().next();
    Map<String, Trie> lChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<String> keySet2 = lChildren.keySet();
    String secondLetter = keySet2.iterator().next();
    Map<String, Trie> uChildren = lChildren.get(secondLetter).getChildren();
    Set<String> keySet3 = uChildren.keySet();
    String thirdLetter = keySet3.iterator().next();
    Map<String, Trie> cChildren = uChildren.get(thirdLetter).getChildren();
    Set<String> keySet4 = cChildren.keySet();
    String forthLetter = keySet4.iterator().next();
    Map<String, Trie> yChildren = cChildren.get(forthLetter).getChildren();
    Set<String> keySet5 = yChildren.keySet();
    String fullName = keySet5.iterator().next();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, "L");
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, "U");
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, "C");
    assertEquals(keySet4.size(), 1);
    assertEquals(forthLetter, "Y");
    assertEquals(keySet5.size(), 1);
    assertEquals(fullName, "Lucy Qu");
    assertTrue(cChildren.get("Y").getIsEnd());
  }

  /** Handles case in which duplicate names are inserted into trie. */
  @Test
  public void insertDuplicateNamesTest() {
    trie.insert("Lucy", "Lucy Qu");
    trie.insert("Lucy", "Lucy Qu");
    Set<String> keySet = trie.getChildren().keySet();
    String firstLetter = keySet.iterator().next();
    Map<String, Trie> lChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<String> keySet2 = lChildren.keySet();
    String secondLetter = keySet2.iterator().next();
    Map<String, Trie> uChildren = lChildren.get(secondLetter).getChildren();
    Set<String> keySet3 = uChildren.keySet();
    String thirdLetter = keySet3.iterator().next();
    Map<String, Trie> cChildren = uChildren.get(thirdLetter).getChildren();
    Set<String> keySet4 = cChildren.keySet();
    String forthLetter = keySet4.iterator().next();
    Map<String, Trie> yChildren = cChildren.get(forthLetter).getChildren();
    Set<String> keySet5 = yChildren.keySet();
    String fullName = keySet5.iterator().next();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, "L");
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, "U");
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, "C");
    assertEquals(keySet4.size(), 1);
    assertEquals(forthLetter, "Y");
    assertEquals(keySet5.size(), 1);
    assertEquals(fullName, "Lucy Qu");
    assertTrue(cChildren.get("Y").getIsEnd());
  }
  /**
   * Tests that inserting Rose (full name Rose Smith) and Rosie (full name Rosie Brown) into trie
   * will result in the correct trie structure.
   */
  @Test
  public void insertOverlappingNamesTest() {
    trie.insert("Rose", "Rose Smith");
    trie.insert("Rosie", "Rosie Brown");
    Set<String> keySet = trie.getChildren().keySet();
    String firstLetter = keySet.iterator().next();
    Map<String, Trie> rChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<String> keySet2 = rChildren.keySet();
    String secondLetter = keySet2.iterator().next();
    Map<String, Trie> oChildren = rChildren.get(secondLetter).getChildren();
    Set<String> keySet3 = oChildren.keySet();
    String thirdLetter = keySet3.iterator().next();
    Map<String, Trie> sChildren = oChildren.get(thirdLetter).getChildren();
    Set<String> keySet4 = sChildren.keySet();
    Map<String, Trie> iChildren = sChildren.get("I").getChildren();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, "R");
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, "O");
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, "S");
    assertEquals(keySet4.size(), 2);
    assertTrue(keySet4.contains("I"));
    assertTrue(keySet4.contains("E"));
    assertTrue(sChildren.get("E").getIsEnd());
    assertFalse(sChildren.get("I").getIsEnd());
    assertEquals(iChildren.size(), 1);
    assertTrue(iChildren.keySet().contains("E"));
    assertTrue(iChildren.get("E").getIsEnd());
  }

  @Test
  public void searchWithPrefixTest() {
    trie.insert("Lucy", "Lucy Qu");
    trie.insert("Lucille", "Lucille Ball");
    trie.insert("Lucie", "Lucie White");
    trie.insert("Lilly", "Lilly Singh");

    Set<String> names = trie.searchWithPrefix("Luc", "Luc");

    assertEquals(names.size(), 3);
    assertTrue(names.contains("Lucy Qu"));
    assertTrue(names.contains("Lucille Ball"));
    assertTrue(names.contains("Lucie White"));
    assertFalse(names.contains("Lilly Singh"));
  }

  @Test
  public void searchWithPrefixNoMatchTest() {
    trie.insert("Terence", "Terence McKenna");

    Set<String> names = trie.searchWithPrefix("Aud", "Aud");

    assertEquals(names.size(), 0);
  }
}
