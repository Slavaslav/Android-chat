package com.slava.chat.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.slava.chat.MyService;
import com.slava.chat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentMain extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MessageReceiver mMessageReceiver = new MessageReceiver();
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private DialogsListAdapter dialogsAdapter;
    private ListView listDlg;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //start service
        getActivity().startService(new Intent(getActivity(), MyService.class).putExtra("message", "loadUserDialogs"));

        dialogsAdapter = new DialogsListAdapter();

        listDlg = (ListView) getView().findViewById(R.id.listDlg);

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

        // Register to receive messages
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(MyService.DIALOGS_LIST_UPDATED));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class MessageReceiver extends BroadcastReceiver {
        // Handler for received Intents
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MyService.DIALOGS_LIST_UPDATED)) {
                List<ParseObject> list = (List<ParseObject>) intent.getExtras().getSerializable(MyService.DIALOGS_LIST);
                ArrayList<ParseObject> dList = new ArrayList<>(list);
                dialogsAdapter.setDialogsList(dList);
                listDlg.setAdapter(dialogsAdapter);
            }
        }
    }

    private class DialogsListAdapter extends BaseAdapter {

        LayoutInflater inflater = getActivity().getLayoutInflater();
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

            ((TextView) convertView.findViewById(R.id.dlgTitle)).setText(list.get(position).get("title").toString());
            ((TextView) convertView.findViewById(R.id.dlgMessage)).setText(list.get(position).get("lMessage").toString());
            ((TextView) convertView.findViewById(R.id.dlgTime)).setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(list.get(position).getUpdatedAt()));

            return convertView;
        }
    }
}
