package com.example.ledblink;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;

public class SerialCommArduino {
    public static final String TAG = "Serial";

    private String PATH = "/dev/ttyACM0";

    public String GetData() {
        Log.v(TAG, "Getting Value");
        BufferedReader br;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(PATH));
            line = br.readLine();
            br.close();

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return line ;



    }

}
