package com.creiss.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.creiss.R;
import com.creiss.network.ApiListener;

public class AuthenticationFragment extends Fragment implements View.OnClickListener, ApiListener {
    public static final String TAG = AuthenticationFragment.class.getSimpleName();

    private EditText etAuthKey;
    private Button btnVerify;
    private ProgressDialog progressDialog;
    private TextInputLayout tilAuthKey;

    private NavigationListener mListener;
    private AuthenticationInteractor interactor;

    public static AuthenticationFragment newInstance() {
        Bundle args = new Bundle();

        AuthenticationFragment fragment = new AuthenticationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //LIFECYCLE BEGINS
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationListener)
            mListener = (NavigationListener) context;
        else throw new RuntimeException("WHere have you put me?");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        etAuthKey = (EditText) view.findViewById(R.id.et_authkey);
        tilAuthKey = (TextInputLayout) view.findViewById(R.id.til_authkey);
        btnVerify = (Button) view.findViewById(R.id.btn_verify);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnVerify.setOnClickListener(this);
        etAuthKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validate()) {
                        verifyCredential(String.format("%s", etAuthKey.getText().toString()));
                    }
                    return true;
                }
                return false;
            }
        });
        interactor = new AuthenticationInteractor();
        interactor.onCreate();
    }

    @Override
    public void onDestroy() {
        interactor.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //LIFECYCLE ENDS
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_verify:
                if (validate()) {
                    verifyCredential(String.format("%s", etAuthKey.getText().toString()));
                }
                break;
            default:
                break;
        }
    }

    private void verifyCredential(String key) {
        interactor.authenticate(key, this);
    }

    private boolean validate() {
        String text = etAuthKey.getText().toString();
        boolean res = true;
        if (TextUtils.isEmpty(text)) {
            res = false;
            etAuthKey.setError(getString(R.string.str_auth_empty));
        } else if (text.length() > 100) {
            res = false;
            etAuthKey.setError(getString(R.string.str_auth_long));
        }
        return res;
    }

    @Override
    public void progressDialog(boolean show) {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.str_wait));
            if (show) {
                progressDialog.show();
            }
        } else if (!show && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void success() {
        if (null != mListener)
            mListener.navigate();
    }

    @Override
    public void failure(Throwable t) {
        tilAuthKey.setError(t.getMessage());
    }
}
