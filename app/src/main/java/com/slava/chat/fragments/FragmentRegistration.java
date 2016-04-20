package com.slava.chat.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentRegistration.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentRegistration#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistration extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText txtPhone;
    private EditText txtPwd;
    private Button btnReg;

    private OnFragmentInteractionListener mListener;

    public FragmentRegistration() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegistration.
     */
    public static FragmentRegistration newInstance(String param1, String param2) {
        FragmentRegistration fragment = new FragmentRegistration();
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
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        txtPhone = (EditText) view.findViewById(R.id.txtPhone);
        txtPwd = (EditText) view.findViewById(R.id.txtPwd);
        btnReg = (Button) view.findViewById(R.id.btnReg);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View.OnClickListener pressBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = txtPhone.getText().toString();
                String pwd = txtPwd.getText().toString();

                switch (v.getId()) {
                    case R.id.btnReg:
                        final ProgressDialog pd = ProgressDialog.show(getActivity(), null, getString(R.string.progress_wait), false, false);
                        Account.signUp(login, pwd, new Account.CallbackLogIn() {
                            @Override
                            public void success() {
                                Utils.hideKeyboard(getActivity());
                                ((MainActivity) getActivity()).loadingFragment(MainActivity.FRAGMENT_MAIN);
                                pd.dismiss();
                            }

                            @Override
                            public void e(String s) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        };
        btnReg.setOnClickListener(pressBtn);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void setDrawerLockMode(int i);
    }
}
