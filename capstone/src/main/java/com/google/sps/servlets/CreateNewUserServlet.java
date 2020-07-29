// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.User;
import com.google.sps.search.SearchPredictor;

@WebServlet("/createNewUser")
public class CreateNewUserServlet extends AuthenticatedServlet {

  private SearchPredictor searchPredictor;
  private final String TRIE_FILE = "../../data/trie";

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String first = request.getParameter("first");
    String last = request.getParameter("last");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");
    double latitude = Double.parseDouble(request.getParameter("latitude"));
    double longitude = Double.parseDouble(request.getParameter("longitude"));
    ArrayList<String> interests = getInterests(request);

    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    User user = new User(userId,
                        first,
                        last, 
                        /* email= */ userService.getCurrentUser().getEmail(),
                        phone, 
                        /* profilePic= */ "", 
                        /* address= */ address, 
                        /* latitude= */ latitude, 
                        /* longitude= */ longitude, 
                        /* badges= */ new LinkedHashSet<Badge>(), 
                        /* groups= */ new LinkedHashSet<Long>(), 
                        interests);
    datastore.put(user.toEntity());

    this.insertNameIntoTries(first, last);
    this.saveState();
  }

  private void insertNameIntoTries(String firstName, String lastName) {
    try {
      FileInputStream fileInput = new FileInputStream(new File(TRIE_FILE));
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);
      searchPredictor = (SearchPredictor) objectInput.readObject();
      searchPredictor.insertName(firstName, lastName);

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
    searchPredictor = new SearchPredictor();
    searchPredictor.insertName(firstName, lastName);
  }

  private void saveState() {
    FileOutputStream fileOutputStream;
    try {
      fileOutputStream = new FileOutputStream(new File(TRIE_FILE));
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(searchPredictor);

      objectOutputStream.close();
      fileOutputStream.close();
      System.out.println("successfully written to file");
    } catch (FileNotFoundException e1) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      System.err.println("Cannot write to file");
    }
  }

  /** Gets the list of interests entered by user. */
  private ArrayList<String> getInterests(HttpServletRequest request) {
    // Get input from the form.
    String interests = request.getParameter("interests");
    // Trim any whitespace immediately following a comma.
    interests = interests.replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

    // Convert the input to an array list.
    String[] interestsArray = interests.split(",");
    ArrayList<String> interestsList = new ArrayList<String>();
    interestsList.addAll(Arrays.asList(interestsArray));

    return interestsList;
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
