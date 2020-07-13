package com.google.sps.utils;

public class TestUtils {

  /* Compare two JSON strings, strip whitespace and test for equality */
  public static boolean assertEqualsJson(String actual, String expected) {
    actual = actual.replaceAll("\\s", "");
    expected = expected.replaceAll("\\s", "");

    return actual.equals(expected);
  }
}