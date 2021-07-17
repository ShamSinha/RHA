package com.example;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ledblink.BuildConfig;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class Utils {

    static {
        // Set settings before the main shell can be created
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }

    public static boolean rootAccess() {
        return Shell.rootAccess();
    }

    public static void runCommand(String command) {
        Shell.su(command).exec();
    }
    public static void chmod(String permission, String path) {
        runCommand("chmod " + permission + " " + path);
    }

    @NonNull
    static String runAndGetOutput(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            List<String> outputs = Shell.su(command).exec().getOut();
            if (ShellUtils.isValidOutput(outputs)) {
                for (String output : outputs) {
                    sb.append(output).append("\n");
                }
            }
            return removeSuffix(sb.toString()).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static void runAndGetLiveOutput(String command, List<String> output) {
        if (rootAccess()) {
            Shell.su(command).to(output, output).exec();
        } else {
            try {
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader mInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader mError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = mInput.readLine()) != null) {
                    output.add(line);
                }
                while ((line = mError.readLine()) != null) {
                    output.add(line);
                }
            } catch (Exception ignored) {
            }
        }
    }


    private static String removeSuffix(@Nullable String s) {
        if (s != null && s.endsWith("\n")) {
            return s.substring(0, s.length() - "\n".length());
        }
        return s;
    }

    public static void create(String text, String path) {
        if (path.startsWith("/storage/") || path.contains(BuildConfig.APPLICATION_ID)) {
            try {
                File mFile = new File(path);
                mFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(mFile);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);
                myOutWriter.append(text);
                myOutWriter.close();
                fOut.close();
            } catch (Exception ignored) {
            }
        } else {
            runCommand("echo '" + text + "' > " + path);
        }
    }

    public static String getInternalDataStorage() {
        return Environment.getExternalStorageDirectory().toString() + "/scripts";
    }

    public static String read(String file) {
        if (!file.startsWith("/storage/")) {
            return runAndGetOutput("cat '" + file + "'");
        } else {
            try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                return sb.toString();
            } catch (IOException ignored) {
            }
            return null;
        }
    }

    public static void delete(String path) {
        if (path.startsWith("/storage/") || path.contains(BuildConfig.APPLICATION_ID)) {
            new File(path).delete();
        } else {
            runCommand("rm -r " + path);
        }
    }

    public static boolean checkWriteStoragePermission(Context context) {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }



    public static String getExtension(String string) {
        return android.webkit.MimeTypeMap.getFileExtensionFromUrl(string);
    }

}
