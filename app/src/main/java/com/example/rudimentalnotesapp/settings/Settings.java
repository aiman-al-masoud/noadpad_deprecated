package com.example.rudimentalnotesapp.settings;

import android.graphics.Color;
import android.util.Log;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.filesystem.FileIO;

import java.io.File;
import java.io.IOException;

public class Settings {

    //references to (maybe not yet extant) files
    public static File settingsFolder = new File(FileIO.getRootDir().getPath()+"/settings");
    public static File textSizeFile = new File(settingsFolder.getPath()+"/textSize.txt");
    public static File backgroundAndForegroundColorFile = new File(settingsFolder.getPath()+"/backgroundAndForegroundColor.txt"); //background on line one. foreground on line two.
    public static File currentColorThemeFile =  new File(settingsFolder.getPath()+"/currentColorTheme.txt"); //it's an integer ID 0:default, 1:dark ...
    public static File selfSortingCollectionsFlagFile = new File(settingsFolder.getPath()+"/selfSortingCollectionsFlag.txt");

    //settings folder is in the same dir
    //as all of the note folders.
    //it's a regular folder that contains regular
    //text files that are accessed to via this
    //object.
    public static void createSettingsFolder(){

        //create settings folder if it doesn't exist yet
        if(!settingsFolder.exists()){
            settingsFolder.mkdir();
        }

        //create settings files that don't exist yet
        createNonExistingFiles();

    }

    //access/modify text size
    public static void setTextSize(int newTextSize) {
        if (!textSizeFile.exists()) {
            try {
                //if text size file doesn't exist yet,
                //create it
                textSizeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //modify text size
        FileIO.writeToFile(textSizeFile, newTextSize+"");
    }

    public static int getTextSize(){
        try{
            String text = FileIO.readFile(textSizeFile).trim();
            try {
                return Integer.parseInt(text);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }catch (Exception e){
            //if file wasn't found (should've been FileNotFound...)
            try {
                textSizeFile.createNewFile();
                FileIO.writeToFile(textSizeFile, "18");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }

        //default text size
        return 18;
    }

    //get background/foreground color.
    // bgOrFg = 0 => background, bgOrFg = 1 => gets foreground
    public static int getBackgroundForegroundColor(int bgOrFg){

        //background color on first line of this file
        String colorInfo = FileIO.readFile(backgroundAndForegroundColorFile);

        try{
            //get background color name
            String backgroundColorName = colorInfo.split("\n")[bgOrFg].trim().toUpperCase();

           int bgColor =  (int)Color.class.getField(backgroundColorName).get(null);
           return bgColor;

        }catch (Exception e){
            e.printStackTrace();
        }

        //return default background/foreground color
        if(bgOrFg==0){
            //default background is white
            return Color.WHITE;
        }

        //default foreground is black
        return Color.BLACK;
    }

    //set background and foreground color
    public static void setBackgroundAndForegroundColor(String bg, String fg){
        bg = bg.trim().toUpperCase();
        fg = fg.trim().toUpperCase();
        FileIO.writeToFile(backgroundAndForegroundColorFile, bg+"\n"+fg);
    }




    //get current default color theme
    public static int getCurrentColorTheme() {
        try{
            return Integer.parseInt(FileIO.readFile(currentColorThemeFile).trim());
        }catch (Exception e){

        }
        return 0; //DEFAULT theme
    }

    //set current default color theme
    public static void setCurrentColorTheme(int i) {
        FileIO.writeToFile(currentColorThemeFile, i+"");
    }




    //USEFUL FOR UPDATES:
    //create all settings files that don't esixst yet
    public static void createNonExistingFiles(){
        for(File file : settingsFolder.listFiles()){
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //set self sorting collections on
    public static void toggleSelfSortingCollections(boolean bool){
        if(bool){
            FileIO.writeToFile(selfSortingCollectionsFlagFile, "true");
        }else{
            FileIO.writeToFile(selfSortingCollectionsFlagFile, "false");
        }
    }


    //are set self sorting collections on?
    public static boolean isSelfSortingCollectionsOn(){
        try{
            if(FileIO.readFile(selfSortingCollectionsFlagFile).contains("true")){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){

        }

       return false;
    }
























    }
