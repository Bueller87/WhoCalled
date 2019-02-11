package com.kevlarcodes.whocalled.view.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.kevlarcodes.whocalled.R;
import com.kevlarcodes.whocalled.service.repository.CallLogRepository;


public class MainActivity extends AppCompatActivity {
    private final String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            checkPermissionToExecute(requiredPermissions, CallLogRepository.READ_LOGS);
        }
    }

    private void loadCallLogFragment(){
        CallLogFragment fragment = new CallLogFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, fragment, CallLogFragment.TAG)
                .commit();
    }

    private boolean areAllPermissionsGranted(int[] grantResults) {
        for (int i : grantResults) {
            if (i != PermissionChecker.PERMISSION_GRANTED ) {
                return false;
            }
        }
        return true;
    }
    // A method to check if a permission is granted then execute tasks depending on that particular permission
    private void checkPermissionToExecute(String permissions[], int requestCode) {
        int[] grantResults = new int[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            grantResults[i] =  ContextCompat.checkSelfPermission(this, permissions[i]);
        }

        if (areAllPermissionsGranted(grantResults)) {
            loadCallLogFragment();
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (areAllPermissionsGranted(grantResults)) {
            loadCallLogFragment();
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(R.string.permissions_question)
                    .setTitle(R.string.permissions_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.permissions_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkPermissionToExecute(requiredPermissions, CallLogRepository.READ_LOGS);
                        }
                    })
                    .setNegativeButton(R.string.permissions_exit_app, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        }
    }
}
