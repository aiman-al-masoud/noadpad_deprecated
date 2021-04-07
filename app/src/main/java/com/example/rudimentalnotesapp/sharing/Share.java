package com.example.rudimentalnotesapp.sharing;

import android.content.Intent;

import com.example.rudimentalnotesapp.displayAndEditText.TextEditorActivity;
import com.example.rudimentalnotesapp.mainNotesList.MainActivity;

public class Share {

    public static void shareText(String text){
        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share note");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        MainActivity.mainActivity.startActivity(Intent.createChooser(intent, "share via:"));
    }




}
