package com.example.rudimentalnotesapp.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rudimentalnotesapp.R;
import com.example.rudimentalnotesapp.collections.Collection;

public class SettingsActivity extends AppCompatActivity {

    Button textStyleSubMenuButton;
    Switch globalEncryptionToggle;
    Button acknowledgementsSubMenuButton;
    Switch toggleSelfSortingCollections;

    //autoreference
    AppCompatActivity eigenReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //set autoreference
        eigenReference = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //get and set text style button
        textStyleSubMenuButton = findViewById(R.id.textStyle);
        textStyleSubMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextSettingsDialogFragment textSettingsDialogFragment = new TextSettingsDialogFragment();
                textSettingsDialogFragment.show(getSupportFragmentManager(), "textStyleOptions");
            }
        });



        //get self sorting collections toggle
        toggleSelfSortingCollections = (Switch) findViewById(R.id.selfSortingCollectionsToggle);

        //load switches previous state
        if(Settings.isSelfSortingCollectionsOn()){
            toggleSelfSortingCollections.setChecked(true);
        }else{
            toggleSelfSortingCollections.setChecked(false);

        }

        //set switch's action
        toggleSelfSortingCollections.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Settings.toggleSelfSortingCollections(true);

                    //add folders to collections proactively
                    if(Settings.isSelfSortingCollectionsOn()){
                        Collection.proactiveAddFoldersToCollections();
                    }

                }else{
                    Settings.toggleSelfSortingCollections(false);
                }
            }
        });



    }






}
