package com.example;

import com.topjohnwu.superuser.Shell;

public class Utils {

    public static boolean rootAccess() {
        return Shell.rootAccess();
    }

    public static void runCommand(String command) {
        Shell.su(command).exec();
    }

    public static void chmod(String permission, String path) {
        runCommand("chmod " + permission + " " + path);
    }

}