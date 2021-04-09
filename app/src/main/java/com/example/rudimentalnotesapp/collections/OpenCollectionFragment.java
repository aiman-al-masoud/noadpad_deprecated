package com.example.rudimentalnotesapp.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;

import java.util.ArrayList;

public class OpenCollectionFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //get autoreference
        DialogFragment eigenReference = this;

        //inflate view
        View view = inflater.inflate(R.layout.show_collections_fragment, container, false);

        //load all collection as buttons
        for(String collectionName : Collection.getCollectionNames()){
            Button button = new Button(MainActivity.mainActivity);
            //set the button's name to the collection's
            button.setText(collectionName);

            //set the button's on click action to refresh the main activity with the
            //note folder items in the clicked collection alone.
            Collection buttonsCollection = Collection.getCollection(collectionName);
            ArrayList<NoteFolder> linkedNoteFolders = buttonsCollection.getLinkedNoteFolders();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //set currently displayed collection
                    MainActivity.mainActivity.setCurrentlyDisplayedCollection(buttonsCollection);
                    //display only items from selected collection
                    MainActivity.mainActivity.removeAllAndAddSelection(linkedNoteFolders);
                    //dismiss this fragment
                    eigenReference.dismiss();
                }
            });

            //set a long press button action to delete or edit a collection
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //inflate collections long press menu
                    PopupMenu popupMenu = new PopupMenu(MainActivity.mainActivity, button);
                    popupMenu.getMenuInflater().inflate(R.menu.long_press_collection_menu, popupMenu.getMenu());

                    //set collections long press menu's actions
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getTitle().toString()){
                                case "delete":
                                    Collection.getCollection(collectionName).delete();
                                    eigenReference.dismiss();
                                    break;
                                case "edit":
                                    CreateCollectionFragment editCollectionFrag = new CreateCollectionFragment();
                                    editCollectionFrag.setCollectionToBeEdited(Collection.getCollection(collectionName));
                                    editCollectionFrag.show(getFragmentManager(), "edit collection fragment");
                                    break;

                            }


                            return true;
                        }
                    });

                    //show collections long press menu
                    popupMenu.show();


                    return true;
                }
            });



            //add the button to the linear layout
            ((LinearLayout)view.findViewById(R.id.collectionsLinearLayout)).addView(button);
        }

        return view;
    }





}
