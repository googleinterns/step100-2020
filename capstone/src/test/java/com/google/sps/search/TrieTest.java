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
    Set<Character> keySet = trie.getChildren().keySet();
    Character firstLetter = keySet.iterator().next();
    Map<Character, Trie> lChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<Character> keySet2 = lChildren.keySet();
    Character secondLetter = keySet2.iterator().next();
    Map<Character, Trie> uChildren = lChildren.get(secondLetter).getChildren();
    Set<Character> keySet3 = uChildren.keySet();
    Character thirdLetter = keySet3.iterator().next();
    Map<Character, Trie> cChildren = uChildren.get(thirdLetter).getChildren();
    Set<Character> keySet4 = cChildren.keySet();
    Character forthLetter = keySet4.iterator().next();
    Set<String> fullNames = cChildren.get(forthLetter).getFullNames();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, (Character) 'L');
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, (Character) 'U');
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, (Character) 'C');
    assertEquals(keySet4.size(), 1);
    assertEquals(forthLetter, (Character) 'Y');
    assertTrue(cChildren.get('Y').getIsName());
    assertEquals(fullNames.size(), 1);
    assertTrue(fullNames.contains("Lucy Qu"));
  }

  @Test
  public void insertSameFirstNameTest() {
    trie.insert("Lucy", "Lucy Qu");
    trie.insert("Lucy", "Lucy Chen");
    Set<Character> keySet = trie.getChildren().keySet();
    Character firstLetter = keySet.iterator().next();
    Map<Character, Trie> lChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<Character> keySet2 = lChildren.keySet();
    Character secondLetter = keySet2.iterator().next();
    Map<Character, Trie> uChildren = lChildren.get(secondLetter).getChildren();
    Set<Character> keySet3 = uChildren.keySet();
    Character thirdLetter = keySet3.iterator().next();
    Map<Character, Trie> cChildren = uChildren.get(thirdLetter).getChildren();
    Set<Character> keySet4 = cChildren.keySet();
    Character forthLetter = keySet4.iterator().next();
    Set<String> fullNames = cChildren.get(forthLetter).getFullNames();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, (Character) 'L');
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, (Character) 'U');
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, (Character) 'C');
    assertEquals(keySet4.size(), 1);
    assertEquals(forthLetter, (Character) 'Y');
    assertTrue(cChildren.get('Y').getIsName());
    assertEquals(fullNames.size(), 2);
    assertTrue(fullNames.contains("Lucy Qu"));
    assertTrue(fullNames.contains("Lucy Chen"));
  }

  /** Handles case in which duplicate names are inserted into trie. */
  @Test
  public void insertDuplicateNamesTest() {
    trie.insert("Lucy", "Lucy Qu");
    trie.insert("Lucy", "Lucy Qu");
    Set<Character> keySet = trie.getChildren().keySet();
    Character firstLetter = keySet.iterator().next();
    Map<Character, Trie> lChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<Character> keySet2 = lChildren.keySet();
    Character secondLetter = keySet2.iterator().next();
    Map<Character, Trie> uChildren = lChildren.get(secondLetter).getChildren();
    Set<Character> keySet3 = uChildren.keySet();
    Character thirdLetter = keySet3.iterator().next();
    Map<Character, Trie> cChildren = uChildren.get(thirdLetter).getChildren();
    Set<Character> keySet4 = cChildren.keySet();
    Character forthLetter = keySet4.iterator().next();
    Set<String> fullNames = cChildren.get(forthLetter).getFullNames();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, (Character) 'L');
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, (Character) 'U');
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, (Character) 'C');
    assertEquals(keySet4.size(), 1);
    assertEquals(forthLetter, (Character) 'Y');
    assertTrue(cChildren.get('Y').getIsName());
    assertEquals(fullNames.size(), 1);
    assertTrue(fullNames.contains("Lucy Qu"));
  }

  /**
   * Tests that inserting Rose (full name Rose Smith) and Rosie (full name Rosie Brown) into trie
   * will result in the correct trie structure.
   */
  @Test
  public void insertOverlappingNamesTest() {
    trie.insert("Rose", "Rose Smith");
    trie.insert("Rosie", "Rosie Brown");
    Set<Character> keySet = trie.getChildren().keySet();
    Character firstLetter = keySet.iterator().next();
    Map<Character, Trie> rChildren = trie.getChildren().get(firstLetter).getChildren();
    Set<Character> keySet2 = rChildren.keySet();
    Character secondLetter = keySet2.iterator().next();
    Map<Character, Trie> oChildren = rChildren.get(secondLetter).getChildren();
    Set<Character> keySet3 = oChildren.keySet();
    Character thirdLetter = keySet3.iterator().next();
    Map<Character, Trie> sChildren = oChildren.get(thirdLetter).getChildren();
    Set<Character> keySet4 = sChildren.keySet();
    Map<Character, Trie> iChildren = sChildren.get('I').getChildren();

    assertEquals(keySet.size(), 1);
    assertEquals(firstLetter, (Character) 'R');
    assertEquals(keySet2.size(), 1);
    assertEquals(secondLetter, (Character) 'O');
    assertEquals(keySet3.size(), 1);
    assertEquals(thirdLetter, (Character) 'S');
    assertEquals(keySet4.size(), 2);
    assertTrue(keySet4.contains('I'));
    assertTrue(keySet4.contains('E'));
    assertTrue(sChildren.get('E').getIsName());
    assertFalse(sChildren.get('I').getIsName());
    assertEquals(iChildren.size(), 1);
    assertTrue(iChildren.containsKey('E'));
    assertTrue(iChildren.get('E').getIsName());
  }

  /** Inserts two names into the trie, where one name is the substring of other names. */
  @Test
  public void insertOneNameIsSubstringTest() {
    trie.insert("Qu", "Lucy Qu");
    trie.insert("Qui", "Jane Qui");
    trie.insert("Squirrel", "Jane Squirrel");
    Set<Character> keySet = trie.getChildren().keySet();
    Map<Character, Trie> qChildren = trie.getChildren().get('Q').getChildren();
    Map<Character, Trie> sChildren = trie.getChildren().get('S').getChildren();
    Trie uTrie = qChildren.get('U');

    assertEquals(keySet.size(), 2);
    assertTrue(keySet.contains('Q'));
    assertTrue(keySet.contains('S'));
    assertTrue(sChildren.containsKey('Q'));
    assertEquals(qChildren.size(), 1);
    assertTrue(qChildren.containsKey('U'));
    assertTrue(uTrie.getIsName());
    assertEquals(uTrie.getFullNames().size(), 1);
    assertTrue(uTrie.getFullNames().contains("Lucy Qu"));
    assertEquals(uTrie.getChildren().size(), 1);
    assertTrue(uTrie.getChildren().containsKey('I'));
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
  public void searchWithPrefixSameNameTest() {
    trie.insert("Lucy", "Lucy Qu");
    trie.insert("Lucy", "Lucy Liu");
    trie.insert("Ball", "Lucille Ball");

    Set<String> names = trie.searchWithPrefix("Luc", "Luc");

    assertEquals(names.size(), 2);
    assertTrue(names.contains("Lucy Qu"));
    assertTrue(names.contains("Lucy Liu"));
    assertFalse(names.contains("Lucille Ball"));
  }

  /** Tests for search with prefix when one name in the trie is the substring of another name. */
  @Test
  public void searchWithPrefixOneNameIsSubstringTest() {
    trie.insert("Qu", "Lucy Qu");
    trie.insert("Quincy", "Jane Quincy");
    trie.insert("Squirrel", "Jane Squirrel");

    Set<String> names = trie.searchWithPrefix("Qu", "Qu");

    assertEquals(names.size(), 2);
    assertTrue(names.contains("Lucy Qu"));
    assertTrue(names.contains("Jane Quincy"));
    assertFalse(names.contains("Jane Squirrel"));
  }

  @Test
  public void searchWithPrefixNoMatchTest() {
    trie.insert("Terence", "Terence McKenna");

    Set<String> names = trie.searchWithPrefix("Aud", "Aud");

    assertEquals(names.size(), 0);
  }

  @Test
  public void searchLedTest() {
    trie.insert("Jack", "Jack Rose");
    trie.insert("Jane", "Jane Doe");
    trie.insert("Joe", "Joe Qu");
    trie.insert("John", "John Liu");
    trie.insert("Johnny", "Johnny Sterling");

    Set<String> suggestions = trie.searchLed("JANE");

    System.out.println(suggestions);
    assertEquals(3, suggestions.size());
    assertTrue(suggestions.contains("Jack Rose"));
    assertTrue(suggestions.contains("Jane Doe"));
    assertTrue(suggestions.contains("Joe Qu"));
  }
}
