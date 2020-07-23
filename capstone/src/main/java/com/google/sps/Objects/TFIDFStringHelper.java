package com.google.sps.Objects;

import java.util.LinkedHashMap;
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
    input = input.toLowerCase();
    // Regex identifies all punctuation except apostrophes.
    input = input.replaceAll("[\\p{P}&&[^']]", "");
    return input;  
  }

  /**
   * Splits up a given string into n-grams, counting their frequency along the way.
   * Returns a LinkedHashMap containing single words (unigrams), bigrams, and trigrams,
   * mapped with each term's freqency count.
   */
  public static LinkedHashMap<String, Integer> ngramTokenizer(String input) {
    input = sanitize(input);
    LinkedHashMap<String, Integer> ngrams = new LinkedHashMap<String, Integer>();
    String[] words = input.split(" ");

    for (int n = 1; n <= 3; n++) {
      for (int i = 0; i < words.length - n + 1; i++) {
        String ngram = concat(words, i, n);
        Integer count = ngrams.get(ngram);
        if (count == null) {
          ngrams.put(ngram, 1);
        } else {
          ngrams.put(ngram, count + 1);
        }
      }
    }

    return ngrams;
  }

  /**
   * Helper method for {@code ngramTokenizer()}. Concatenates words to form appropriate n-gram.
   */
  public static String concat(String[] words, int startIndex, int n) {
    StringBuilder sb = new StringBuilder();
    for (int i = startIndex; i < startIndex + n; i++) {
      if (i > startIndex) {
        sb.append(" ");
      }
      sb.append(words[i]);
    }
    return sb.toString();
  }
}