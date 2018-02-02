package com.akshayboth.wikisearch.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by akshayboth on 01/02/18.
 */

public class Terms implements Serializable
{
    private List<String> description;

    public void setDescription(List<String> description){
        this.description = description;
    }
    public List<String> getDescription(){
        return this.description;
    }
}
