package com.google.sps.data;

public final class Poll{

    ArrayList<Option> options;

    public Poll(){
        this.options = new ArrayList<Option>();
    }

    public void addOption(Option option){
        this.options.add(option);
    }

    public ArrayList<Option> getOptions() {
        return options;
    }
}