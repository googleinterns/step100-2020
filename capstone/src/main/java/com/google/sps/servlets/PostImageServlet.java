package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@WebServlet("/post-image-servlet")
public class PostImageServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Long groupId = Long.parseLong(request.getParameter("groupId"));

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String url = "/group-post?groupId=" + groupId;
    String uploadUrl = blobstoreService.createUploadUrl(url);

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}
