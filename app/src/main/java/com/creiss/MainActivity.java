/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.creiss;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import com.creiss.base_class_section.BaseActivity;
import com.creiss.model.TripScanResponse;
import com.creiss.mvp.TripMVP;
import com.creiss.mvp.TripPresenterImplementer;
import com.creiss.scanner.BarcodeCaptureActivity;
import com.creiss.settings.SettingsActivity;
import com.creiss.utility.CreissPreferences;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, TripMVP.TripView {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_BARCODE_CAPTURE = 7890;
    private static final int TRIP_BARCODE_CAPTURE = 1000;
    private Button mBtnScanProductCode, mBtnScanTripCode;
    TripPresenterImplementer mTripPresenterImplementer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI ();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTripPresenterImplementer.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTripPresenterImplementer.destroyView();
    }

    private void setupUI() {
        mBtnScanProductCode = findViewById(R.id.btn_scan_product_code);
        mBtnScanTripCode = findViewById(R.id.btn_scan_trip_code);
        mBtnScanProductCode.setOnClickListener(this);
        mBtnScanTripCode.setOnClickListener(this);
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            mBtnScanProductCode.setEnabled(true);
            mBtnScanTripCode.setEnabled(true);
        } else {
            mBtnScanProductCode.setEnabled(false);
            mBtnScanTripCode.setEnabled(false);
            requestCameraPermission();
        }mTripPresenterImplementer = new TripPresenterImplementer(this);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, BarcodeCaptureActivity.RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        BarcodeCaptureActivity.RC_HANDLE_CAMERA_PERM);
            }
        };
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan_product_code:
                if (!TextUtils.isEmpty(CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH))) {
                    Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                } else {
                    showAlert(getString(R.string.path_not_defined_message));
                }
                break;
            case R.id.btn_scan_trip_code:
                if (!TextUtils.isEmpty(CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH))) {
                    Intent tripIntent = new Intent(this, BarcodeCaptureActivity.class);
                    startActivityForResult(tripIntent, TRIP_BARCODE_CAPTURE);
                } else {
                    showAlert(getString(R.string.path_not_defined_message));
                }
                break;
        }
    }

    private void showAlert (String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity (intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_to_settings:
                startActivity(SettingsActivity.getIntent(this));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != BarcodeCaptureActivity.RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            mBtnScanProductCode.setEnabled(true);
            mBtnScanTripCode.setEnabled(true);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.appname)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    if (!TextUtils.isEmpty(CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH))) {
                        File f = BarcodeCaptureActivity.search(barcode.rawValue, CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH));
                        if (null != f) {
                            Uri uri = Uri.fromFile(f);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            String mime = "*/*";
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            if (mimeTypeMap.hasExtension(
                                    mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                                mime = mimeTypeMap.getMimeTypeFromExtension(
                                        mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                            intent.setDataAndType(uri, mime);
                            intent.setPackage("com.xyzmo.signature.standalone");
//                            if (isAppInstalled(MainActivity.this, "com.xyzmo.signature.standalone"))
                            if (whetherAppAvailableToHandle(MainActivity.this, intent))
                                startActivity(intent);
                            else {
                                Toast.makeText(getApplicationContext(), "Please install Significant first!", Toast.LENGTH_LONG).show();
                                intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id=com.xyzmo.signature.standalone"));
                                startActivity(intent);
                            }
                        } else {
                            Snackbar.make(mBtnScanProductCode, "File not found! Check Path...", Snackbar.LENGTH_LONG).setAction("Check path", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(SettingsActivity.getIntent(MainActivity.this));
                                }
                            }).show();
                        }
                    } else {
                        Snackbar.make(mBtnScanProductCode, "Please set the path from settings first!", Snackbar.LENGTH_LONG).setAction("Set path", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(SettingsActivity.getIntent(MainActivity.this));
                            }
                        }).show();
                    }
                } else {
                    Snackbar.make(mBtnScanProductCode, R.string.barcode_failure, Snackbar.LENGTH_LONG).show();
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Snackbar.make(mBtnScanProductCode, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Snackbar.LENGTH_LONG).show();
            }
        } else if (requestCode == TRIP_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    showLoading();
                    mTripPresenterImplementer.onTripScanClicked(barcode.displayValue);
                } else {
                    Snackbar.make(mBtnScanTripCode, R.string.barcode_failure, Snackbar.LENGTH_LONG).show();
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Snackbar.make(mBtnScanTripCode, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Snackbar.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean whetherAppAvailableToHandle(Context mContext, Intent intent) {
        PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfos.size() > 0;
    }

    @Override
    public void tripScanSuccessful(List<TripScanResponse> tripScanResponse) {
        hideLoading();
        for (TripScanResponse data: tripScanResponse) {
            createPdf (data.getName(), data.getBytes());
        }
        showMessage (getString(R.string.trip_files_success_message_1) + CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH)
                + getString(R.string.trip_files_success_message_2));
    }

    private void createPdf(String name, String bytes) {
        File mFile = new File(CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH) + "/" + name);
        try {
            FileUtils.writeByteArrayToFile(mFile, Base64.decode(bytes,Base64.DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tripScanFailed(String error) {
        hideLoading();
        showMessage(error);
    }
}
/**
 * This class will search for the file and return
 **/