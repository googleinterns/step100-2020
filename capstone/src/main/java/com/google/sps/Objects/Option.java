package com.google.sps.data;

public final class Option {

    private final String text;
    private ArrayList<UserId> votes;

    public Option(String text){
        this.text = text;
        this.votes = new ArrayList<UserId>();
    }

    public String getText(){
        return this.text;
    }

    public void addVote(UserId id){
        this.votes.add(id);
    }

    public ArrayList<UserId> getVotes() {
        return votes;
    }
}