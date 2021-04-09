package com.example.rudimentalnotesapp.encryption;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.filesystem.NoteFolder;
import com.example.rudimentalnotesapp.allPurposeFragments.QuickMessageDialogFragment;
import com.example.rudimentalnotesapp.R;

import java.util.ArrayList;

public class EncryptFragment extends DialogFragment {


    //list of note folders to be encrypted
    ArrayList<NoteFolder> toBeEncryptedFoldersList = new ArrayList<NoteFolder>();

    //button to generate OTP automatically
    private boolean isOTPOnFlag = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //get reference to this dialog fragment
        DialogFragment eigenReference = this;

        //inflate view
        View view = inflater.inflate(R.layout.encrypt_fragment, container, false);

        //get my key input
        EditText myKeyInput = (EditText)view.findViewById(R.id.keyInput);

        //set use my key button's behaviour
        Button useMyKeyButton = view.findViewById(R.id.useMyKeyButton);
        useMyKeyButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                String userProvidedKey = myKeyInput.getText().toString();

                for(NoteFolder noteFolder : toBeEncryptedFoldersList){
                    noteFolder.encrypt(userProvidedKey);
                }

                toBeEncryptedFoldersList.clear();
                eigenReference.dismiss();
                startActivity(new Intent(MainActivity.mainActivity, MainActivity.class));
            }
        });

        //set generate OTP button's behavior
        Button generateOTPButton = view.findViewById(R.id.generateOTPButton);
        //if OTP is disabled, grey out the generateOTP button
        if(!isOTPOnFlag){
            generateOTPButton.setEnabled(false);
        }
        generateOTPButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                //OTP option is only gonna be available for a single folder
                NoteFolder noteFolder =  toBeEncryptedFoldersList.get(0);
                String OTP = noteFolder.encrypt();

                //display OTP somehow
                new QuickMessageDialogFragment("your OTP is: "+OTP).show(MainActivity.mainActivity.getSupportFragmentManager(), "tag");
                MainActivity.mainActivity.refreshAndReaddAll();
            }
        });


        return view;
    }


    //add a note folder the the list to be encrypted
    public void addNoteFolderToBeEncrypted(NoteFolder noteFolder){
        toBeEncryptedFoldersList.add(noteFolder);
    }

    //deactivate OTP button
    public void deactivateOTP(){
        this.isOTPOnFlag = false;
    }


}
