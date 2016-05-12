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
import android.widget.TextView;

import com.parse.ParseObject;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;
import com.slava.chat.Utils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FragmentMessages extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<ParseObject> currentDialogList;
    private List<ParseObject> messagesList;
    private ListView listMessages;
    private FrameLayout frameLayoutNoMessages;
    private MessagesListAdapter dialogsAdapter;
    private OnFragmentInteractionListener mListener;
    private String senderPhoneNumber;
    private String receiverPhoneNumber;
    private MessagesListAdapter messagesListAdapter;
    private boolean showNoMessageView = false;
    private EditText editTextMessage;

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
        senderPhoneNumber = args.getString("senderPhoneNumber");
        receiverPhoneNumber = args.getString("receiverPhoneNumber");
        String titleActionBar = args.getString("titleActionBar");

        listMessages = (ListView) view.findViewById(R.id.list_messages);
        listMessages.setStackFromBottom(true);
        editTextMessage = (EditText) view.findViewById(R.id.edit_text_message);
        frameLayoutNoMessages = (FrameLayout) view.findViewById(R.id.no_messages);
        Button buttonSendMessage = (Button) view.findViewById(R.id.button_message);

        mListener.setTitleToolbar(titleActionBar);

        loadCurrentDialog();

        messagesListAdapter = new MessagesListAdapter();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextMessage.getText().length() > 0) {

                    if (currentDialogList == null) {
                        createNewDialog();
                    } else {
                        sendMessage();
                    }
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

    private void loadCurrentDialog() {

        Account.loadSelectedDialog(senderPhoneNumber, receiverPhoneNumber, new Account.CallbackLoadObject() {

            @Override
            public void success(List<ParseObject> list) {
                if (list.size() != 0) {
                    currentDialogList = list;
                    loadMessages();
                } else {
                    showNoMessageView();
                }
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });
    }

    private void loadMessages() {

        Account.loadMessages(currentDialogList.get(0), new Account.CallbackLoadObject() {
            @Override
            public void success(List<ParseObject> list) {

                if (list.size() != 0) {
                    messagesList = list;
                    if (listMessages.getAdapter() == null) {
                        listMessages.setAdapter(messagesListAdapter);
                    }
                    if (showNoMessageView == true) {
                        hideNoMessageView();
                    }
                    //messagesListAdapter.notifyDataSetChanged();
                } else {
                    showNoMessageView();
                }
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });

    }

    private void createNewDialog() {

        Account.loadSelectedDialog(senderPhoneNumber, receiverPhoneNumber, new Account.CallbackLoadObject() {

            @Override
            public void success(List<ParseObject> list) {
                if (list.size() != 0) {
                    currentDialogList = list;
                    sendMessage();
                } else {
                    Account.createNewDialog(senderPhoneNumber, receiverPhoneNumber, new Account.Callback() {
                        @Override
                        public void success() {
                            Account.loadSelectedDialog(senderPhoneNumber, receiverPhoneNumber, new Account.CallbackLoadObject() {

                                @Override
                                public void success(List<ParseObject> list) {
                                    if (list.size() != 0) {
                                        currentDialogList = list;
                                        sendMessage();
                                    }
                                }

                                @Override
                                public void e(String s) {
                                    Log.d("LOG", "Error: " + s);
                                }
                            });
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

    private void sendMessage() {

        Account.sendMessage(currentDialogList.get(0), editTextMessage.getText().toString(), senderPhoneNumber, receiverPhoneNumber, new Account.Callback() {
            @Override
            public void success() {
                editTextMessage.getText().clear();
                Utils.hideKeyboard(editTextMessage);
                loadMessages();
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });

    }

    private void showNoMessageView() {
        if (frameLayoutNoMessages.getVisibility() == View.GONE && listMessages.getVisibility() == View.VISIBLE) {
            listMessages.setVisibility(View.GONE);
            frameLayoutNoMessages.setVisibility(View.VISIBLE);
            showNoMessageView = true;
        }
    }

    private void hideNoMessageView() {
        if (frameLayoutNoMessages.getVisibility() == View.VISIBLE && listMessages.getVisibility() == View.GONE) {
            listMessages.setVisibility(View.VISIBLE);
            frameLayoutNoMessages.setVisibility(View.GONE);
            showNoMessageView = false;
        }
    }


    public interface OnFragmentInteractionListener {

        void setDrawerLockMode(int i);

        void setTitleToolbar(String s);
    }

    private class MessagesListAdapter extends BaseAdapter {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            return messagesList.size();
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

            if (convertView == null)
                convertView = inflater.inflate(R.layout.message_item, parent, false);

            TextView msgTextView, msgTimeView;

            View sendMessageBox = convertView.findViewById(R.id.send_message_box);
            View receiveMessageBox = convertView.findViewById(R.id.rcv_message_box);

            String phoneNumber = messagesList.get(position).getString("senderPhoneNumber");

            View visibleView;
            if (phoneNumber.equals(senderPhoneNumber)) {
                visibleView = sendMessageBox;
                msgTextView = (TextView) convertView.findViewById(R.id.send_text_message);
                msgTimeView = (TextView) convertView.findViewById(R.id.send_time);
            } else {
                visibleView = receiveMessageBox;
                msgTextView = (TextView) convertView.findViewById(R.id.rcv_text_message);
                msgTimeView = (TextView) convertView.findViewById(R.id.rcv_time);

            }

            msgTextView.setText(messagesList.get(position).getString("textMessage"));
            msgTimeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(messagesList.get(position).getUpdatedAt()));

            View[] allViews = {sendMessageBox, receiveMessageBox};
            for (View v : allViews) {
                if (v == visibleView) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }
}
