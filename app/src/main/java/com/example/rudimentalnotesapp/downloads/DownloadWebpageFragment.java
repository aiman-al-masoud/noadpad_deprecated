package com.example.rudimentalnotesapp.downloads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rudimentalnotesapp.R;

public class DownloadWebpageFragment extends DialogFragment {

    DownloaderTask downloader = new DownloaderTask();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_dialog_fragment, container, false);

        //set message
        ((TextView)view.findViewById(R.id.textInputPromptMessage)).setText("enter a full url:");

        //get text input bar
        EditText urlInput = view.findViewById(R.id.searchBar);

        //get button and set its action
        Button button = view.findViewById(R.id.goButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get a note folder with text from the requested page
                //and launch text editor after the download is done
                downloader.downloadPage(urlInput.getText().toString(), true);
            }
        });

        return view;
    }
}
