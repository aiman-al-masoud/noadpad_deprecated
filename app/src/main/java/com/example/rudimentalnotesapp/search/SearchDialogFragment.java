package com.example.rudimentalnotesapp.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.collections.TemporaryCollection;
import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.filesystem.NoteFolderManager;

import java.util.ArrayList;

public class SearchDialogFragment extends DialogFragment {

    Button goButton;
    EditText searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_dialog_fragment, container, false);

        //get go button
        goButton = view.findViewById(R.id.goButton);
        //get search bar
        searchBar = view.findViewById(R.id.searchBar);

        //get message box and set it
        ((TextView)view.findViewById(R.id.textInputPromptMessage)).setText("enter keyword(s):");


        //set go button listener
        goButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                ArrayList<NoteFolder> relevantNoteFolders = new ArrayList<NoteFolder>();

                String[] keywords = searchBar.getText().toString().split("\\s+");


                //search for folders that contain note files with all of the single-space-separated keywords
                for(NoteFolder noteFolder : NoteFolderManager.getAllNoteFolders()){
                   if(noteFolder.searchForAndedKeywordsInNotesText(keywords)){
                       relevantNoteFolders.add(noteFolder);
                       Log.d("RELEVANT_FOLDERS", noteFolder.getNotesText());
                   }
                }


                //set a new (empty) temporary collection as the current collection
                TemporaryCollection tmpCollection = new TemporaryCollection("query results");
                MainActivity.mainActivity.setCurrentlyDisplayedCollection(tmpCollection);


                //call main activity telling it to display only relevant note files
                MainActivity.mainActivity.removeAllAndAddSelection(relevantNoteFolders);


            }
        });

        return view;
    }



}
