package com.example.ledblink;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Permissions;
import com.example.Utils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch simpleSwitch;
    boolean stopThread =false ;
    TextView status ;
    TextView rfid;

    GpioProcessor gpioProcessor = new GpioProcessor();

    GpioProcessor.Gpio led = gpioProcessor.getPin(2);
    GpioProcessor.Gpio jet = gpioProcessor.getPin(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        simpleSwitch = findViewById(R.id.switch1);
        status = findViewById(R.id.textView);
        rfid = findViewById(R.id.rfid_tag);
        status.setText("Not PluggedIn");


        led.out();
        jet.in();

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                if (isChecked) led.high();
                else led.low();
                // true if the switch is in the On position
            }
        });
        setGivePermission();

        CheckGPIOCableIn();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!Utils.rootAccess()){
            Log.d("TAG","No root access");
        }

    }


    private void CheckGPIOCableIn(){
        final Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted() && !stopThread){
                    try {
                        if(jet.getValue() == 0){
                            stopThread = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("PluggedIn");
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText("Not PluggedIn");
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        t.start();
    }
    public void setGivePermission() {
        List<Integer> gpioList= new ArrayList<Integer>();
        gpioList.add(2);
        gpioList.add(3);
        gpioList.add(25); /// reset pin 22
        gpioList.add(8);
        gpioList.add(9);
        gpioList.add(10);
        gpioList.add(11);

        Permissions.GivePermissionToGpio(gpioList);

    }



}