package com.example.ledblink;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diozero.devices.MFRC522;
import com.diozero.util.Hex;
import com.diozero.util.SleepUtil;
import com.example.Permissions;
import com.example.Utils;

import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch simpleSwitch;
    boolean stopThread =false ;
    TextView status ;
    TextView rfid;

    //GpioProcessor gpioProcessor = new GpioProcessor();

    //GpioProcessor.Gpio led ;
    //GpioProcessor.Gpio jet ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        simpleSwitch = findViewById(R.id.switch1);
        status = findViewById(R.id.textView);
        rfid = findViewById(R.id.rfid_tag);
        status.setText("Not PluggedIn");

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                /*
                if (isChecked) led.high();
                else led.low();

                 */
                // true if the switch is in the On position
            }
        });
        //setGivePermission();

        //CheckGPIOCableIn();

        ReadCard(0,25);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!Utils.rootAccess()){
            Log.d("TAG","No root access");
        }
        //led = gpioProcessor.getPin(2);
        //jet = gpioProcessor.getPin(3);


        //led.out();
        //jet.in();

    }
/*
    private void CheckGPIOCableIn(){
        final Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted() && !stopThread){
                    try {
                        if(jet.getValue() == 0){
                            stopThread = true;
                            Log.d("TAG",String.valueOf(jet.getValue()));
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

 */

    public void ReadCard( int chipSelect , int resetPin){

        Log.d("TAG","hello");
        waitForCard(chipSelect,resetPin);

    }

    public void waitForCard(int chipSelect, int resetPin) {
        MFRC522 mfrc522 = new MFRC522(chipSelect, resetPin);
        //Log.d("TAG",mfrc522.toString());
			if (mfrc522.performSelfTest()) {
				Logger.debug("Self test passed");
			} else {
				Logger.debug("Self test failed");
			}
            // Wait for a card
            MFRC522.UID uid = null;
            while (uid == null) {
                Logger.info("Waiting for a card");
                uid = getID(mfrc522);
                Logger.debug("uid: {}", uid);
                SleepUtil.sleepSeconds(1);
            }
        }


    private static MFRC522.UID getID(MFRC522 mfrc522) {
        // If a new PICC placed to RFID reader continue
        if (! mfrc522.isNewCardPresent()) {
            return null;
        }
        Logger.debug("A card is present!");
        // Since a PICC placed get Serial and continue
        MFRC522.UID uid = mfrc522.readCardSerial();
        if (uid == null) {
            return null;
        }

        // There are Mifare PICCs which have 4 byte or 7 byte UID care if you use 7 byte PICC
        // I think we should assume every PICC as they have 4 byte UID
        // Until we support 7 byte PICCs
        Logger.info("Scanned PICC's UID: {}", Hex.encodeHexString(uid.getUidBytes()));

        mfrc522.haltA();

        return uid;
    }


    public void setGivePermission() {
        List<Integer> gpioList= new ArrayList<Integer>();
        gpioList.add(2);
        gpioList.add(3);
        //gpioList.add(25); /// reset pin 22
        //gpioList.add(8);
        //gpioList.add(9);
        //gpioList.add(10);
        //gpioList.add(11);

        Permissions.GivePermissionToGpio(gpioList);

    }



}