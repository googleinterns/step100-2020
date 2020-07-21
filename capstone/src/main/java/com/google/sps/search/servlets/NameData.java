package com.google.sps.search.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.sps.search.Trie;
import com.google.sps.servlets.AuthenticatedServlet;
import com.google.sps.servlets.ServletHelper;

@WebServlet("/name-data")
public class NameData extends AuthenticatedServlet {

  Trie firstNameTrie;
  Trie lastNameTrie;
  String trieFile = "../../data/trie";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    try {
      FileInputStream fileInput = new FileInputStream(new File(trieFile));
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);
      firstNameTrie = ((List<Trie>) objectInput.readObject()).get(0);
      lastNameTrie = ((List<Trie>) objectInput.readObject()).get(1);

      fileInput.close();
      objectInput.close();
      return;
    } catch (FileNotFoundException e) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      System.err.println("Cannot read from file");
    } catch (ClassNotFoundException e) {
      System.err.println("Class not found");
    }
    firstNameTrie = new Trie();
    lastNameTrie = new Trie();
  }

  @Override
  public void destroy() {
    this.saveState();
  }

  public void saveState() {
    FileOutputStream fileOutputStream;
    try {
      fileOutputStream = new FileOutputStream(new File(trieFile));
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      List<Trie> triesList = new ArrayList<Trie>();
      triesList.add(firstNameTrie);
      triesList.add(lastNameTrie);
      objectOutputStream.writeObject(triesList);

      objectOutputStream.close();
      fileOutputStream.close();
      System.out.println("successfully written to file");
    } catch (FileNotFoundException e1) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      System.err.println("Cannot write to file");
    }
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    firstNameTrie.incrementCounter();
    ServletHelper.write(response, firstNameTrie, "application/json");
    System.out.println("This servlet has been accessed " + firstNameTrie.getCount() + " times");
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) {}
}
