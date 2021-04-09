package com.example.rudimentalnotesapp.navigation;

import android.app.Instrumentation;
import android.view.KeyEvent;

public class SimulateKeyPress {

    public static void press(int KeyEvent){
         new Thread(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent);
            }
        }).start();

    }



}
