package com.google.sps.search.servlets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.servlets.AuthenticatedServlet;
import com.google.sps.servlets.ServletHelper;

@WebServlet("/name-data")
public class NameData extends AuthenticatedServlet {

  int count;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    try {
      FileReader fileReader = new FileReader("../../data/trie");
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String initial = bufferedReader.readLine();
      System.out.println("initialllll");
      count = Integer.parseInt(initial);
      return;
    } catch (FileNotFoundException e) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      System.err.println("Cannot read from file");
    } catch (NumberFormatException e) {
      System.err.println("Cannot parse to integer");
    }
    String initial = "0";
    try {
      System.out.println("parsing");
      count = Integer.parseInt(initial);
      return;
    } catch (NumberFormatException e) {
      return;
    }
  }

  @Override
  public void destroy() {
    this.saveState();
  }

  public void saveState() {
    try {
      FileWriter fileWriter = new FileWriter("../../data/trie");
      String initial = Integer.toString(count);
      fileWriter.write(initial, 0, initial.length());
      fileWriter.close();
      System.out.println("Successfully written to file");
      return;
    } catch (IOException e) {
      System.err.println("Cannot write to Init Destroy Counter");
    }
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    //    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    //    Query query = new Query("User");
    //    PreparedQuery pq = datastore.prepare(query);
    //    List<String> names = new ArrayList<String>();
    //    for (Entity userEntity : pq.asIterable()) {
    //      String firstName = (String) userEntity.getProperty("firstName");
    //      String lastName = (String) userEntity.getProperty("lastName");
    //      String name = firstName + " " + lastName;
    //      names.add(name);
    //    }
    //    return names;
    count++;
    ServletHelper.write(response, count, "application/json");
    System.out.println("This servlet has been accessed " + count + " times");
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) {}
}
