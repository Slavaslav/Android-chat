package com.slava.chat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;
import com.slava.chat.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentMessages extends Fragment {
    Date timeLastMessage;
    private View visibleView;
    private ScrollView emptyList;
    private FrameLayout progressMessages;
    private List<ParseObject> dialogParseObjectsList;
    private ListView messagesList;
    private OnFragmentInteractionListener mListener;
    private String senderPhoneNumber;
    private String recipientPhoneNumber;
    private MessagesListAdapter messagesListAdapter;
    private EditText editTextMessage;
    private ArrayList<Message> messageArrayList = new ArrayList<>();

    public FragmentMessages() {
        // Required empty public constructor
    }

    public static FragmentMessages newInstance(String senderPhoneNumber, String recipientPhoneNumber, String titleActionBar) {
        FragmentMessages fragment = new FragmentMessages();
        Bundle bundle = new Bundle();
        bundle.putString("senderPhoneNumber", senderPhoneNumber);
        bundle.putString("recipientPhoneNumber", recipientPhoneNumber);
        bundle.putString("titleActionBar", titleActionBar);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        Bundle args = getArguments();
        senderPhoneNumber = args.getString("senderPhoneNumber");
        recipientPhoneNumber = args.getString("recipientPhoneNumber");
        String titleActionBar = args.getString("titleActionBar");

        if (titleActionBar == null) {
            mListener.setTitleToolbar(recipientPhoneNumber);
        } else {
            mListener.setTitleToolbar(titleActionBar);
        }


        emptyList = (ScrollView) view.findViewById(R.id.empty_list);
        progressMessages = (FrameLayout) view.findViewById(R.id.progress_messages);
        messagesList = (ListView) view.findViewById(R.id.messages_list);
        messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messagesList.setStackFromBottom(true);
        editTextMessage = (EditText) view.findViewById(R.id.edit_text_message);
        final Button buttonSendMessage = (Button) view.findViewById(R.id.button_message);

        loadCurrentDialog();

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextMessage.getText().length() > 0) {
                    buttonSendMessage.setEnabled(false);
                    if (dialogParseObjectsList == null) {
                        createNewDialog();
                    } else {
                        sendMessage();
                    }
                }
            }
        });

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    buttonSendMessage.setEnabled(true);
                } else {
                    buttonSendMessage.setEnabled(false);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

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

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideKeyboard(editTextMessage);
    }


    private void loadCurrentDialog() {

        Account.loadSelectedDialog(senderPhoneNumber, recipientPhoneNumber, new Account.CallbackLoadObject() {

            @Override
            public void success(List<ParseObject> list) {
                if (list.size() != 0) {
                    visibleView = messagesList;
                    dialogParseObjectsList = list;
                    loadMessages();
                } else {
                    visibleView = emptyList;
                }
                View[] views = new View[]{messagesList, progressMessages, emptyList};
                setVisibilityViews(views, visibleView);
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });
    }

    private void loadMessages() {

        if (messageArrayList.isEmpty()) {
            Account.loadAllMessages(dialogParseObjectsList, new Account.CallbackLoadObject() {
                @Override
                public void success(List<ParseObject> list) {
                    if (list.size() != 0) {
                        visibleView = messagesList;

                        for (int i = 0; i < list.size(); i++) {
                            String senderPhoneNumber = list.get(i).getString("senderPhoneNumber");
                            String textMessage = list.get(i).getString("textMessage");
                            Date sendTime = list.get(i).getUpdatedAt();
                            Message message = new Message(senderPhoneNumber, textMessage, sendTime);
                            messageArrayList.add(message);
                        }
                        int size = messageArrayList.size();
                        timeLastMessage = messageArrayList.get(size - 1).sendTime;

                        if (messagesList.getAdapter() == null) {
                            messagesListAdapter = new MessagesListAdapter();
                            messagesList.setAdapter(messagesListAdapter);
                        } else {
                            messagesListAdapter.notifyDataSetChanged();
                        }

                    } else {
                        visibleView = emptyList;
                    }
                    View[] views = new View[]{messagesList, progressMessages, emptyList};
                    setVisibilityViews(views, visibleView);
                }

                @Override
                public void e(String s) {
                    Log.d("LOG", "Error: " + s);
                }
            });
        } else {
            Account.loadNewMessages(dialogParseObjectsList, timeLastMessage, new Account.CallbackLoadObject() {
                @Override
                public void success(List<ParseObject> list) {
                    if (list.size() != 0) {

                        for (int i = 0; i < list.size(); i++) {
                            String senderPhoneNumber = list.get(i).getString("senderPhoneNumber");
                            String textMessage = list.get(i).getString("textMessage");
                            Date sendTime = list.get(i).getUpdatedAt();
                            Message message = new Message(senderPhoneNumber, textMessage, sendTime);
                            messageArrayList.add(message);
                        }
                        int size = messageArrayList.size();
                        timeLastMessage = messageArrayList.get(size - 1).sendTime;

                        messagesListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void e(String s) {
                    Log.d("LOG", "Error: " + s);
                }
            });
        }
    }

    private void createNewDialog() {

        Account.loadSelectedDialog(senderPhoneNumber, recipientPhoneNumber, new Account.CallbackLoadObject() {

            @Override
            public void success(List<ParseObject> list) {
                if (list.size() != 0) {
                    dialogParseObjectsList = list;
                    sendMessage();
                } else {
                    Account.createNewDialog(senderPhoneNumber, recipientPhoneNumber, new Account.Callback() {
                        @Override
                        public void success() {
                            Account.loadSelectedDialog(senderPhoneNumber, recipientPhoneNumber, new Account.CallbackLoadObject() {

                                @Override
                                public void success(List<ParseObject> list) {
                                    if (list.size() != 0) {
                                        dialogParseObjectsList = list;
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

        Account.sendMessage(dialogParseObjectsList, editTextMessage.getText().toString(), senderPhoneNumber, new Account.Callback() {
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

    private void setVisibilityViews(View[] views, View visibleView) {
        for (View v : views) {
            if (visibleView == v)
                v.setVisibility(View.VISIBLE);
            else {
                v.setVisibility(View.GONE);
            }
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
            return messageArrayList.size();
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

            String phoneNumber = messageArrayList.get(position).senderPhoneNumber;

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

            msgTextView.setText(messageArrayList.get(position).textMessage);
            msgTimeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageArrayList.get(position).sendTime));

            View[] views = {sendMessageBox, receiveMessageBox};
            setVisibilityViews(views, visibleView);

            return convertView;
        }
    }

    private class Message {
        String senderPhoneNumber;
        String textMessage;
        Date sendTime;

        Message(String senderPhoneNumber, String textMessage, Date sendTime) {
            this.senderPhoneNumber = senderPhoneNumber;
            this.textMessage = textMessage;
            this.sendTime = sendTime;
        }
    }
}
