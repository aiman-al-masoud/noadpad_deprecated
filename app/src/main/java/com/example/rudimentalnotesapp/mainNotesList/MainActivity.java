package com.example.rudimentalnotesapp.mainNotesList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.content.Context;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.displayAndEditText.TextEditorActivity;
import com.example.rudimentalnotesapp.allPurposeFragments.SearchDialogFragment;
import com.example.rudimentalnotesapp.collections.Collection;
import com.example.rudimentalnotesapp.collections.CreateCollectionFragment;
import com.example.rudimentalnotesapp.collections.OpenCollectionFragment;
import com.example.rudimentalnotesapp.downloads.DownloadWebpageFragment;
import com.example.rudimentalnotesapp.downloads.DownloaderTask;
import com.example.rudimentalnotesapp.filesystem.FileIO;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.filesystem.NoteFolderManager;
import com.example.rudimentalnotesapp.settings.Settings;
import com.example.rudimentalnotesapp.settings.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {


    //path to root of app's local filesystem
    // (excluding final slash)
    public static File rootDir;

    //get this casted to Context
    public static Context mainActivityContext;

    //get this activity
    public static MainActivity mainActivity;

    //currently displayed fragments (list of ItemButtons)
    ArrayList<ItemButtonFragment> currentItemsList = new ArrayList<ItemButtonFragment>();

    //FAB to create a new note folder
    FloatingActionButton newFileButton;

    //FAB to search for note files
    FloatingActionButton searchButton;

    //FAB to launch settings activity
    FloatingActionButton settingsButton;



    //current collection on display
    public static Collection currentlyDisplayedCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get path to root of app's local filesystem
        rootDir = getFilesDir();

        //get this acttivity
        mainActivity = this;

        //get this casted to Context
        mainActivityContext = this;

        //get new-folder FAB and set its action
        newFileButton = findViewById(R.id.createNewFileFAB);
        newFileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //create a new note folder
                NoteFolder newNoteFolder = NoteFolderManager.createNoteFolder();
                Intent intent = new Intent(MainActivity.mainActivityContext, TextEditorActivity.class);
                intent.putExtra("NOTE_FOLDER_ID", newNoteFolder.getID());

                //if a collection is currently on display, add newly created folder to it
                if(currentlyDisplayedCollection!=null){
                    newNoteFolder.addToCollection(currentlyDisplayedCollection.getName());
                    //refresh the list since a new item got added to the collection
                    mainActivity.removeAllAndAddSelection(currentlyDisplayedCollection.getLinkedNoteFolders());
                }

                startActivity(intent);

            }
        });

        //set new-folder fab's long press action
        newFileButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu longPressMenu = new PopupMenu(MainActivity.mainActivityContext, newFileButton);
                longPressMenu.getMenuInflater().inflate(R.menu.new_folder_long_press_menu, longPressMenu.getMenu());

                //set long press menu's actions
                longPressMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.downloadPageFromWebItem:
                                DownloadWebpageFragment downloadWebpageFragment = new DownloadWebpageFragment();
                                downloadWebpageFragment.show(getSupportFragmentManager(), "download page fragment");

                                break;


                        }
                        return true;
                    }
                });


                longPressMenu.show();

                return true;
            }
        });



        //get search fab and set its action
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new SearchDialogFragment().show(getSupportFragmentManager(), "SeachDialogFragment");
            }
        });


        //get options fab and set its action
        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.mainActivityContext, settingsButton);
                popupMenu.getMenuInflater().inflate(R.menu.options_popup_menu, popupMenu.getMenu());

                //set listener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("settings")){
                            Intent intent = new Intent(MainActivity.mainActivityContext, SettingsActivity.class);
                            startActivity(intent);
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        //get collection fab and set its actions
        FloatingActionButton collectionFAB = findViewById(R.id.collectionsFAB);
        collectionFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //inflate collections manager menu
                PopupMenu collectionsPopupMenu = new PopupMenu(MainActivity.mainActivityContext, collectionFAB);
                collectionsPopupMenu.getMenuInflater().inflate(R.menu.collections_manager_menu, collectionsPopupMenu.getMenu());

                //set collections manager menu's actions
                collectionsPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getTitle().toString()){
                            case "make collection":
                                //display create collection dialog fragment
                                CreateCollectionFragment createCollectionFragment = new CreateCollectionFragment();
                                createCollectionFragment.show(getSupportFragmentManager(), "create collection fragment");
                                break;
                            case "show collections":
                                //display show collections fragment
                                OpenCollectionFragment showCollectionsFragment = new OpenCollectionFragment();
                                showCollectionsFragment.show(getSupportFragmentManager(), "show collections fragment");
                                break;


                        }

                        return true;
                    }
                });

                //show collections manager menu
                collectionsPopupMenu.show();



            }
        });



        //if it doesn't exist yet
        Settings.createSettingsFolder();



    }


    //reload available files
    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(currentlyDisplayedCollection!=null){
           return;
        }

        //re-add ALL items once
        refreshAndReaddAll();
        //reset to no collection displayed
        setCurrentlyDisplayedCollection(null);
    }

    @Override
    public void onBackPressed() {
        //re-add ALL items once
        refreshAndReaddAll();
        //reset to no collection displayed
        setCurrentlyDisplayedCollection(null);
        //super.onBackPressed();
    }

    //add an item to the currentItemsList and stick it to the
    //activity visually
    public void addItemToCurrentItemList(NoteFolder itemsNoteFolder){
        if(itemsNoteFolder == null){
            return;
        }

        for(ItemButtonFragment item : currentItemsList){
            if(item.getNoteFolder().equals(itemsNoteFolder)){
                return;
            }
        }

        //make a new visual item
        ItemButtonFragment newItem = new ItemButtonFragment();
        //set its referenced file
        newItem.setNoteFolder(itemsNoteFolder);
        //add the item to the current list
        currentItemsList.add(newItem);

        //add new item to this activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.linearLayout, newItem, null).commit();

    }

    //remove everything and clear list
    public void removeAllItems(){
        //remove all items
        for(ItemButtonFragment item : currentItemsList){
            getSupportFragmentManager().beginTransaction().remove(item).commit();
        }
        //clear list
        currentItemsList.clear();
    }

    //add all files in the app's local dir
    public void addAll(){
        for(NoteFolder noteFolder : NoteFolderManager.getAllNoteFolders()){
            addItemToCurrentItemList(noteFolder);
        }
    }

    //make a new file and give it an integer ID
    //the lowest still available integer
    public File makeNewDocFile(){

        //get all files
        File[] filesList = FileIO.getMyNoteFiles();

        //if there are no files yet, just return file with
        //name: file0.txt
        if(filesList.length==0){
            return FileIO.createNewFile("file0");
        }

        //find maximum int used for file name
        Arrays.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {

                try{
                    //get int 1
                    int int1 = Integer.parseInt(file1.getName().replace("file", "").replace(".txt", "").trim());

                    //get int 2
                    int int2 = Integer.parseInt(file2.getName().replace("file", "").replace(".txt", "").trim());
                    return int2-int1;
                }catch (NumberFormatException e){
                   e.printStackTrace();
                }

                return 0;
            }
        });


        int nextIntID = Integer.parseInt(filesList[0].getName().replace("file", "").replace(".txt", "").trim())+1;


        //create and return a new file with this ID
        return FileIO.createNewFile("file"+nextIntID);
    }

    //add items selectively
    public void removeAllAndAddSelection(ArrayList<NoteFolder> noteFolders){
        //remove everything
        removeAllItems();
        //add only those items that pertain to files in filesList
        for(NoteFolder noteFolder : noteFolders){
            addItemToCurrentItemList(noteFolder);
        }
    }

    //refresh and add all
    public void refreshAndReaddAll(){
        removeAllItems();
        addAll();
    }

    //turn on check-box selection for all items
    public void displayCheckBoxes(){
        for(ItemButtonFragment item : currentItemsList){
            item.setCheckBoxVisibility(View.VISIBLE);
        }
    }

    //turn off all checkboxes for all items and unselect all
    public void hideCheckBoxes(){
        for(ItemButtonFragment item : currentItemsList){
            item.setCheckBoxVisibility(View.GONE);
            item.setCheckedBox(false);
        }
    }

    //get selection (ie: all items with ticked check-boxes)
    public ArrayList<ItemButtonFragment> getSelection(){
        ArrayList<ItemButtonFragment> selection = new ArrayList<ItemButtonFragment>();
        for(ItemButtonFragment item : currentItemsList){
            if(item.isCheckedBox()){
                selection.add(item);
            }
        }
        return selection;
    }


    //delete selection
    public void deleteSelection(){
        for(ItemButtonFragment item : getSelection()){
            item.deleteNoteFolder();
        }
    }

    //compact selection in one new big notes folder,
    //then delete selection
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void compactSelection(){

        //get all of the notes from all of the
        //selected folders in one buffer
        String buf = "";
        for(ItemButtonFragment item : getSelection()){
            buf+="//"+item.getNoteFolder().getDateLastModified().toString()+"\n"+item.getNoteFolder().getNotesText()+"\n\n";
        }
        //create new compacted notes folder and fill it up
        NoteFolder noteFolder = NoteFolderManager.createNoteFolder();
        noteFolder.setNotesText(buf);

        //and now delete selection
        deleteSelection();
    }

    //set currently displayed collection
    public void setCurrentlyDisplayedCollection(Collection currentlyDisplayedCollection){
        this.currentlyDisplayedCollection = currentlyDisplayedCollection;
        if(currentlyDisplayedCollection == null){
            setNotesListTitle("");
        }else{
            setNotesListTitle(currentlyDisplayedCollection.getName());
        }
    }

    //set notes list title (display a title for the current list)
    public void setNotesListTitle(String title){
        ((TextView)findViewById(R.id.notesListTitle)).setText(title);

    }








}