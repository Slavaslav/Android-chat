package com.slava.chat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.parse.ParseObject;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;
import com.slava.chat.Utils;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMessages.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMessages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMessages extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<ParseObject> currentDialogList;
    List<ParseObject> messagesList;
    ListView listMessages;
    FrameLayout frameLayoutNoMessages;
    private MessagesListAdapter dialogsAdapter;
    private OnFragmentInteractionListener mListener;

    public FragmentMessages() {
        // Required empty public constructor
    }

    public static FragmentMessages newInstance(String param1, String param2) {
        FragmentMessages fragment = new FragmentMessages();
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
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        Bundle args = getArguments();
        final String senderPhoneNumber = args.getString("senderPhoneNumber");
        final String receiverPhoneNumber = args.getString("receiverPhoneNumber");
        String titleActionBar = args.getString("titleActionBar");

        listMessages = (ListView) view.findViewById(R.id.list_messages);
        listMessages.setStackFromBottom(true);
        final EditText editTextMessage = (EditText) view.findViewById(R.id.edit_text_message);
        frameLayoutNoMessages = (FrameLayout) view.findViewById(R.id.no_messages);
        Button buttonSendMessage = (Button) view.findViewById(R.id.button_message);

        mListener.setTitleToolbar(titleActionBar);

        handleSelectedDialog(senderPhoneNumber, receiverPhoneNumber);

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextMessage.getText().length() > 0) {
                    Account.sendMessage(currentDialogList.get(0), editTextMessage.getText().toString(), senderPhoneNumber, receiverPhoneNumber, new Account.Callback() {
                        @Override
                        public void success() {
                            editTextMessage.getText().clear();
                            Utils.hideKeyboard(editTextMessage);
                        }

                        @Override
                        public void e(String s) {
                            Log.d("LOG", "Error: " + s);
                        }
                    });
                }
            }
        });

        return view;
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

    private void handleSelectedDialog(final String senderPhoneNumber, final String receiverPhoneNumber) {

        Account.loadSelectedDialog(senderPhoneNumber, receiverPhoneNumber, new Account.CallbackLoadObject() {
            @Override
            public void success(List<ParseObject> list) {
                if (list.size() != 0) {
                    currentDialogList = list;
                    Account.loadMessages(currentDialogList.get(0), new Account.CallbackLoadObject() {
                        @Override
                        public void success(List<ParseObject> list) {
                            // messagesList = list;
                            if (list.size() == 0) {
                                if (frameLayoutNoMessages.getVisibility() == View.GONE && listMessages.getVisibility() == View.VISIBLE) {
                                    listMessages.setVisibility(View.GONE);
                                    frameLayoutNoMessages.setVisibility(View.VISIBLE);
                                }
                            } else {
                                // MessagesListAdapter messagesListAdapter = new MessagesListAdapter(list);
                                // listMessages.setAdapter(messagesListAdapter);

                            }
                        }

                        @Override
                        public void e(String s) {
                            Log.d("LOG", "Error: " + s);
                        }
                    });
                } else {
                    Account.createNewDialog(senderPhoneNumber, receiverPhoneNumber, new Account.Callback() {
                        @Override
                        public void success() {
                            handleSelectedDialog(senderPhoneNumber, receiverPhoneNumber);
                        }

                        @Override
                        public void e(String s) {
                            Log.d("LOG", "Error: " + s);
                        }
                    });
                }

            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });
    }

    public interface OnFragmentInteractionListener {

        void setDrawerLockMode(int i);

        void setTitleToolbar(String s);
    }

    private class MessagesListAdapter extends BaseAdapter {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        List<ParseObject> list;

        MessagesListAdapter(List<ParseObject> list) {
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return convertView;
        }
    }
}
