package com.google.sps.Objects;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * A class containing String manipulation helper methods, used when calculating TF-IDF.
 */
public class TFIDFStringHelper {
  
  /**
   * "Sanitizes" a String so it can be analyzed.
   * Remove all punctuation and convert String to lowercase.
   */
  public static String sanitize(String input) {
    if (input ==  null || input.length() == 0) {
      return input;
    }

    input = input.toLowerCase();
    // TO-DO: Handle splitting up sentences/phrases into multiple Strings
    input = removeUrls(input);
    // Regex identifies punctuation except apostrophes.
    input = input.replaceAll("[\\p{P}&&[^']]", " ");
    input = removeExtraWhitespaces(input);
  
    return input;  
  }

  /**
   * Helper method for {@code sanitize()}. Removes urls from a String.
   */
  public static String removeUrls(String input) {
    input = input.replaceAll("[\\S]+://[\\S]+", "");
    input = input.replaceAll("www.[\\S]+", "");
    return input;
  }

  /**
   * Helper method for {@code sanitize()}. Removes extra whitespace from a String.
   */
  public static String removeExtraWhitespaces(String input) {
    input = input.replaceAll(" +", " ");
    input = input.trim();
    return input;
  }

  /**
   * Splits up a given string into n-grams, counting their frequency along the way.
   * Returns a LinkedHashMap containing single words (unigrams), bigrams, and trigrams,
   * mapped with each term's freqency count.
   */
  public static LinkedHashMap<String, Integer> ngramTokenizer(String input) {
    LinkedHashMap<String, Integer> ngrams = new LinkedHashMap<String, Integer>();

    if (input ==  null || input.length() == 0) {
      return ngrams;
    }

    input = sanitize(input);
    String[] words = input.split(" ");

    int nLength = (words.length >= 3) ? 3 : words.length;

    for (int n = 1; n <= nLength; n++) {
      for (int i = 0; i < words.length - n + 1; i++) {
        String ngram = concat(words, i, n);
        Integer count = ngrams.getOrDefault(ngram, 0);
        ngrams.put(ngram, count + 1);
      }
    }

    return ngrams;
  }

  /**
   * Helper method for {@code ngramTokenizer()}. Concatenates words to form appropriate n-gram.
   */
  public static String concat(String[] words, int startIndex, int n) {
    String[] nWords = Arrays.copyOfRange(words, startIndex, startIndex + n);
    return String.join(" ", nWords);
  }

  /**
   * Combines two or more HashMaps of n-grams into a single map.
   */
  public static LinkedHashMap<String, Integer> combineMaps(
      ArrayList<LinkedHashMap<String, Integer>> maps) {

    LinkedHashMap<String, Integer> ngrams = new LinkedHashMap<String, Integer>();
    for (LinkedHashMap<String, Integer> map : maps) {
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        Integer count = ngrams.getOrDefault(entry.getKey(), 0);
        ngrams.put(entry.getKey(), count + entry.getValue());
      }
    }
    return ngrams;
  }
}