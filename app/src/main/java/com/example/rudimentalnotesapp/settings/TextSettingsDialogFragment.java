package com.example.rudimentalnotesapp.settings;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.collections.Collection;
import com.example.rudimentalnotesapp.mainNotesList.MainActivity;
import com.example.rudimentalnotesapp.settings.Settings;

import java.time.LocalDateTime;

public class TextSettingsDialogFragment extends DialogFragment {


    //new text size input field
    EditText newTextSizeField;

    //go button
    Button setButton;

    //themes' drop down menu
    Spinner themesDropDownMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_settings, container, false);

        //get text size field
        newTextSizeField = view.findViewById(R.id.textSizeInput);
        //get text size set button
        setButton = view.findViewById(R.id.textSizeSet);

        //get themes' drop down menu
        themesDropDownMenu = (Spinner)view.findViewById(R.id.themesDropDownMenu);


        //set text size button
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

        //set themes' drop down menu's options and actions
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SettingsActivity.settingsActivity, R.array.color_themes_array, R.layout.support_simple_spinner_dropdown_item);
        themesDropDownMenu.setAdapter(adapter);


        //load previous theme selection
        themesDropDownMenu.setSelection(Settings.getCurrentColorTheme());


        themesDropDownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) id){
                    case 0: //DEFAULT THEME
                        Settings.setBackgroundAndForegroundColor("WHITE", "BLACK");
                        break;
                    case 1: //DARK THEME
                        Settings.setBackgroundAndForegroundColor("BLACK", "WHITE");
                        break;
                    case 2: //SEPIA
                        Settings.setBackgroundAndForegroundColor("LTGRAY", "GRAY");
                        break;
                    case 3: //BLUE
                        Settings.setBackgroundAndForegroundColor("BLUE", "BLACK");
                        break;
                }

                //save current color theme
                Settings.setCurrentColorTheme((int)id);

                //reset colors of settings activity soon after the change
                SettingsActivity.settingsActivity.resetColors();

                //tell main activity it needs to change
                MainActivity.needToRefresh = true;


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

}
