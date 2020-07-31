package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.HashSet;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import java.io.PrintWriter;
import com.google.sps.Objects.QuadTree;
import com.google.sps.Objects.Coordinate;
import com.google.sps.Objects.GroupLocation;
import com.google.sps.Objects.Location;
import com.google.sps.Objects.BoundingBox;
import error.ErrorHandler;

/*
 * Parses a TSV file of fake group data and populates the Datastore with Group and Post entities.
*/
@WebServlet("/create-quadtree")
public class QuadTreeServlet extends HttpServlet {

  private QuadTree quadTree;
  private final String QUADTREE_FILE = "../../data/quadtree";
  private final String FAST_FOOD_DATASET = "/WEB-INF/fast-food-location.csv";

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    this.getQuadTreeFromFile();
  }

  @Override
  public void destroy() {
    this.saveState();
  }

  private void getQuadTreeFromFile() {
    try {
      FileInputStream fileInput = new FileInputStream(new File(QUADTREE_FILE));
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);
      quadTree = (QuadTree) objectInput.readObject();

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

    quadTree = new QuadTree(
        /*NODE_CAPACITY*/ 4, 
        /*level*/ 0, 
        /*BoudingBox*/ new BoundingBox(25.641526, -130.429688, 50.847573, -66.269531));
    populateQuadTree(quadTree);
    saveState();
  }

  private void populateQuadTree(QuadTree quadTree) {
    Scanner scanner = new Scanner(
      getServletContext().getResourceAsStream(FAST_FOOD_DATASET));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
      assert fields.length == 9;
      Location newLocation = new Location(
        /* location name */ fields[7], 
        /* address */ fields[0], 
        /* coordinate*/ new Coordinate(Double.valueOf(fields[4]), Double.valueOf(fields[5])), 
        /*. distance */ 0.0);
      quadTree.insert(newLocation);
    }

    scanner.close();
  }

  private void saveState() {
    FileOutputStream fileOutputStream;
    ObjectOutputStream objectOutputStream;
    try {
      fileOutputStream = new FileOutputStream(new File(QUADTREE_FILE));
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(quadTree);

      objectOutputStream.close();
      fileOutputStream.close();
      System.out.println("successfully written to file");
    } catch (FileNotFoundException e1) {
      System.err.println("File does not exist");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.getQuadTreeFromFile();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Long groupId = Long.parseLong(request.getParameter("groupId"));

    GroupLocation groupLocation = new GroupLocation(groupId);
    Coordinate midPoint = groupLocation.findGroupMidPoint(groupId);
    Location groupMidPoint = new Location("Group Midpoint", "", midPoint, 0.0);

    // Create list of closest 20 locations, add midPoint to front of list
    ArrayList<Location> closest20Locations = quadTree.findKNearestNeighbors(groupMidPoint, 20);
    closest20Locations.add(0, groupMidPoint);

    addCentralLocationIdsToGroup(response, groupId, datastore, closest20Locations);
  }

  private void addCentralLocationIdsToGroup(HttpServletResponse response, Long groupId, DatastoreService datastore, ArrayList<Location> closest20Locations) throws IOException {
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    ArrayList<Long> locationIds = new ArrayList<Long>();

    for (Location loc: closest20Locations) {
      Entity locationEntity = loc.toEntity();
      datastore.put(locationEntity);
      locationIds.add(locationEntity.getKey().getId());
    }

    groupEntity.setProperty("locationIds", locationIds);
    datastore.put(groupEntity);
  }

}
