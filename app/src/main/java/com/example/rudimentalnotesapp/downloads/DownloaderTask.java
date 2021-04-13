package com.example.rudimentalnotesapp.downloads;


import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.displayAndEditText.TextEditorActivity;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.filesystem.NoteFolderManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DownloaderTask extends AsyncTask {

    String webAddress;
    String webpageText;
    NoteFolder noteFolder;
    boolean launchTextEditorAfterDownload;


    public NoteFolder downloadPage(String webAddress, boolean launchTextEditorAfterDownload){
        this.webAddress = webAddress;
        this.launchTextEditorAfterDownload = launchTextEditorAfterDownload;
        noteFolder = NoteFolderManager.createNoteFolder();
        execute();
        return noteFolder;
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            Document doc = Jsoup.connect(webAddress).get();
            webpageText = doc.text();

        } catch (Exception e) {
            e.printStackTrace();
            webpageText = "could not download your page";
        }


        return null;
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        //make some newlines in between paragraphs
        webpageText = webpageText.replace(".", ".\n\n");

        noteFolder.setNotesText(webpageText);

        if(launchTextEditorAfterDownload){
            //launch Text Editor activity
            Intent intent = new Intent(MainActivity.mainActivity, TextEditorActivity.class);
            intent.putExtra("NOTE_FOLDER_ID", noteFolder.getID());
            MainActivity.mainActivity.startActivity(intent);
        }
    }


}