package com.google.sps.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvReader {

  public CsvReader() {}

  public List<String> parseCorpus(String file) {
    List<String> names = new ArrayList<String>();
    Scanner in = null;
    try {
      in = new Scanner(new File(file));
    } catch (FileNotFoundException e) {
      System.err.println("File does not exist.");
      return null;
    }
    // Go past csv header
    if (in.hasNextLine()) {
      in.nextLine();
    }
    while (in.hasNextLine()) {
      String nextLine = in.nextLine().toUpperCase().replaceAll(",", " ");
      names.add(nextLine);
    }
    in.close();
    return names;
  }
}
