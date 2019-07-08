package com.creiss.base_class_section;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.creiss.R;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private Snackbar snackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(snackbar!=null){
            snackbar.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showLoading() {
        hideLoading();
        try {
//            mProgressDialog = new ProgressDialog (this);
////            mProgressDialog.setContentView(R.layout.progress_dialog);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setTitle("Fetching Files...");
//            mProgressDialog.show();
//            mProgressDialog.setContentView(R.layout.progress_dialog);
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(true);//you can cancel it by pressing back button
            mProgressDialog.setMessage("Fetching Files...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setProgress(0);//initially progress is 0
            mProgressDialog.setMax(100);//sets the maximum value 100
            mProgressDialog.show();//displays the progress bar
        } catch (Exception e) {
            Log.d ("Exc", e.getMessage());
        }
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showSnackBar(String message) {
        snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_white));
        snackbar.show();
    }

    public void showMessage (String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
