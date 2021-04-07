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
import com.example.rudimentalnotesapp.R;

import java.util.ArrayList;

public class DecryptFragment extends DialogFragment {


    //list of note folders to be encrypted
    ArrayList<NoteFolder> toBeDecryptedFoldersList = new ArrayList<NoteFolder>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get reference to this dialog fragment
        DialogFragment eigenReference = this;

        //inflate view
        View view = inflater.inflate(R.layout.decrypt_fragment, container, false);

        //get key input field
        EditText keyInput = (EditText)view.findViewById(R.id.decryptKeyInput);

        //get decrypt button and set its behaviour
        Button decryptButton = view.findViewById(R.id.decryptButton);
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                String userProvidedKey = keyInput.getText().toString();

                for(NoteFolder noteFolder : toBeDecryptedFoldersList){
                    noteFolder.decrypt(userProvidedKey);
                }

                eigenReference.dismiss();
                startActivity(new Intent(MainActivity.mainActivityContext, MainActivity.class));
            }
        });



        return view;
    }


    //add a note folder the the list to be decrypted
    public void addNoteFolderToBeDecrypted(NoteFolder noteFolder){
        toBeDecryptedFoldersList.add(noteFolder);
    }


}
