package com.slava.chat.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;
import com.slava.chat.Utils;

public class FragmentSetting extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button buttonLogOut;
    private TextView userPhoneNumberTextView;
    private OnFragmentInteractionListener mListener;

    public FragmentSetting() {
        // Required empty public constructor
    }

    public static FragmentSetting newInstance(String param1, String param2) {
        FragmentSetting fragment = new FragmentSetting();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        buttonLogOut = (Button) view.findViewById(R.id.button_log_out);
        userPhoneNumberTextView = (TextView) view.findViewById(R.id.user_phone_number);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_log_out:
                        final ProgressDialog progressDialog = Utils.showProgressDialog(getActivity(), getString(R.string.progress_wait), false, false);
                        progressDialog.show();
                        Account.logOut(new Account.Callback() {
                            @Override
                            public void success() {
                                progressDialog.dismiss();
                                Utils.detachAllFragments(getActivity());
                                //Account.updateUserStatus(false);
                                mListener.loadFragment(new FragmentLogin(), false, false);
                            }

                            @Override
                            public void e(String s) {
                                Log.d("LOG", "Error: " + s);
                            }
                        });
                        break;
                }
            }
        });
        //set Toolbar title
        mListener.setTitleToolbar(getString(R.string.menu_profile));

        userPhoneNumberTextView.setText(ParseUser.getCurrentUser().getUsername());

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
        void setTitleToolbar(String s);

        void setDrawerLockMode(int i);

        void loadFragment(Fragment fragment, boolean showActionBar, boolean addBackStack);
    }
}
