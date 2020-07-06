package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import error.ErrorHandler;

/**
 * This class writes the JSON object to the response object's outut stream.
 *
 * @author lucyqu
 */
public class ServletHelper {

  /**
   * Converts JSON object to String and write to response writer along with content type.
   *
   * @param response HttpServletResponse
   * @param servletResponse JSON object
   * @param type content type
   * @throws IOException exception thrown if cannot write to response
   */
  public static void write(HttpServletResponse response, Object servletResponse, String type)
      throws IOException {
    String json = "";
    if (servletResponse != null) {
      json = new Gson().toJson(servletResponse);
    }
    response.setContentType(type);
    try {
      response.getWriter().println(json);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      ErrorHandler.sendError(response, "Cannot write to response");
    }
  }
}
