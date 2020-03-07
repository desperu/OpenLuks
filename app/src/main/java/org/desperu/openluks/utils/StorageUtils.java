package org.desperu.openluks.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.desperu.openluks.R;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static android.content.ContentValues.TAG;

public class StorageUtils {

    @NotNull
    private static File createOrGetFile(File destination, String fileName, String folderName) {
        File folder = new File(destination, folderName);
        return new File(folder, fileName);
    }

    public static String getTextFromStorage(File rootDestination, Context context, String fileName, String folderName){
        File file = createOrGetFile(rootDestination, fileName, folderName);
        return readOnFile(context, file);
    }

    public static void setTextInStorage(File rootDestination, Context context, String fileName, String folderName, String text){
        File file = createOrGetFile(rootDestination, fileName, folderName);
        writeOnFile(context, text, file);
    }

    @NotNull
    public static File getFileFromStorage(File rootDestination, Context context, String fileName, String folderName) {
        return createOrGetFile(rootDestination, fileName, folderName);
    }

    // ----------------------------------
    // EXTERNAL STORAGE
    // ----------------------------------

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // ----------------------------------
    // READ & WRITE ON STORAGE
    // ----------------------------------

    private static String readOnFile(Context context, @NotNull File file) {

        String result = null;
        if (file.exists()) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file));
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    result = sb.toString();
                } finally {
                    br.close();
                }
            } catch (IOException e) {
                Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    private static void writeOnFile(Context context, String text, @NotNull File file) {

        try {
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            Writer w = new BufferedWriter(new OutputStreamWriter(fos));

            try {
                w.write(text);
                w.flush();
                fos.getFD().sync();
            } finally {
                w.close();
                Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Reload Media Scanner to refresh media list after mount storage.
     * @param context Context from this method is called.
     */
    public static void reloadMediaScanner(Context context, String[] path) {
        MediaScannerConnection.scanFile(context, path, null, (path1, uri) ->
                Log.i(TAG, String.format("Scanned path %s -> URI = %s", path1, uri.toString())));
    }
}