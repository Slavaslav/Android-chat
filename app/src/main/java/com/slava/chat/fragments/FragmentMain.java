package com.slava.chat.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.MyService;
import com.slava.chat.R;
import com.slava.chat.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentMain extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final MessageReceiver mMessageReceiver = new MessageReceiver();
    Handler handler = new Handler();
    private FrameLayout progressDialogs;
    private View visibleView;
    private OnFragmentInteractionListener mListener;
    private DialogsListAdapter dialogsListAdapter;
    private ListView dialogsList;
    private List<ParseObject> dialogsParseObjectsList;
    private Date updatedAt;
    private ScrollView emptyList;
    Runnable loadDialogs = new Runnable() {
        @Override
        public void run() {
            getDialogsList();
        }
    };

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        dialogsList = (ListView) view.findViewById(R.id.dialogs_list);
        progressDialogs = (FrameLayout) view.findViewById(R.id.progress_dialogs);
        emptyList = (ScrollView) view.findViewById(R.id.empty_list);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.setTitleToolbar(getString(R.string.fragment_main), null);
        mListener.setUserPhone(ParseUser.getCurrentUser().getUsername());

        //start service
        //getActivity().startService(new Intent(getActivity(), MyService.class).putExtra(MyService.INTENT_MESSAGE, MyService.UPDATE_DIALOGS_LIST));
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
        handler.post(loadDialogs);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(loadDialogs);
    }

    private void getDialogsList() {
        Account.getDialogsList(new Account.CallbackLoadObject() {

            @Override
            public void success(final List<ParseObject> list) {

                if (list.size() != 0) {
                    visibleView = dialogsList;
                    dialogsParseObjectsList = list;

                    if (dialogsList.getAdapter() == null) {
                        updatedAt = list.get(list.size() - 1).getUpdatedAt();
                        dialogsListAdapter = new DialogsListAdapter();
                        dialogsList.setAdapter(dialogsListAdapter);
                        dialogsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                final String senderPhoneNumber = list.get(position).getString("sender");
                                final String recipientPhoneNumber = list.get(position).getString("recipient");
                                String titleActionBar = Account.contactsDataMap.get(recipientPhoneNumber);

                                FragmentMessages fragmentMessages = FragmentMessages.newInstance(senderPhoneNumber, recipientPhoneNumber, titleActionBar);
                                mListener.loadFragment(fragmentMessages, true, true);


                            }
                        });
                    } else {
                        Date newUpdatedAt = list.get(list.size() - 1).getUpdatedAt();
                        if (!updatedAt.equals(newUpdatedAt)) {
                            updatedAt = newUpdatedAt;
                            dialogsListAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    visibleView = emptyList;
                }
                View[] views = new View[]{dialogsList, progressDialogs, emptyList};
                for (View v : views) {
                    if (visibleView == v)
                        v.setVisibility(View.VISIBLE);
                    else {
                        v.setVisibility(View.GONE);
                    }
                }
                handler.postDelayed(loadDialogs, 1000);
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void setTitleToolbar(String title, String subTitle);

        void setUserPhone(String s);

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
                            mListener.setTitleToolbar(list.get(position).get("title").toString(), null);

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
            return dialogsParseObjectsList.size();
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
                convertView = inflater.inflate(R.layout.item_dialog, parent, false);

            TextView titleDialogView = (TextView) convertView.findViewById(R.id.dialog_title);
            TextView lastMessageView = (TextView) convertView.findViewById(R.id.dialog_message);
            TextView timeDialogView = (TextView) convertView.findViewById(R.id.dialog_time);
            TextView countUnreadView = (TextView) convertView.findViewById(R.id.count_unread);

            ParseObject messageObject = dialogsParseObjectsList.get(position);

            String titleDialog = Account.contactsDataMap.get(messageObject.getString("recipient"));
            if (titleDialog == null) {
                titleDialog = messageObject.getString("recipient");
            }
            String lastMessage = messageObject.getString("lastMessage");
            Date updatedAt = messageObject.getUpdatedAt();
            int countUnread = messageObject.getInt("countUnread");

            titleDialogView.setText(titleDialog);
            lastMessageView.setText(lastMessage);

            if (Utils.isToday(updatedAt)) {
                timeDialogView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(updatedAt));
            } else {
                timeDialogView.setText(new SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(updatedAt));
            }

            if (countUnread != 0) {
                countUnreadView.setVisibility(View.VISIBLE);
                countUnreadView.setText(String.valueOf(countUnread));
            } else {
                countUnreadView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }
}
