package com.google.sps.search.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.search.SearchPredictor;
import com.google.sps.servlets.AuthenticatedServlet;
import com.google.sps.servlets.ServletHelper;

@WebServlet("/name-data")
public class NameDataServlet extends AuthenticatedServlet {

  private SearchPredictor searchPredictor;
  private final String TRIE_FILE = "../../data/trie";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    this.getSearchPredictorFromFile();
  }

  @Override
  public void destroy() {
    this.saveState();
  }

  private void getSearchPredictorFromFile() {
    FileInputStream fileInput = null;
    ObjectInputStream objectInput = null;

    try {
      fileInput = new FileInputStream(new File(TRIE_FILE));
      objectInput = new ObjectInputStream(fileInput);
      searchPredictor = (SearchPredictor) objectInput.readObject();

    } catch (FileNotFoundException e) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      System.err.println("Cannot read from file");
    } catch (ClassNotFoundException e) {
      System.err.println("Class not found");
    }
    try {
      if (fileInput != null) {
        fileInput.close();
      }
      if (objectInput != null) {
        objectInput.close();
      }
    } catch (IOException e) {
      System.err.println("Cannot close file");
    }
  }

  private void saveState() {
    FileOutputStream fileOutputStream = null;
    ObjectOutputStream objectOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(new File(TRIE_FILE));
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(searchPredictor);

      System.out.println("successfully written to file");
    } catch (FileNotFoundException e1) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      System.err.println("Cannot write to file");
    }

    try {
      if (objectOutputStream != null) {
        objectOutputStream.close();
      }
      if (fileOutputStream != null) {
        fileOutputStream.close();
      }
    } catch (IOException e) {
      System.err.println("Cannot close file");
    }
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    this.getSearchPredictorFromFile();
    if (searchPredictor == null) {
      this.searchPredictor = new SearchPredictor();
    }
    String input = request.getParameter("input").toUpperCase();
    List<String> suggestions = searchPredictor.suggest(input);
    ServletHelper.write(response, suggestions, "application/json");
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) {}
}
