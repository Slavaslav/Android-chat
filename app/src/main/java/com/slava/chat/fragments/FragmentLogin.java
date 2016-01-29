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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentLogin.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogin extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText txtPhone;
    EditText txtPwd;
    Button btnLog;
    Button btnReg;
    ProgressDialog pd;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public FragmentLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLogin.
     */
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtPhone = (EditText) getView().findViewById(R.id.txtPhone);
        txtPwd = (EditText) getView().findViewById(R.id.txtPwd);
        btnLog = (Button) getView().findViewById(R.id.btnLog);
        btnReg = (Button) getView().findViewById(R.id.btnReg);

        View.OnClickListener pressBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = txtPhone.getText().toString();
                String pwd = txtPwd.getText().toString();
                Account acc = new Account();
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Loading...");
                pd.setIndeterminate(false);
                pd.setCancelable(false);
                pd.show();

                switch (v.getId()) {
                    case R.id.btnLog:
                        acc.logIn(login, pwd, new MyCallback() {
                            @Override
                            public void loggedIn() {
                                pd.dismiss();
                                ((MainActivity) getActivity()).loadingFragment("fragmentMain");
                            }

                            @Override
                            public void e(String s) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.btnReg:
                        ((MainActivity) getActivity()).loadingFragment("fragmentReg");
                        break;
                }
            }
        };
        btnLog.setOnClickListener(pressBtn);
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

    public interface MyCallback {
        void loggedIn();

        void e(String s);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
