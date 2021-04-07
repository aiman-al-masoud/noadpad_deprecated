package com.example.rudimentalnotesapp.allPurposeFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.R;

public class QuickMessageDialogFragment extends DialogFragment {

    String quickMessage;

    public QuickMessageDialogFragment(String quickMessage){
        this.quickMessage = quickMessage;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get reference to this dialog fragment
        DialogFragment eigenReference = this;

        //inflate view
        View view = inflater.inflate(R.layout.quick_message_fragment, container, false);

        //set quick message
        ((TextView)view.findViewById(R.id.quickMessageTextView)).setText(quickMessage);

        return view;
    }



}
