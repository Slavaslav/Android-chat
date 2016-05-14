package com.slava.chat.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.MyService;
import com.slava.chat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentMain extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final MessageReceiver mMessageReceiver = new MessageReceiver();
    private OnFragmentInteractionListener mListener;
    private DialogsListAdapter dialogsListAdapter;
    private ListView dialogsList;
    private List<ParseObject> messagesParseObjectsList;
    private ScrollView viewNoMessages;
    private boolean showNoMessageView = false;

    public FragmentMain() {
        // Required empty public constructor
    }

    public static FragmentMain newInstance(String param1, String param2) {
        FragmentMain fragment = new FragmentMain();
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        dialogsList = (ListView) view.findViewById(R.id.dialogs_list);
        viewNoMessages = (ScrollView) view.findViewById(R.id.no_messages);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //start service
        //getActivity().startService(new Intent(getActivity(), MyService.class).putExtra(MyService.INTENT_MESSAGE, MyService.UPDATE_DIALOGS_LIST));


        //set Toolbar title
        mListener.setTitleToolbar(getString(R.string.fragment_main));

        Account.findDialogs(new Account.CallbackLoadObject() {

            @Override
            public void success(final List<ParseObject> list) {
                if (list.size() != 0) {
                    messagesParseObjectsList = list;
                    dialogsListAdapter = new DialogsListAdapter();

                    dialogsList.setAdapter(dialogsListAdapter);
                    dialogsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final String senderPhoneNumber = list.get(position).getString("sender");
                            final String recipientPhoneNumber = list.get(position).getString("recipient");
                            String titleActionBar = Account.contactsDataMap.get(recipientPhoneNumber);

                            Bundle bundle = new Bundle();
                            bundle.putString("senderPhoneNumber", senderPhoneNumber);
                            bundle.putString("recipientPhoneNumber", recipientPhoneNumber);
                            bundle.putString("titleActionBar", titleActionBar);
                            Fragment fragmentMessages = new FragmentMessages();
                            fragmentMessages.setArguments(bundle);
                            mListener.loadFragment(fragmentMessages, true, true);


                        }
                    });

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        // Register to receive messages
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(MyService.UPDATE_DIALOGS_LIST));
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setDrawerLockMode(MainActivity.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }


    private void showNoMessageView() {
        if (viewNoMessages.getVisibility() == View.GONE && dialogsList.getVisibility() == View.VISIBLE) {
            dialogsList.setVisibility(View.GONE);
            viewNoMessages.setVisibility(View.VISIBLE);
            showNoMessageView = true;
        }
    }

    private void hideNoMessageView() {
        if (viewNoMessages.getVisibility() == View.VISIBLE && dialogsList.getVisibility() == View.GONE) {
            dialogsList.setVisibility(View.VISIBLE);
            viewNoMessages.setVisibility(View.GONE);
            showNoMessageView = false;
        }
    }


    public interface OnFragmentInteractionListener {
        void setTitleToolbar(String s);

        void setDrawerLockMode(int i);

        void loadFragment(Fragment fragment, boolean showActionBar, boolean addBackStack);
    }

    public class MessageReceiver extends BroadcastReceiver {
        // Handler for received Intents
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MyService.UPDATE_DIALOGS_LIST)) {
                final ArrayList<ParseObject> list = (ArrayList<ParseObject>) intent.getExtras().getSerializable(MyService.DIALOGS_LIST);
                //dialogsListAdapter.setDialogsList(list);
                dialogsList.setAdapter(dialogsListAdapter);
                dialogsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (list != null) {
                            mListener.setTitleToolbar(list.get(position).get("title").toString());

                            Bundle bundle = new Bundle();
                            bundle.putString("dialogId", list.get(position).getObjectId());
                            Fragment fragmentMessages = new FragmentMessages();
                            fragmentMessages.setArguments(bundle);

                            mListener.loadFragment(fragmentMessages, true, true);
                        }
                    }
                });
            }
        }
    }

    private class DialogsListAdapter extends BaseAdapter {

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        @Override
        public int getCount() {
            return messagesParseObjectsList.size();
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
                convertView = inflater.inflate(R.layout.dialog_item, parent, false);

            TextView titleDialog = (TextView) convertView.findViewById(R.id.dialog_title);
            TextView laseMessageDialog = (TextView) convertView.findViewById(R.id.dialog_message);
            TextView timeDialog = (TextView) convertView.findViewById(R.id.dialog_time);

            ParseObject messageObject = messagesParseObjectsList.get(position);

            String nameDialog = Account.contactsDataMap.get(messageObject.getString("recipient"));
            String lastMessage = messageObject.getString("lastMessage");

            titleDialog.setText(nameDialog);
            laseMessageDialog.setText(lastMessage);
            timeDialog.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageObject.getUpdatedAt()));

            return convertView;
        }
    }
}
