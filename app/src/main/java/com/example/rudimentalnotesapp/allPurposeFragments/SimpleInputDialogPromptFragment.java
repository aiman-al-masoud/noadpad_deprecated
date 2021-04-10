package com.example.rudimentalnotesapp.allPurposeFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.R;

public class SimpleInputDialogPromptFragment extends DialogFragment {



    //this fragment's listener
    DialogListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_dialog_fragment, container, false);

        //get text message and set it
        TextView message = view.findViewById(R.id.textInputPromptMessage);
        message.setText("search for:");

        //get text field
        EditText searchBar = view.findViewById(R.id.searchBar);

        //get go button and set its action
        Button goButton = view.findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getUserInput(searchBar.getText().toString());
                dismiss();
            }
        });


        return view;
    }



    //set dialog fragment listener (calling activity that will
    //receive the resulting input text)
    public void setDialogListener(DialogListener listener){
        this.listener = listener;
    }



    //dialog listener interface
    public interface DialogListener{
        public void getUserInput(String userInput);
    }



}
