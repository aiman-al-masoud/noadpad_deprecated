package com.example.rudimentalnotesapp.displayAndEditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;


import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.collections.TemporaryCollection;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.filesystem.NoteFolderManager;
import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.navigation.SimulateKeyPress;
import com.example.rudimentalnotesapp.settings.Settings;

public class TextEditorActivity extends AppCompatActivity {


    //displayed text area
    EditText textArea;

    //caller's intent
    Intent intent;

    //currently opened note folder to get text from
    NoteFolder noteFolder;

    //initial text to be displayed
    String textToBeDisplayed;

    //text size
    static int textSize =18;

    //eigenreference
    public static TextEditorActivity textEditorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        //get text area
        textArea = findViewById(R.id.textArea);

        //get text size from global settings and set it
        textSize = Settings.getTextSize();
        textArea.setTextSize(textSize);


        //get background color from settings and set it
        int bgColor = Settings.getBackgroundForegroundColor(0);
        textArea.setBackgroundColor(bgColor);
        this.getWindow().getDecorView().setBackgroundColor(bgColor);

        //get foreground color from global settings and set it
        int fgColor = Settings.getBackgroundForegroundColor(1);
        textArea.setTextColor(fgColor);


        //get intent from caller
        intent = getIntent();

        //get note folder ID, and thus note folder
        int noteFolderID = intent.getIntExtra("NOTE_FOLDER_ID", -1);
        noteFolder = NoteFolderManager.getNoteFolder(noteFolderID);

        //get text to be displayed from note folder that has been opened
        try{
            textToBeDisplayed = noteFolder.getNotesText();
        }catch (NullPointerException e){
            textToBeDisplayed = intent.getStringExtra("TEXT_TO_BE_DISPLAYED");
        }

        //display initial text from note folder
        textArea.setText(textToBeDisplayed);

        //get eigenreference
        textEditorActivity = this;


    }


    //each time that it gets paused it's gonna save the progress
    @Override
    protected void onPause() {
        super.onPause();

        //if no noteFolder was passed by caller
        if(noteFolder==null){
            return;
        }

        //get modified text from text area
        String modifiedText = textArea.getText().toString();

        //if file was modified
        if(!modifiedText.equals(textToBeDisplayed)){
            //overwrite file with modified version
            noteFolder.setNotesText(modifiedText);

            //check if modified note folder has to be put in a collection automatically
            if(Settings.isSelfSortingCollectionsOn()){
                noteFolder.addMeToRelevantCollections();
            }

            Toast.makeText(MainActivity.mainActivityContext, "saved" , Toast.LENGTH_SHORT).show();
        }else{
            //else tell user that no change was made
            Toast.makeText(MainActivity.mainActivityContext, "no changes were made" , Toast.LENGTH_SHORT).show();
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case 24: //volume up pressed: back
                SimulateKeyPress.press(KeyEvent.KEYCODE_DPAD_LEFT);
                return true; //makes sure volume toast doesn't get displayed
            case 25: //volume down pressed: forth
                SimulateKeyPress.press(KeyEvent.KEYCODE_DPAD_RIGHT);
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }


}