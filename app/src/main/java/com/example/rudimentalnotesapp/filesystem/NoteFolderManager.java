package com.example.rudimentalnotesapp.filesystem;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NoteFolderManager {

    static String rootPath = MainActivity.rootDir.getPath();


    //actually creates it IN STORAGE in this app's rootDir path
    public static NoteFolder createNoteFolder(String name){

        NoteFolder noteFolder = new NoteFolder(rootPath+"/"+name);

        //call overridden version of mkdir
        //that creates the notesFile too
        noteFolder.mkdir();
        return noteFolder;
    }


    //GENERAL NAME: noteFolderX where X is an int
    //find next available name (integer ID part) for
    //new NoteFolder
    public static NoteFolder createNoteFolder(){
        ArrayList<NoteFolder> noteFolders = getAllNoteFolders();
        if(noteFolders.size()==0){
            return createNoteFolder("noteFolder0");
        }

        //sort: highest ID number first index
        Collections.sort(noteFolders, new Comparator<NoteFolder>() {
            @Override
            public int compare(NoteFolder o1, NoteFolder o2) {
                return o2.getID() - o1.getID();
            }
        });


        int nextID = noteFolders.get(0).getID()+1;
        Log.d("NOTE_FOLDER_TEST", nextID+"");


        return createNoteFolder("noteFolder"+nextID);


    }

    //get all existing note folders
    public static ArrayList<NoteFolder> getAllNoteFolders(){
        ArrayList<NoteFolder> noteFolders = new ArrayList<NoteFolder>();

        for(File file : FileIO.getMyFiles()){
            if(file.getName().contains("noteFolder")){
                noteFolders.add(new NoteFolder(file.getPath()));
                Log.d("NOTE_FOLDER_TEST", file.getName());
            }
        }

        //sort note folders by date-time last edited
        Collections.sort(noteFolders, new Comparator<NoteFolder>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(NoteFolder o1, NoteFolder o2) {
                return o2.getDateLastModified().compareTo(o1.getDateLastModified());
            }
        });
        return noteFolders;
    }

    //get a single note folder by its ID
    public static NoteFolder getNoteFolder(int ID){
        NoteFolder noteFolder = new NoteFolder(rootPath+"/noteFolder"+ID);
        if(noteFolder.exists()){
            return noteFolder;
        }
        return null;
    }








}
