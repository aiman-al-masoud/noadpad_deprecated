package com.example.rudimentalnotesapp.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.collections.Collection;

public class CreateCollectionFragment extends DialogFragment {

    //collection name input
    EditText collectionNameInput;

    //collection tags input
    EditText collectionTagsInput;

    //make collection button
    Button makeCollectionButton;

    //used to edit an existing collection
    Collection collection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_collection_fragment, container, false);

        //get I/O views
        collectionNameInput = view.findViewById(R.id.collectionNameInput);
        collectionTagsInput = view.findViewById(R.id.collectionTagsInput);
        makeCollectionButton = view.findViewById(R.id.makeCollectionButton);

        //if a collection is being edited (not created ex-novo), set the fields to display previous info
        if(collection!=null){
            //set name of collection field
            collectionNameInput.setText(collection.getName());
            //don't allow the user to change the name of an existing collection
            collectionNameInput.setEnabled(false);
            //set tags field
            collectionTagsInput.append(collection.getTagsAsString());

        }

        //set actions
        makeCollectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if collection == null at this stage it means you're making a new collection
                if(collection==null){
                    collection = new Collection(collectionNameInput.getText().toString());
                }
                //clear current tags if any
                collection.getTags().clear();
                //add tags that are present in the input field
                for(String tag : collectionTagsInput.getText().toString().split("\\s+")){
                    collection.addTag(tag);
                }
                collection.saveInfo();
                collection = null; //wipe off the current colleciton reference
            }
        });

        return view;
    }


    //set collection to be edited
    public void setCollectionToBeEdited(Collection collection){
        this.collection = collection;
    }


}
