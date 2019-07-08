package com.creiss.settings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.creiss.R;
import com.creiss.job_schedulers.JobSchedulerMaster;
import com.creiss.utility.CreissPreferences;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.security.KeyPairGenerator;
import java.security.interfaces.DSAParams;
import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    public static final String TAG = SettingsFragment.class.getSimpleName();
    public static final int FILE_CODE = 12345;

    private TextView tvPath;
    private Button btnChoosePath;
    private String path;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        btnChoosePath = (Button) view.findViewById(R.id.settings_btn_choose_path);
        tvPath = (TextView) view.findViewById(R.id.settings_tv_directory);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListeners();
        path = CreissPreferences.getInstance().get(CreissPreferences.SHPREF_KEY_PATH);
        if (!TextUtils.isEmpty(path))
            tvPath.setText(path);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initListeners() {
        if (null != btnChoosePath) {
            btnChoosePath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), FilePickerActivity.class);

                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

                    i.putExtra(FilePickerActivity.EXTRA_START_PATH, TextUtils.isEmpty(path) ? Environment.getExternalStorageDirectory().getPath() : path);

                    startActivityForResult(i, FILE_CODE);
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            path = uri.getPath();
                            tvPath.setText(uri.getPath());
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            path = uri.getPath();
                            tvPath.setText(uri.getPath());
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                path = uri.getPath();
                tvPath.setText(uri.getPath());
                // Do something with the URI
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                CreissPreferences.getInstance().set(CreissPreferences.SHPREF_KEY_PATH, path);
                setupJobSchedulerForSync ();
                getActivity().finish();
                DSAParams dsaParams;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupJobSchedulerForSync() {
        if (CreissPreferences.getInstance().getBoolean(CreissPreferences.IS_JOB_SCHEDULER_CALLED).equals(false)) {
            CreissPreferences.getInstance().setBoolean(CreissPreferences.IS_JOB_SCHEDULER_CALLED, true);
            JobSchedulerMaster jobSchedulerMaster = new JobSchedulerMaster(getActivity());
            jobSchedulerMaster.startSync ();
        }
    }
}
