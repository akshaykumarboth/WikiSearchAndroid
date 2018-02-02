package com.akshayboth.wikisearch.pojo;

import java.io.Serializable;

/**
 * Created by akshayboth on 01/02/18.
 */

public class Redirects implements Serializable
{
    private int index;

    private String from;

    private String to;

    public void setIndex(int index){
        this.index = index;
    }
    public int getIndex(){
        return this.index;
    }
    public void setFrom(String from){
        this.from = from;
    }
    public String getFrom(){
        return this.from;
    }
    public void setTo(String to){
        this.to = to;
    }
    public String getTo(){
        return this.to;
    }
}
