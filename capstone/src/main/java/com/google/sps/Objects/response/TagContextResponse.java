package com.google.sps.Objects.response;

/**
 * Used as a wrapper class to contain information needed to convert to JSON to pass to frontend.
 * Includes the type of data for context, the surrounding text, and the actual tag's text.
 */
public class TagContextResponse {

  private String type;
  private String text;
  private String tagText;

  /**
   * Constructor to set instance variables.
   *
   * @param type List of Option objects
   * @param text The text surrounding a tag to provide context for its usage.
   * @param tagText The tag the user wanted the context of.
   */
  public TagContextResponse(String type, String text, String tagText) {
    this.type = type;
    this.text = text;
    this.tagText = tagText;
  }
}
