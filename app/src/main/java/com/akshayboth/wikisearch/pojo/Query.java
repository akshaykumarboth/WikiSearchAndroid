package com.akshayboth.wikisearch.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by akshayboth on 01/02/18.
 */

public class Query implements Serializable
{
    private List<Redirects> redirects;

    private List<WikiPage> pages;

    public void setRedirects(List<Redirects> redirects){
        this.redirects = redirects;
    }
    public List<Redirects> getRedirects(){
        return this.redirects;
    }
    public void setPages(List<WikiPage> pages){
        this.pages = pages;
    }
    public List<WikiPage> getPages(){
        return this.pages;
    }
}