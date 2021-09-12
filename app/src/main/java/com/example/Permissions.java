package com.example;

import java.util.List;

public class Permissions {

    public static void GivePermissionToGpio(List<Integer> gpioList) {
        for(int i = 0 ; i < gpioList.size() ; i++) {
            String Pin = gpioList.get(i).toString();
            Utils.runCommand("echo " + Pin + " > /sys/class/gpio/export");
            Utils.chmod("777", "/sys/class/gpio/gpio" + Pin);
            Utils.chmod("777", "/sys/class/gpio/gpio" + Pin + "/value");
            Utils.chmod("777", "/sys/class/gpio/gpio" + Pin + "/direction");
        }
    }

    public static void GivePermissionToSerial(){
        Utils.chmod("777", "/dev/ttyACM0");
    }
}
