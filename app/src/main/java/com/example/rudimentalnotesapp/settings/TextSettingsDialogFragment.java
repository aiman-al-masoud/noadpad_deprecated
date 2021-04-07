package com.example.rudimentalnotesapp.settings;

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
import com.example.rudimentalnotesapp.settings.Settings;

public class TextSettingsDialogFragment extends DialogFragment {


    //new text size input field
    EditText newTextSizeField;

    //go button
    Button setButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_settings, container, false);

        //get text size field
        newTextSizeField = view.findViewById(R.id.textSizeInput);
        //get text size set button
        setButton = view.findViewById(R.id.textSizeSet);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    //get new text size from input field
                    int newTextSize = Integer.parseInt(newTextSizeField.getText().toString().trim());

                    //set new text size as global setting
                    Settings.setTextSize(newTextSize);

                }catch(NumberFormatException e){
                    e.printStackTrace();
                }

            }
        });


        return view;
    }

}
