package com.google.sps.Objects;

import java.util.LinkedHashMap;
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
    // Regex identifies punctuation except apostrophes.
    input = input.replaceAll("[\\p{P}&&[^']]", "");
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
}