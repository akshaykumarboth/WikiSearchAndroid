package com.akshayboth.wikisearch.pojo;

import java.io.Serializable;

/**
 * Created by akshayboth on 01/02/18.
 */


public class Root implements Serializable
{
    private boolean batchcomplete;
    private Query query;

    public void setBatchcomplete(boolean batchcomplete){
        this.batchcomplete = batchcomplete;
    }

    public boolean getBatchcomplete(){
        return this.batchcomplete;
    }

    public void setQuery(Query query){
        this.query = query;
    }
    public Query getQuery(){
        return this.query;
    }
}
