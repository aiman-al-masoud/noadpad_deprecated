package com.example.rudimentalnotesapp.displayAndEditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.allPurposeFragments.SimpleInputDialogPromptFragment;
import com.example.rudimentalnotesapp.collections.TemporaryCollection;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.filesystem.NoteFolderManager;
import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.navigation.SimulateKeyPress;
import com.example.rudimentalnotesapp.settings.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TextEditorActivity extends AppCompatActivity implements SimpleInputDialogPromptFragment.DialogListener {


    //displayed text area
    static EditText textArea;

    //caller's intent
    Intent intent;

    //currently opened note folder to get text from
    NoteFolder noteFolder;

    //initial text to be displayed
    String textToBeDisplayed;

    //text size
    static int textSize =18;

    //search query
    String userInput;


    //navigate between search result tokens
    FloatingActionButton nextTokenFAB;
    FloatingActionButton previousTokenFAB;

    //token positions
    static ArrayList<Integer> tokenPositions;
    static int currentPosition;

    //eigenreference
    public static TextEditorActivity textEditorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        //get eigenreference
        textEditorActivity = this;

        //get text area
        textArea = findViewById(R.id.textArea);

        //set search fab's on click action
        FloatingActionButton searchFAB = findViewById(R.id.innerSearchFAB);
        searchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleInputDialogPromptFragment dialogFragment = new SimpleInputDialogPromptFragment();
                dialogFragment.setDialogListener(textEditorActivity);
                dialogFragment.show(getSupportFragmentManager(), "dialog fragment");
            }
        });

        //get and set back and forth fabs' actions
        nextTokenFAB = findViewById(R.id.nextTokenFAB);
        previousTokenFAB = findViewById(R.id.previousTokenFAB);

        nextTokenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextToken();
            }
        });

        previousTokenFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToPreviousToken();
            }
        });









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

        //in case a note folder was passed
        try{
            //get note folder ID, and thus note folder
            long noteFolderID = intent.getLongExtra("NOTE_FOLDER_ID", -1);
            noteFolder = NoteFolderManager.getNoteFolder(noteFolderID);
            //get text to be displayed from note folder that has been opened
            textToBeDisplayed = noteFolder.getNotesText();
            //display initial text from note folder
            textArea.setText(textToBeDisplayed);
            return;
        }catch (Exception e){

        }


        //if the text to be displayed comes from without the app:
        //(selection of text and menu button press)
        try{
            String text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
            textToBeDisplayed = text;
            NoteFolder newNoteFolder = NoteFolderManager.createNoteFolder();
            newNoteFolder.setNotesText(textToBeDisplayed);
            noteFolder = newNoteFolder;
            //display initial text from note folder
            textArea.setText(textToBeDisplayed);
            return;
        }catch (Exception e){

        }


        //try getting the text to be displayed if no notefolder was passed
        try{
            textToBeDisplayed = intent.getStringExtra("TEXT_TO_BE_DISPLAYED");
            //display initial text from note folder
            textArea.setText(textToBeDisplayed);
            return;
        }catch (Exception e){

        }


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

            MainActivity.needToRefresh = true;
            Toast.makeText(MainActivity.mainActivity, "saved" , Toast.LENGTH_SHORT).show();
        }else{
            //else tell user that no change was made
            Toast.makeText(MainActivity.mainActivity, "no changes were made" , Toast.LENGTH_SHORT).show();
        }

    }


    //move to a given position in the textArea
    public void moveToPosition(int postion){
        if(postion<0){
            return; //no negative positions allowed
        }

        textArea.requestFocus();
        textArea.setSelection(postion);
    }


    //method used to interface with input dialog.
    //here I'm using to trigger search
    @Override
    public void getUserInput(String userInput) {
        this.userInput = userInput;

        //make navigation fabs visible
        nextTokenFAB.setVisibility(View.VISIBLE);
        previousTokenFAB.setVisibility(View.VISIBLE);

        //get positions of queried text tokens
        tokenPositions = noteFolder.getPositionsOf(userInput);

        currentPosition = 0;
        moveToPosition(tokenPositions.get(currentPosition));

    }

    //move to the next token
    public void goToNextToken(){
        if(tokenPositions.size()>currentPosition+1){
            currentPosition++;
            moveToPosition(tokenPositions.get(currentPosition));
        }
    }

    //go back to the previous token
    public void goBackToPreviousToken(){
        if(currentPosition-1 >= 0){
            currentPosition--;
            moveToPosition(tokenPositions.get(currentPosition));
        }
    }



    ////////////////////////EXPERIMENTAL TEXT NAVIGATION WITH VOLUME KEYS///////////////////////
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