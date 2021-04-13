package com.example.rudimentalnotesapp.filesystem;

import android.util.Log;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class NoteFolderManager {

    //the path to the directory all of the note folders are stored in
    static String rootPath = FileIO.getRootDir().getPath();

    //the list of all currently existing note folders
    static ArrayList<NoteFolder> currentNoteFoldersList = new ArrayList<NoteFolder>();


    //actually creates it IN STORAGE in this app's rootDir path
    public static NoteFolder createNoteFolder(String name){

        NoteFolder newNoteFolder = new NoteFolder(rootPath+"/"+name);

        //call overridden version of mkdir
        //that creates the internal files too
        newNoteFolder.mkdir();


        return newNoteFolder;
    }


    //GENERAL NAME: noteFolderX where X is an int
    //find next available name (integer ID part) for
    //new NoteFolder
    public static NoteFolder createNoteFolder(){

        NoteFolder noteFolder = createNoteFolder("noteFolder"+System.currentTimeMillis());
        Log.d("NOTE_FOLDER_UNIX", noteFolder.getName());
        return noteFolder;

    }

    //get all existing note folders
    public static ArrayList<NoteFolder> getAllNoteFolders(){

        //TODO: optimize this, don't load from storage all the time
        currentNoteFoldersList.clear();
        for(File file : FileIO.getMyFiles()){
            if(file.getName().contains("noteFolder")){
                currentNoteFoldersList.add(new NoteFolder(file.getPath()));
            }
        }

        //sort note folders by date-time last edited
        currentNoteFoldersList = sortNoteFoldersByDateLastModified(currentNoteFoldersList);

        return currentNoteFoldersList;
    }

    //sort note folders by date-time last edited
    public static ArrayList<NoteFolder> sortNoteFoldersByDateLastModified(ArrayList<NoteFolder> noteFolders){
        Collections.sort(noteFolders, new Comparator<NoteFolder>() {
            @Override
            public int compare(NoteFolder o1, NoteFolder o2) {
                return o2.getDateLastModified().compareTo(o1.getDateLastModified());
            }
        });
        return noteFolders;
    }



    //get a single note folder by its ID
    public static NoteFolder getNoteFolder(long ID){

        for(NoteFolder noteFolder : currentNoteFoldersList){
            if(noteFolder.getID()==ID){
                return noteFolder;
            }
        }

        //if you don't find it in the current ones

        NoteFolder noteFolder = new NoteFolder(rootPath+"/noteFolder"+ID);
        if(noteFolder.exists()){
            return noteFolder;
        }
        return null;


    }



    //compact note folders in one single new big notes folder,
    //then delete old smaller note folders.
    public static NoteFolder compactNoteFolders(ArrayList<NoteFolder> noteFoldersToBeCompacted){

        //sort them by date last modified
        noteFoldersToBeCompacted = sortNoteFoldersByDateLastModified(noteFoldersToBeCompacted);

        //make a new note folder
        NoteFolder newNoteFolder = createNoteFolder();

        //make a string buffer
        String buffer = "";

        //latest date in the bunch
        String firstDateBuffer = noteFoldersToBeCompacted.get(0).getDateLastModifiedStringWithoutTime();

        //compact all of the contents into one buffer
        buffer += "//"+firstDateBuffer+"\n\n";
        for(NoteFolder noteFolder : noteFoldersToBeCompacted){
            if(!firstDateBuffer.equals(noteFolder.getDateLastModifiedStringWithoutTime())){
                firstDateBuffer = noteFolder.getDateLastModifiedStringWithoutTime();
                buffer+="//"+firstDateBuffer+"\n\n";
            }
            buffer+="//"+noteFolder.getTimeLastModifiedString()+"\n"+noteFolder.getNotesText()+"\n\n";
        }


        //store the contents of the buffer in the new folder
        newNoteFolder.setNotesText(buffer);

        //delete old note folders
        for(NoteFolder noteFolder : noteFoldersToBeCompacted){
            noteFolder.delete();
        }

        return newNoteFolder;
    }







}
