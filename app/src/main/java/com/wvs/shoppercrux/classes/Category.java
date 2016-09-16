package com.wvs.shoppercrux.classes;

/**
 * Created by root on 9/8/16.
 */
public class Category {

    private String id,Name;

    public Category(String id, String name){

        this.setId(id);
        this.setName(name);
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }


}
