package org.desperu.openluks.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import org.desperu.openluks.R;
import org.desperu.openluks.ui.Base.BaseFragment;
import org.desperu.openluks.utils.BashCommand;
import org.desperu.openluks.utils.EncryptedFile;
import org.desperu.openluks.utils.ManageVolume;
import org.desperu.openluks.utils.StorageUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class MainFragment extends BaseFragment {

    // FOR DATA
    private static final String PASSWORD = "password";
    private boolean isMountPermissionEnabled = true;
    // TODO in constant
    private static final String[] PERMS = {Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}; // TODO don't ask...
    private static final int PERM_MOUNT_FILESYSTEMS = 100;

    // FOR DESIGN
    @Nullable @BindView(R.id.alert_dialog_linear_root) LinearLayout linearRoot;

    // --------------
    // BASE METHODS
    // --------------

    @Override
    protected int getFragmentLayout() { return R.layout.fragment_main; }

    @Override
    protected void configureDesign() {
    }


    public MainFragment() {
        // Needed empty constructor
    }

    @NotNull
    @Contract("_ -> new")
    public static MainFragment newInstance(int position) { return new MainFragment(); }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Check if mount unmount system permission is granted, if not, ask for it.
     */
    private void checkMountSystemPermissionsStatus() {
        assert getContext() != null;
        if (!EasyPermissions.hasPermissions(getContext(), PERMS))
            EasyPermissions.requestPermissions(this, getString(R.string.title_permission),
                    PERM_MOUNT_FILESYSTEMS, PERMS);
        else isMountPermissionEnabled = true;
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        this.isMountPermissionEnabled = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    // --------------
    // ACTION
    // --------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.fragment_main_button_mount)
    void OnClickMount() {
        List<String> cmd = new ArrayList<>();
        cmd.add("echo");
        cmd.add("pwd");
        cmd.add("|");
        cmd.add("su");
        cmd.add("cryptsetup");
        cmd.add("luksOpen");
        cmd.add("/dev/block/mmcblk1p3");
        cmd.add("myluks");
        this.alertDialog(cmd);
    }

    @OnClick(R.id.fragment_main_button_unmount)
    void OnclickUnMount() {
        List<String> cmd = new ArrayList<>();
        cmd.add("id");
        new Handler().post(() -> BashCommand.executeBashCommand(cmd));
    }

    @OnClick(R.id.fragment_main_input_text)
    void OnclickInputText() { }

    @OnClick(R.id.fragment_main_button_set_folder)
    void OnclickSetFolder() { }

    // --------------
    // UI
    // --------------

    private void alertDialog(List<String> cmd) {
        assert getContext() != null;
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        // Create dialog for ask Password
        dialog.setTitle("Password");

        // Add edit text to dialog
        View editView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog, linearRoot);
        final EditText editText = editView.findViewById(R.id.alert_dialog_edit_text);
        dialog.setView(editView);

        // Set positive button
        dialog.setPositiveButton("Mount", (dialog1, which) -> {
            if (getEncodedPwdFile().exists())
                cmd.set(cmd.indexOf("pwd"), EncryptedFile.getDecodedFile(getEncodedPwdFile(), PASSWORD));
            else cmd.set(cmd.indexOf("pwd"), String.valueOf(editText.getText()));
            BashCommand.executeBashCommand(cmd);
            if (isMountPermissionEnabled)
                ManageVolume.mountVolume(getContext(), "/dev/mapper/myluks");// TODO on test
            else checkMountSystemPermissionsStatus();
        });

        // Set neutral button // TODO perform if a kay exist, save key to keystore, save file to external storage, random password
        dialog.setNeutralButton("Save", (dialog1, which) -> {
            getEncodedPwdFile().getParentFile().mkdirs();
            EncryptedFile.setEncodedFile(getEncodedPwdFile(), PASSWORD, String.valueOf(editText.getText()));
            Toast.makeText(getContext(), "Save encrypted password", Toast.LENGTH_SHORT).show();
        });

        // Set negative button
        dialog.setNegativeButton("Cancel", (dialog1, which) -> dialog1.cancel());
        dialog.show();
    }

    @NotNull
    private File getEncodedPwdFile() {
        assert getActivity() != null;
        return StorageUtils.getFileFromStorage(getActivity().getFilesDir(),
                getContext(), "secure", "private");
    }
}