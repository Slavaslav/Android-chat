package com.slava.chat.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;
import com.slava.chat.Utils;

public class FragmentLogin extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText phoneText;
    private EditText passwordText;
    private Button loginButton;
    private Button openRegistrationButton;

    private OnFragmentInteractionListener mListener;

    public FragmentLogin() {
        // Required empty public constructor
    }

    public static FragmentLogin newInstance(String param1, String param2) {
        FragmentLogin fragment = new FragmentLogin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        phoneText = (EditText) view.findViewById(R.id.phone_text);
        passwordText = (EditText) view.findViewById(R.id.password_text);
        loginButton = (Button) view.findViewById(R.id.login_button);
        openRegistrationButton = (Button) view.findViewById(R.id.open_registration_button);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = phoneText.getText().toString();
                String password = passwordText.getText().toString();

                switch (v.getId()) {
                    case R.id.login_button:
                        final ProgressDialog progressDialog = Utils.showProgressDialog(getActivity(), getString(R.string.progress_wait), false, false);
                        progressDialog.show();
                        Account.logIn(login, password, new Account.Callback() {
                            @Override
                            public void success() {
                                Utils.hideKeyboard(passwordText);
                                progressDialog.dismiss();
                                mListener.loadFragment(new FragmentMain(), true, false);
                            }

                            @Override
                            public void e(String s) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.open_registration_button:
                        mListener.loadFragment(new FragmentRegistration(), false, false);
                        break;
                }
            }
        };
        loginButton.setOnClickListener(onClickListener);
        openRegistrationButton.setOnClickListener(onClickListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setDrawerLockMode(MainActivity.LOCK_MODE_LOCKED_CLOSED);
    }

    public interface OnFragmentInteractionListener {

        void setDrawerLockMode(int i);

        void loadFragment(Fragment fragment, boolean showActionBar, boolean addBackStack);
    }
}
