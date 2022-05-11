package org.desperu.openluks.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;

import org.desperu.openluks.BuildConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ManageVolume {

    public void ObbExpansionsManager(@NotNull Context context, final OnObbStateChangeListener listener) {
        Log.d("MountVolume", "Creating new instance...");
        String packageName = context.getPackageName();
        Log.d("MountVolume", "Package name = " + packageName);

        String packageVersion = String.valueOf(BuildConfig.VERSION_CODE);
        Log.d("MountVolume", "Package version = " + packageVersion);
//        this.listener = listener;
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        File mainFile = new File(Environment.getExternalStorageDirectory() + "/Android/obb/" + packageName + "/"
                + "main." + packageVersion + "." + packageName + ".obb");

        Log.d("MountVolume", "Check if main file already mounted: " + sm.isObbMounted(mainFile.getAbsolutePath()));
        if (sm.isObbMounted(mainFile.getAbsolutePath())) {
            Log.d("MountVolume", "Main file already mounted.");
            String main = sm.getMountedObbPath(mainFile.getAbsolutePath());
//            listener.onMountSuccess();
        } else {
//            mountMain();
            sm.mountObb(String.valueOf(mainFile), null, listener);
        }

        if (!mainFile.exists()) {
            Log.d("MountVolume", "No expansion files found!");
//            listener.onFilesNotFound();
        }
    }

    public static void mountVolume(@NotNull Context context, String volId) {
//        android.os.storage.IMountService
//        IStorageManager iStorageManager = (IStorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//        try {
//            iStorageManager.mount("/dev/mapper/myluks"); // need id partition UUID
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        sm.mount(volId);
    }
}