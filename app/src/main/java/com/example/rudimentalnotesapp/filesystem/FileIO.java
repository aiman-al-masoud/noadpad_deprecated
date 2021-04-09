package com.example.rudimentalnotesapp.filesystem;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileIO {

    //root directory of this app's files
    static File rootDir = MainActivity.rootDir;

    //overwrite contents of file with a String
    public static void writeToFile(File file, String text){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.flush();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //get contents of text file
    public static String readFile(File file){
        String results = "";
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String buf = null;
            while((buf = reader.readLine()) != null){
                results+=buf+"\n";
            }

            return results;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    //search for keyword in text file
    public static boolean searchForKeyword(File file, String keyword){
        //get text from file
        String fileText = readFile(file);
        //see if it contains a given keyword
        if(fileText.toUpperCase().contains(keyword.toUpperCase())){
            return true;
        }else{
            return false;
        }
    }

    //search for ANDed keywords
    public static boolean searchForAndedKeywords(File file, String[] keywords){
        for(String keyword : keywords){
            if(!searchForKeyword(file, keyword)){
                return false;
            }
        }
        return true;
    }


    //create file
    public static  File createNewFile(String name){
        File newFile = new File(rootDir+"/"+name+".txt");
        try {
            if(!newFile.exists()){
                newFile.createNewFile();
            }
            return newFile;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    //get all of the files stored on app's filesystem.
    public static  File[] getMyFiles(){
        return rootDir.listFiles();
    }

    //get JUST my note fileslistFiles()
    public static File[] getMyNoteFiles(){
        ArrayList<File> noteFilesList = new ArrayList<File>();

        //"file" is the identifier for note files
        for(File file : getMyFiles()){
            if(file.getName().contains("file")){
                noteFilesList.add(file);
            }
        }
        //convert list to array
        File[] noteFilesArray = new File[noteFilesList.size()];
        for(int i =0; i<noteFilesList.size(); i++){
            noteFilesArray[i] = noteFilesList.get(i);
        }
        return noteFilesArray;
    }



    //get a file by its name
    public File getFileByName(String name){
        for(File file : getMyFiles()){
            if(file.getName().contains(name+".txt")){
                return file;
            }
        }
        return null;
    }


    //get root directory
    public static File getRootDir(){
        return rootDir;
    }









}
