package com.example.rudimentalnotesapp.collections;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.settings.Settings;
import com.example.rudimentalnotesapp.filesystem.FileIO;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.filesystem.NoteFolderManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//this is a collection of note folders.
//a note folder can be linked to more than one collection.
//Each collection has:
//A NAME
//A LIST OF TAGS/ALIASES

public class Collection {

    //this is the name of this Collection
    private String name;

    //this is the list of tags/aliases of this Collection
    private ArrayList<String> tagsList;

    //a reference to the global folder containing info about all saved collections
    static File globalCollectionsFolder = new File(FileIO.getRootDir().getPath()+"/collections");

    //a reference to this Collection's file inside of the global collections directory
    File savedInfoFile;

    public Collection(String name){
        this.name = name;

        //reference to this collection's info file
        savedInfoFile = new File(globalCollectionsFolder.getPath()+"/"+name+".txt");

        //initialize tagsList
        tagsList = new ArrayList<String>();

        //check if this collection already
        //exists in storage. If it does
        //load it (load its tags).
        //Else make a new file for it.
        if(loadInfo()==false){
            //initializes Collection in storage
            saveInfo();
        }

    }


    //does this collection contain a given tag/alias?
    public boolean hasTag(String tag){
        for(String tagInList : tagsList){
            if(tagInList.equals(tag)){
                return true;
            }
        }
        return false;
    }

    //add a new tag/alias
    public void addTag(String tag){

        //don't add duplicate tags
        if(this.hasTag(tag)){
            return;
        }

        //if string is whitespace, don;t add tag
        if(tag.trim().isEmpty()){
            return;
        }

        tagsList.add(tag);
    }

    //save this Collection to storage
    public void saveInfo(){
        //create the global collections folder if it doesn't exist yet
        if(!globalCollectionsFolder.exists()){
            globalCollectionsFolder.mkdir();
        }
        //create this Collection's info file, if it doesn't exist yet
        if(!savedInfoFile.exists()){
            try {
                savedInfoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //this Collection's file's name is gonna be
        //the name of the Collection.
        //And the contents of the file are gonna
        //be the tags. One tag per line.
        String info = "";
        for(String tag : tagsList){
            info+=tag+"\n";
        }
        //write this info string to the info file
        FileIO.writeToFile(savedInfoFile, info);


        //TODO: YOU CAN MAKE THIS BETTER!
        //a new collection has been made or edited.
        //check if you have to add note folders to collection
        if(Settings.isSelfSortingCollectionsOn()){
            Collection.proactiveAddFoldersToCollections();
        }

    }



    //load this Collection from storage.
    //if loading failed, return false;
    public boolean loadInfo(){

        //if info file doesn't exist yet, return false = failed
        if(!savedInfoFile.exists()){
            return false;
        }

        //load all of the Collection tags from the info file
        try{
            String[] tags = FileIO.readFile(savedInfoFile).split("\n");
            for(String tag : tags){
                addTag(tag);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    //find all of the folders that are linked to
    // this collection
    public ArrayList<NoteFolder> getLinkedNoteFolders(){
        ArrayList<NoteFolder> relevantNoteFolders = new ArrayList<NoteFolder>();
        for(NoteFolder noteFolder : NoteFolderManager.getAllNoteFolders()){
            if(noteFolder.isInCollection(this.getName())){
                relevantNoteFolders.add(noteFolder);
            }
        }
        return relevantNoteFolders;
    }

    //delete this collection and remove its mentions
    //from all of the folders
    public void delete(){

        //search for folder references, and remove them
        for(NoteFolder noteFolder : getLinkedNoteFolders()){
            noteFolder.removeFromCollection(getName());
        }

        //delete this collection from storage
        savedInfoFile.delete();
    }




    //get all of the existing collection names
    public static ArrayList<String> getCollectionNames(){
        ArrayList<String> result = new ArrayList<String>();

        try{
            for(File file : globalCollectionsFolder.listFiles()){
                result.add(file.getName().replace(".txt",""));
            }
        }catch (Exception e){

        }

        return result;
    }


    //find an existing collection
    public static Collection getCollection(String name){
        for(File file : globalCollectionsFolder.listFiles()){
            if(file.getName().replace(".txt", "").equals(name)){
                return new Collection(name);
            }
        }

        return null;
    }

    //add note folders that contain a tag (keyword) in their text
    //to a Collection automatically
    public static void proactiveAddFoldersToCollections(){
        //search each note folder for any of the tags of any Collection
        for(NoteFolder noteFolder : NoteFolderManager.getAllNoteFolders()){
                noteFolder.addMeToRelevantCollections();
        }
    }



    //get this Collection's name
    public String getName() {
        return name;
    }
    //get this Collection's tags/aliases
    public ArrayList<String> getTags() {
        return tagsList;
    }
    //get all of the Collection's tags/aliases as a single string (space separated)
    public String getTagsAsString(){
        String buf = "";
        for(String tag : tagsList){
            buf+= tag+" ";
        }
        return buf;
    }

}
