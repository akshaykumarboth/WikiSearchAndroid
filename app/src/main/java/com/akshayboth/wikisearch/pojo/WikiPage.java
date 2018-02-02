package com.akshayboth.wikisearch.pojo;

import java.io.Serializable;

/**
 * Created by akshayboth on 01/02/18.
 */


public class WikiPage implements Serializable  {

    private int pageid;
    private int ns;
    private String title;
    private int index;
    private Thumbnail thumbnail;
    private Terms terms;

    public void setPageid(int pageid){
        this.pageid = pageid;
    }
    public int getPageid(){
        return this.pageid;
    }
    public void setNs(int ns){
        this.ns = ns;
    }
    public int getNs(){
        return this.ns;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setIndex(int index){
        this.index = index;
    }
    public int getIndex(){
        return this.index;
    }
    public void setThumbnail(Thumbnail thumbnail){
        this.thumbnail = thumbnail;
    }
    public Thumbnail getThumbnail(){
        return this.thumbnail;
    }
    public void setTerms(Terms terms){
        this.terms = terms;
    }
    public Terms getTerms(){
        return this.terms;
    }
}

//

