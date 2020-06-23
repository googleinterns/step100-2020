package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.sps.Objects.Option;

@WebServlet("/poll")
public class PollServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Option option1 = new Option("Option 1");
    option1.addVote("1");
    option1.addVote("2");
    Option option2 = new Option("Option 2");
    option2.addVote("1");
    Option option3 = new Option("Option 3");
    option3.addVote("3");
    List<Option> options = new ArrayList<Option>();
    options.add(option1);
    options.add(option2);
    options.add(option3);
    String json = new Gson().toJson(options);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }
}
