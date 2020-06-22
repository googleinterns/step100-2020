package com.google.sps.data;


public final class Challenge{

    private final String challengeName;
    private final long timestamp;
    private final Badge badge;
    private final ArrayList<User> usersCompleted;

    public Challenge(String challengeName, long timestamp, Badge badge, ArrayList<User> usersCompleted) {
        this.timestamp = timestamp;
        this.challengeName = challengeName;
        this.badge = badge;
        this.usersCompleted = usersCompleted;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public Badge getBadge() {
        return badge;
    }

    public ArrayList<User> usersCompleted() {
        return usersCompleted;
    }
}
