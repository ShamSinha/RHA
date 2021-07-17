package com.example.ledblink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class MainActivity extends AppCompatActivity {

    Switch simpleSwitch;
    boolean stopThread =false ;
    TextView status ;
    Button givePermission;

    GpioProcessor gpioProcessor = new GpioProcessor();

    GpioProcessor.Gpio led = gpioProcessor.getPin2();
    GpioProcessor.Gpio jet = gpioProcessor.getPin3();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        simpleSwitch = findViewById(R.id.switch1);
        status = findViewById(R.id.textView);
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
    public void onClickRunScript(View view) {
        Utils.runCommand("echo 2 > /sys/class/gpio/export");
        Utils.chmod("777","/sys/class/gpio/gpio2");
        Utils.chmod("777","/sys/class/gpio/gpio2/value");
        Utils.chmod("777","/sys/class/gpio/gpio2/direction");
        Utils.runCommand("echo 3 > /sys/class/gpio/export");
        Utils.chmod("777","/sys/class/gpio/gpio3");
        Utils.chmod("777","/sys/class/gpio/gpio3/value");
        Utils.chmod("777","/sys/class/gpio/gpio3/direction");

    }


}