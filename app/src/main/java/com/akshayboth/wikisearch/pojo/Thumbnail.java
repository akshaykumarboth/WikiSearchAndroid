package com.akshayboth.wikisearch.pojo;

import java.io.Serializable;

/**
 * Created by akshayboth on 01/02/18.
 */


public class Thumbnail implements Serializable
{
    private String source;
    private int width;
    private int height;

    public Thumbnail(String source, int width, int height) {
        this.source = source;
        this.width = width;
        this.height = height;
    }

    public void setSource(String source){
        this.source = source;
    }
    public String getSource(){
        return this.source;
    }

    public void setWidth(int width){
        this.width = width;
    }
    public int getWidth(){
        return this.width;
    }

    public void setHeight(int height){
        this.height = height;
    }
    public int getHeight(){
        return this.height;
    }
}