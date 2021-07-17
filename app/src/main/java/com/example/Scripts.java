package com.example;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scripts {

    public static String mScriptName;
    public static String mScriptPath;

    public static List<String> mOutput = null;

    private static final String SCRIPTS = Utils.getInternalDataStorage();
    public static File ScriptFile() {
        return new File(SCRIPTS);
    }


    private static void applyScript(String file) {

        mOutput.add("Checking Output!");
        Utils.runCommand("sleep 1");
        mOutput.add("********************");
        Utils.runAndGetLiveOutput("sh " + file, mOutput);
    }

    public static String readScript(String file) {
        return Utils.read(file);
    }

    private void sendRequest(String file) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                applyScript(file);
            }
        });
        executorService.shutdown();
    }

    private static void makeScriptFolder() {
        if (ScriptFile().exists() && ScriptFile().isFile()) {
            ScriptFile().delete();
        }
        ScriptFile().mkdirs();
    }

    public static void importScript(String string) {
        makeScriptFolder();
        Utils.create(Utils.read(string) , SCRIPTS + "/" + new File(string).getName());
    }

    public static void createScript(String file, String text) {
        makeScriptFolder();
        Utils.create(text, file);
    }

}
