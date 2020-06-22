package com.google.sps.data;


public final class User{

    private final int userId;
    private final String name;
    private final String email;
    private final ArrayList<Badge> badges;
    private final ArrayList<Group> groups;
    private final ArrayList<String> interests;
 
    public User(int userId, String name, String email, ArrayList<Badge> badges, ArrayList<Group> groups, ArrayList<String> interests) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.badges = badges;
        this.groups = groups;
        this.interests = interests;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Badge> getBadges() {
        return badges;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }
    
    public ArrayList<String> getInterests() {
        return interests;
    }
}


