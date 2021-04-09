package com.example.rudimentalnotesapp.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;

import java.util.ArrayList;

public class AddToColletionFragment extends DialogFragment {

    //the name says it
    ArrayList<NoteFolder> noteFoldersToBeAddedToCollection =new ArrayList<NoteFolder>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_collections_fragment, container, false);

        //load all collection as buttons
        for(String collectionName : Collection.getCollectionNames()){
            Button button = new Button(MainActivity.mainActivity);

            //set button's name to collection name
            button.setText(collectionName);

            //set button's action to: link the selected folder(s)
            //to the button's collection
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(NoteFolder noteFolder : noteFoldersToBeAddedToCollection){
                        noteFolder.addToCollection(collectionName);
                    }
                    noteFoldersToBeAddedToCollection.clear();
                }
            });



            //add button to list of collections
            ((LinearLayout)view.findViewById(R.id.collectionsLinearLayout)).addView(button);
        }


        return view;
    }


    //add a note folder to the list to be added to a collection
    public void addToNoteFoldersToBeAddedToCollection(NoteFolder noteFolder){
        noteFoldersToBeAddedToCollection.add(noteFolder);
    }




    }
