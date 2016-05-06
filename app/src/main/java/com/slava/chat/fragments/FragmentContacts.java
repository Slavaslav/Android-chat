package com.slava.chat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;

import java.util.HashMap;
import java.util.List;

public class FragmentContacts extends Fragment implements
        AdapterView.OnItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private OnFragmentInteractionListener mListener;
    private ListView mContactsList;

    public FragmentContacts() {
        // Required empty public constructor
    }

    public static FragmentContacts newInstance(String param1, String param2) {
        FragmentContacts fragment = new FragmentContacts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsList = (ListView) view.findViewById(R.id.contacts_list);
        mContactsList.setOnItemClickListener(this);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            handleContactsList();
        }

        //set Toolbar title
        mListener.setTitleToolbar(getString(R.string.fragment_contacts));
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleContactsList();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setDrawerLockMode(MainActivity.LOCK_MODE_LOCKED_CLOSED);
    }

    private void handleContactsList() {

        Account.loadContactsList(new Account.CallbackLoadUser() {
            @Override
            public void success(List<ParseUser> list, HashMap<String, String> contactsDataMap) {
                ContactsAdapter contactsAdapter = new ContactsAdapter(list, contactsDataMap);
                mContactsList.setAdapter(contactsAdapter);
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void setTitleToolbar(String s);

        void setDrawerLockMode(int i);
    }

    private class ContactsAdapter extends BaseAdapter {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        List<ParseUser> list;
        HashMap<String, String> contactsDataMap;
        String phoneNumber;
        String name;

        public ContactsAdapter(List<ParseUser> list, HashMap<String, String> contactsDataMap) {
            this.list = list;
            this.contactsDataMap = contactsDataMap;
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

            if (convertView == null)
                convertView = inflater.inflate(R.layout.contacts_list_item, parent, false);

            phoneNumber = list.get(position).getUsername();
            name = contactsDataMap.get(phoneNumber);

            ((TextView) convertView.findViewById(R.id.contact_name)).setText(name);
            ((TextView) convertView.findViewById(R.id.contact_phone_number)).setText(phoneNumber);

            return convertView;
        }
    }
}
