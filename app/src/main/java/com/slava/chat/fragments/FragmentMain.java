package com.slava.chat.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.slava.chat.MainActivity;
import com.slava.chat.MyService;
import com.slava.chat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class FragmentMain extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final MessageReceiver mMessageReceiver = new MessageReceiver();
    private OnFragmentInteractionListener mListener;
    private DialogsListAdapter dialogsAdapter;
    private ListView dialogsList;

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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //start service
        getActivity().startService(new Intent(getActivity(), MyService.class).putExtra(MyService.INTENT_MESSAGE, MyService.DIALOGS_LIST_UPDATED));

        dialogsAdapter = new DialogsListAdapter();

        //set Toolbar title
        mListener.setTitleToolbar(getString(R.string.fragment_main));
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(MyService.DIALOGS_LIST_UPDATED));
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

    public interface OnFragmentInteractionListener {
        void setTitleToolbar(String s);

        void setDrawerLockMode(int i);

        void loadFragment(Fragment fragment, boolean showActionBar, boolean addBackStack);
    }

    public class MessageReceiver extends BroadcastReceiver {
        // Handler for received Intents
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MyService.DIALOGS_LIST_UPDATED)) {
                final ArrayList<ParseObject> list = (ArrayList<ParseObject>) intent.getExtras().getSerializable(MyService.DIALOGS_LIST);
                dialogsAdapter.setDialogsList(list);
                dialogsList.setAdapter(dialogsAdapter);
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

                       /*final String lastMessage = "wuzzup";

                        // add new message
                        ParseObject message = new ParseObject("message");
                        message.put("content", lastMessage);
                        message.put("parent", ParseObject.createWithoutData("dialog", list.get(position).getObjectId()));
                        message.put("senderID", ParseUser.getCurrentUser());
                        message.saveEventually();

                        // add last message to dialogs table
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("dialog");
                        query.getInBackground(list.get(position).getObjectId(), new GetCallback<ParseObject>() {
                            public void done(ParseObject dialog, ParseException e) {
                                if (e == null) {
                                    dialog.put("lMessage", lastMessage);
                                    dialog.saveEventually();
                                }
                            }
                        });*/

                    }
                });
            }
        }
    }

    private class DialogsListAdapter extends BaseAdapter {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        private ArrayList<ParseObject> list = null;

        public void setDialogsList(ArrayList<ParseObject> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
                convertView = inflater.inflate(R.layout.dialog_item, parent, false);

            ((TextView) convertView.findViewById(R.id.dialog_title)).setText(list.get(position).get("title").toString());
            ((TextView) convertView.findViewById(R.id.dialog_message)).setText(list.get(position).get("lMessage").toString());
            ((TextView) convertView.findViewById(R.id.dialog_time)).setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(list.get(position).getUpdatedAt()));

            return convertView;
        }
    }
}
