package com.example.rudimentalnotesapp.collections;

//this collection subclass never gets saved to storage.
//it is used to neatly group (temporary) search
//results in a collection.
public class TemporaryCollection extends Collection {

    public TemporaryCollection(String name) {
        super(name);
    }

    @Override
    public void saveInfo() {
        //super.saveInfo();
    }

    @Override
    public boolean loadInfo() {
        //return super.loadInfo();
        return true;
    }




}
