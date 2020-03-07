package org.desperu.openluks.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public class BashCommand {

    /**
     * Execute one line (simple) bash command.
     * @param cmd Command to execute.
     */
    public static void executeBashCommand(List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            String line;

            // To get Error Stream
            BufferedReader stdin = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            while ( (line = stdin.readLine()) != null ) {
                Log.d("ExecuteBashCommand", "Input Stream : " + line);
            }

            // To get Error Stream
            BufferedReader stdError = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            while ( (line = stdError.readLine()) != null ) {
                Log.e("ExecuteBashCommand", "Error Stream : " + line);
            }

            int exitVal = process.waitFor();
            Log.d("ExecuteBashCommand", "Command " + cmd.toString() + " waitFor exitValue : " + exitVal);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e("ExecuteBashCommand", "Catch Command : " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void preCmd() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("ls");
        pb.inheritIO();
        pb.directory(new File("/system/bin"));
        pb.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void executeCommands() throws IOException {

        File tempScript = createTempScript();

        try {
            ProcessBuilder pb = new ProcessBuilder("sh", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            tempScript.delete();
        }
    }

    @NotNull
    private static File createTempScript() throws IOException {
        File tempScript = File.createTempFile("script", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/system/bin/sh");
        printWriter.println("cd /system/bin");
        printWriter.println("ls");

        printWriter.close();

        return tempScript;
    }
}