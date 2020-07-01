package error;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ErrorHandler {

  /**
   * Handles error for Java Servlet and displays that something went wrong.
   *
   * @param response    HttpServletResponse
   * @param errorString error message
   * @throws IOException exception thrown when cannot write to file
   */
  public static void sendError(HttpServletResponse response, String errorString)
      throws IOException {
    response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorString);
    response.getWriter().print("<html><head><title>Oops an error happened!</title></head>");
    response.getWriter().print("<body>Something bad happened uh-oh!</body>");
    response.getWriter().println("</html>");
  }

}
