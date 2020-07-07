package com.google.sps.Objects;

import java.util.ArrayList;

public final class HelloWorld {

  private final String helloWorld;
  private ArrayList<String> users;

  public HelloWorld(String helloWorld, ArrayList<String> users) {
    this.helloWorld = helloWorld;
    this.users = users;
  }

  public String getHelloWorld() {
    return this.helloWorld;
  }

  public ArrayList<String> getUsers() {
    return users;
  }
}