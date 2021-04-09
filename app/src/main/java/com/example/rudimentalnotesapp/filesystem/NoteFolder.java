package com.example.rudimentalnotesapp.filesystem;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.rudimentalnotesapp.collections.Collection;
import com.example.rudimentalnotesapp.encryption.Encrypter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoteFolder extends File {

    //note text file: stores the text notes.
    private File notesTextFile;

    //encrypted flag file: is this file encrypted or not?
    private File encryptedFlagFile;

    //file that contains the names of all of the Collections this noteFolder is linked to
    private File linkedToCollectionsFile;

    //list of the collections names.
    ArrayList<String> collectionNamesList;

    //encrypter
    private static Encrypter encrypter = new Encrypter();

    //make a new reference to an (existing/or yet to be created)
    //created) notes folder.
    public NoteFolder(@NonNull String pathname) {
        super(pathname);

        //declare this noteFolder's internal files
        notesTextFile = new File(pathname+"/notesTextFile.txt");
        encryptedFlagFile = new File(pathname+"/encryptedFlagFile.txt");
        linkedToCollectionsFile = new File(pathname+"/linkedToCollections.txt");



        //if this folder exists, yet some of its
        //internal files don't, create them
        allExistAndIfNotMake();

        //initialize and load collection names list
        loadCollectionNamesList();

    }

    //get text file's text
    public String getNotesText(){
        return FileIO.readFile(notesTextFile);
    }

    //set text file's text
    public void setNotesText(String newText){
        FileIO.writeToFile(notesTextFile, newText);
    }

    //get the notes file. To be phased out later on, 'cuz encapsulation...
    public File getNotesTextFile(){
        return notesTextFile;
    }


    //make this folder in storage,
    // and create all of its internal files
    @Override
    public boolean mkdir() {
        super.mkdir();
        try {
            notesTextFile.createNewFile();
            encryptedFlagFile.createNewFile();
            FileIO.writeToFile(encryptedFlagFile, "false");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //get integer ID
    public int getID(){
        return Integer.parseInt(getName().replace("noteFolder", "").trim());
    }


    //delete this folder from storage
    //NB: non-recursive, works only if
    //internal structure remains flat!!
    @Override
    public boolean delete() {
        for(File file : listFiles()){
            file.delete();
        }
        return super.delete();
    }



    //does note file contain a given keyword
    public boolean searchForKeywordInNotesText(String keyword){
        String notesText = FileIO.readFile(notesTextFile);

        //ignore empty keywords
        if(keyword.trim().isEmpty()){
            return false;
        }

        if(notesText.toUpperCase().contains(keyword.toUpperCase())){
            return true;
        }
        return false;
    }

    //does note file contain a set of anded keywords
    public boolean searchForAndedKeywordsInNotesText(String[] keywords){
        for(String keyword : keywords){
            if(!searchForKeywordInNotesText(keyword)){
                return false;
            }
        }
        return true;
    }

    //does note file contain a set of ORed keywords
    public boolean searchForOredKeywordsInNotesText(String[] keywords){
        for(String keyword : keywords){
            if(searchForKeywordInNotesText(keyword)){
                return true;
            }
        }
        return false;
    }




    //get date last modified (of notes file)
    public Date getDateLastModified(){
        try {
            //TEST
            //Log.d("DATE_TEST",new SimpleDateFormat("dd/MM/yyyy").format(new Date(notesTextFile.lastModified())));
            Date dateLastModified = new Date(notesTextFile.lastModified());
            Log.d("DATES_TEST",dateLastModified.toString());
            return dateLastModified;
            //BasicFileAttributes fileAttributes = Files.readAttributes(notesTextFile.toPath(), BasicFileAttributes.class);
            //return fileAttributes.lastModifiedTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //get date last modfied string
    public String getDateLastModifiedString(){
        return new SimpleDateFormat("dd/MM/yyyy hh:mm").format( getDateLastModified());
    }



    // If this folder exists, yet some of its
    // internal folders don't, create them.
    //Useful for when the app gets updated
    //and new metadata files have to be added to a
    //noteFolder's internal structure.
    public void allExistAndIfNotMake(){

        //if this folder doesn't exist yet,
        //don't bother creating its files
        if(!this.exists()){
            return;
        }

        //if this folder exists, you gotta make
        //sure that all of its required internal
        //files do too.
        for(File file : this.listFiles()){
            if(!file.exists()){
                try{
                    file.createNewFile();
                }catch(IOException e){
                }
            }
        }
    }



    //HANDLING COLLECTIONS:

    //intialize linked to collection names list
    public void loadCollectionNamesList(){
        collectionNamesList = new ArrayList<String>();
        try{
            for(String collectionName : FileIO.readFile(linkedToCollectionsFile).split("\n")){
                collectionNamesList.add(collectionName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //save collection names to file
    //one name one line
    public void saveCollectionNames(){
        String bufCollectionsString = "";
        for(String name : collectionNamesList){
            bufCollectionsString+=name+"\n";
        }
        FileIO.writeToFile(linkedToCollectionsFile, bufCollectionsString);
    }


    //add this folder to a collection
    public void addToCollection(String collectionName){
        if(isInCollection(collectionName)){
            return; //don't add it twice
        }
        //add a new line to the the file for any
        //new collection
        collectionNamesList.add(collectionName);

        //re-save collecitons to storage
        saveCollectionNames();
    }


    //is this folder in a collection?
    public boolean isInCollection(String collectionName){
        for(String name : collectionNamesList){
            if(name.equals(collectionName)){
                return true;
            }
        }
        return false;
    }

    //remove this folder from a collection
    public void removeFromCollection(String collectionName){
        if(!isInCollection(collectionName)){
            //if it's not in the collection don't do anything
            return;
        }
        //remove collection from list
        collectionNamesList.remove(collectionName);

        //re-save collections to storage
        saveCollectionNames();
    }



    //check if this folder contains any of the keywords
    //from any collection, and add it in case it does.
    public void addMeToRelevantCollections(){
        for(String collectionName : Collection.getCollectionNames()){

            Log.d("TAGS", Collection.getCollection(collectionName).getTagsAsString());


            if(searchForOredKeywordsInNotesText(Collection.getCollection(collectionName).getTagsAsString().split(" "))){
                addToCollection(collectionName);
            }
        }
    }





    //ENCRYPTION AND DECRYPTION:

    //sets the state of this folder as "encrypted"
    private void setEncrypted(boolean encrypted) {
        FileIO.writeToFile(encryptedFlagFile, encrypted+"");
    }

    //tells caller if this folder is currently encrypted
    public boolean isEncrypted(){
        if(FileIO.readFile(encryptedFlagFile).contains("true")){
            return true;
        }
        return false;
    }

    //encrypt this folder's notes with user-provided key
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void encrypt(String key){
        if(isEncrypted()){
            //if it's already encrypted, don't
            //encrypt it twice
            return;
        }
        //get plain text from note file
        String plainText = getNotesText();
        //get cipher text
        String cipherText = encrypter.encryptString(key, plainText);
        //overwrite plain text with cipher text
        setNotesText(cipherText);
        //set encrypyted flag to true
        setEncrypted(true);
    }

    //encrypt this folder's notes with auto-generated OTP
    //and return OTP
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String encrypt(){
        String OTP = encrypter.generateRandomKey(getNotesText().length());
        encrypt(OTP);
        return OTP;
    }

    //decrypt note's folder note file
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void decrypt(String key){
        if(!isEncrypted()){
            //if it's not encrypted,
            //don't "decrypt" it
            return;
        }

        //get cipher text
        String cipherText = getNotesText();
        //decrypt it
        String plainText = encrypter.decryptString(key, cipherText);
        //overwrite cipher text with decrypted text
        setNotesText(plainText);
        //set encryption flag to false
        setEncrypted(false);
    }









}
