package com.example.rudimentalnotesapp.mainNotesList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import android.widget.PopupMenu.OnMenuItemClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.displayAndEditText.TextEditorActivity;
import com.example.rudimentalnotesapp.collections.AddToColletionFragment;
import com.example.rudimentalnotesapp.encryption.DecryptFragment;
import com.example.rudimentalnotesapp.encryption.EncryptFragment;
import com.example.rudimentalnotesapp.collections.Collection;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.settings.Settings;
import com.example.rudimentalnotesapp.sharing.Share;



import bsh.EvalError;
import bsh.Interpreter;

//this is an item of a list of text files
public class ItemButtonFragment extends Fragment {

    //this item's corresponding text file
    NoteFolder noteFolder;

    //this item's checkBox
    CheckBox checkBox;

    //this item's on long click popup menu
    PopupMenu popupMenu;

    //this item's button contains the title.
    public String title;

    //displays date last edited for this item
    TextView datesText;

    //button to access this item's notes folder
    Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_item_button, container, false);


        try{
            //change name of button according to
            //contents of file
            button = view.findViewById(R.id.button);
            String fileText = noteFolder.getNotesText();

            try{
                //first 10 chars of the text
                fileText = fileText.substring(0, 10);
            }catch (Exception e){

            }
            button.setText(fileText.replace("\n", "")+"...");


            title = button.getText().toString();


            //set onclick listener for the button
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    callTextEditor();
                }
            });


            try{
                //display date last modified
                //on datesText part of a fragment
                datesText = (TextView)view.findViewById(R.id.datesText);
                datesText.setText("last edited: "+noteFolder.getDateLastModifiedString());


            }catch (NoSuchMethodError e){
                e.printStackTrace();
            }



            //add a long-press menu to the item button
            button.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {

                    popupMenu = new PopupMenu(MainActivity.mainActivityContext, button);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_item_popup, popupMenu.getMenu());

                    //set title of encrypt/decrypt button depending
                    //on whether or not this item's note folder is encrypted
                    if(noteFolder.isEncrypted()){
                        popupMenu.getMenu().findItem(R.id.encryptDectyptItem).setTitle("decrypt");
                    }else{
                        popupMenu.getMenu().findItem(R.id.encryptDectyptItem).setTitle("encrypt");
                    }

                    //if checkbox's visibility is on, activate
                    //the multiple-item version of the popup menu
                    if(checkBox.getVisibility()==View.VISIBLE){
                        popupMenu.getMenu().findItem(R.id.selectItemsOption).setTitle("unselect all");
                        popupMenu.getMenu().findItem(R.id.deleteFileItem).setTitle("delete all");
                        popupMenu.getMenu().findItem(R.id.compactItem).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.runAsScript).setTitle("run all");
                        popupMenu.getMenu().findItem(R.id.addToCollectionItem).setTitle("add all to collection");
                        popupMenu.getMenu().findItem(R.id.removeFromCollectionItem).setTitle("remove all from collection");
                        popupMenu.getMenu().findItem(R.id.encryptDectyptItem).setTitle(popupMenu.getMenu().findItem(R.id.encryptDectyptItem).getTitle()+" all");
                    }

                    //if a collection's being displayed, activate the collection-related menu items
                    Collection currentCollection = null;
                    if((currentCollection = MainActivity.currentlyDisplayedCollection)!=null){
                        popupMenu.getMenu().findItem(R.id.removeFromCollectionItem).setVisible(true);
                    }




                    //add an onclick listener to the items in the menu
                    popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getTitle().toString()){
                                case "delete":
                                    noteFolder.delete();
                                    //restart main activity
                                    MainActivity.needToRefresh = true;
                                    startActivity(new Intent(MainActivity.mainActivityContext, MainActivity.class));
                                    break;
                                case "run as script":

                                    try {
                                        Interpreter interpreter = new Interpreter();
                                        String script = noteFolder.getNotesText();
                                        Object result = interpreter.eval(script);

                                        //switch to displaying results of execution on the text editor
                                        Intent intent = new Intent(MainActivity.mainActivityContext, TextEditorActivity.class);
                                        intent.putExtra("TEXT_TO_BE_DISPLAYED", result.toString());
                                        startActivity(intent);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;

                                case "encrypt":
                                    EncryptFragment encryptFragment = new EncryptFragment();
                                    encryptFragment.addNoteFolderToBeEncrypted(noteFolder);
                                    encryptFragment.show(getFragmentManager(), "tag");
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "decrypt":
                                    DecryptFragment decryptFragment = new DecryptFragment();
                                    decryptFragment.addNoteFolderToBeDecrypted(noteFolder);
                                    decryptFragment.show(getFragmentManager(), "tag");
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "select":
                                    //activate all checkboxes
                                    MainActivity.mainActivity.displayCheckBoxes();
                                    //make this item's checkbox selected
                                    checkBox.setChecked(true);

                                    break;
                                case "unselect all":
                                    //deactivate all checkboxes
                                    MainActivity.mainActivity.hideCheckBoxes();
                                    break;
                                case "delete all":
                                    //if more than one item is selected, delete them all
                                    MainActivity.mainActivity.deleteSelection();
                                    //restart main activity
                                    MainActivity.needToRefresh = true;
                                    startActivity(new Intent(MainActivity.mainActivityContext, MainActivity.class));
                                    break;
                                case "compact all":
                                    MainActivity.mainActivity.compactSelection();
                                    //restart main activity
                                    MainActivity.needToRefresh = true;
                                    startActivity(new Intent(MainActivity.mainActivityContext, MainActivity.class));
                                    break;
                                case "run all":
                                    //initialize interpreter
                                    Interpreter interpreter = new Interpreter();
                                    //get all of the parts of the script in one string.
                                    //Interpreter executes the code strictly line-by-line,
                                    //so you gotta make sure that function definitions
                                    //go first, and function calls last.

                                    String functionDefsBuffer="";
                                    String functionCallsBuffer = "";
                                    String tmpBuf = "";
                                    String scriptBuffer = "";
                                    for(ItemButtonFragment noteItem : MainActivity.mainActivity.getSelection()){
                                        if((tmpBuf = noteItem.noteFolder.getNotesText()).contains("{")) {
                                            //if it's a definition, put it with defs
                                            functionDefsBuffer += tmpBuf + "\n";
                                        }else if(tmpBuf.contains("=")){
                                            //if it's a definition, put it with defs
                                            functionDefsBuffer+=tmpBuf+";\n";
                                        }else{
                                            //else put it with "calls"
                                            functionCallsBuffer+=tmpBuf+"\n";
                                        }
                                    }
                                    scriptBuffer = functionDefsBuffer+"\n"+functionCallsBuffer;

                                    //run script and get results
                                    try{
                                        Object result = interpreter.eval(scriptBuffer);
                                        //switch to displaying results of execution on the text editor
                                        Intent intent = new Intent(MainActivity.mainActivityContext, TextEditorActivity.class);
                                        intent.putExtra("TEXT_TO_BE_DISPLAYED", result.toString());
                                        startActivity(intent);
                                    }catch (EvalError e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case "add to collection":
                                    //display show collections fragment
                                    AddToColletionFragment addToCollectionFragment = new AddToColletionFragment();
                                    addToCollectionFragment.addToNoteFoldersToBeAddedToCollection(noteFolder);
                                    addToCollectionFragment.show(getFragmentManager(), "show collections to add an item");
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "add all to collection":
                                    AddToColletionFragment addToCollectionFrag = new AddToColletionFragment();
                                    for(ItemButtonFragment noteItem : MainActivity.mainActivity.getSelection()){
                                        addToCollectionFrag.addToNoteFoldersToBeAddedToCollection(noteItem.noteFolder);
                                    }
                                    addToCollectionFrag.show(getFragmentManager(), "show collections to add more than one item");
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "remove from collection":
                                    noteFolder.removeFromCollection(MainActivity.currentlyDisplayedCollection.getName());
                                    MainActivity.mainActivity.removeAllAndAddSelection(MainActivity.currentlyDisplayedCollection.getLinkedNoteFolders());
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "remove all from collection":
                                    for(ItemButtonFragment token : MainActivity.mainActivity.getSelection()){
                                        token.noteFolder.removeFromCollection(MainActivity.currentlyDisplayedCollection.getName());
                                    }
                                    MainActivity.mainActivity.removeAllAndAddSelection(MainActivity.currentlyDisplayedCollection.getLinkedNoteFolders());
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "encrypt all":
                                    EncryptFragment encrFrag = new EncryptFragment();
                                    for(ItemButtonFragment itemm : MainActivity.mainActivity.getSelection()){
                                        encrFrag.addNoteFolderToBeEncrypted(itemm.noteFolder);
                                    }
                                    encrFrag.show(getFragmentManager(), "tag");
                                    encrFrag.deactivateOTP(); //no OTP option for multiple note folders
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "decrypt all":
                                    DecryptFragment decrFrag = new DecryptFragment();
                                    for(ItemButtonFragment itemm : MainActivity.mainActivity.getSelection()){
                                        decrFrag.addNoteFolderToBeDecrypted(itemm.noteFolder);
                                    }
                                    decrFrag.show(getFragmentManager(), "tag");
                                    MainActivity.needToRefresh = true;
                                    break;
                                case "share":
                                    Share.shareText(noteFolder.getNotesText());
                                    break;

                            }


                            return true;
                        }
                    });

                    //show popup menu
                    popupMenu.show();
                    return true;
                }
            });


        }catch(Exception e){
            //if file doesn't exist or is faulted
            e.printStackTrace();
        }

        //get this item's check box
        checkBox = (CheckBox) view.findViewById(R.id.noteItemCheckBox);



        //set this item's backround and foreground according
        //to global settings
        int bgColor = Settings.getBackgroundForegroundColor(0);
        int fgColor = Settings.getBackgroundForegroundColor(1);
        view.getRootView().setBackgroundColor(bgColor);
        datesText.setTextColor(fgColor);
        button.setTextColor(fgColor);

        return view;
    }

    //get file's text and call the TextEditor Activity
    public void callTextEditor(){
        Intent intent = new Intent(MainActivity.mainActivityContext, TextEditorActivity.class);
        //give text editor an ID of a note folder
        intent.putExtra("NOTE_FOLDER_ID", noteFolder.getID());
        startActivity(intent);
    }


    //get this item's corresponding text note folder
    public NoteFolder getNoteFolder() {
        return noteFolder;
    }

    //set this item's corresponding note folder
    public void setNoteFolder(NoteFolder noteFolder) {
        this.noteFolder = noteFolder;
    }

    //turn on/off visibility for this item's checkbox
    //NB: use View.VISIBLE
    public void setCheckBoxVisibility(int visible){
        checkBox.setVisibility(visible);
    }

    //check (tick) this item's checkbox
    public void setCheckedBox(boolean bool){
        checkBox.setChecked(bool);
    }

    //is checkbox checked? ie: ticked?
    public boolean isCheckedBox(){
        return checkBox.isChecked();
    }

    //delete this item's note folder
    public void deleteNoteFolder(){
        noteFolder.delete();
    }









}
