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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.slava.chat.Account;
import com.slava.chat.MainActivity;
import com.slava.chat.R;

import java.util.HashMap;
import java.util.List;

public class FragmentContacts extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private FrameLayout progressContacts;
    private ScrollView emptyList;
    private View visibleView;
    private OnFragmentInteractionListener mListener;
    private ListView contactsList;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactsList = (ListView) view.findViewById(R.id.contacts_list);
        progressContacts = (FrameLayout) view.findViewById(R.id.progress_contacts);
        emptyList = (ScrollView) view.findViewById(R.id.empty_list);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.setTitleToolbar(getString(R.string.fragment_contacts), null);

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            handleContactsList();
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
    public void onResume() {
        super.onResume();
        mListener.setDrawerLockMode(MainActivity.LOCK_MODE_LOCKED_CLOSED);
    }

    private void handleContactsList() {

        Account.loadContactsList(new Account.CallbackLoadUser() {
            @Override
            public void success(final List<ParseUser> list) {
                if (list.size() != 0) {
                    visibleView = contactsList;
                    ContactsAdapter contactsAdapter = new ContactsAdapter(list, Account.contactsDataMap);
                    contactsList.setAdapter(contactsAdapter);
                    contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            final String senderPhoneNumber = ParseUser.getCurrentUser().getUsername();
                            final String recipientPhoneNumber = list.get(position).getUsername();
                            String titleActionBar = Account.contactsDataMap.get(recipientPhoneNumber);

                            FragmentMessages fragmentMessages = FragmentMessages.newInstance(senderPhoneNumber, recipientPhoneNumber, titleActionBar);
                            mListener.loadFragment(fragmentMessages, true, true);
                        }
                    });
                } else {
                    visibleView = emptyList;
                }
                View[] views = new View[]{contactsList, progressContacts, emptyList};
                for (View v : views) {
                    if (visibleView == v)
                        v.setVisibility(View.VISIBLE);
                    else {
                        v.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void e(String s) {
                Log.d("LOG", "Error: " + s);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void setTitleToolbar(String title, String subTitle);

        void setDrawerLockMode(int i);

        void loadFragment(Fragment fragment, boolean showActionBar, boolean addBackStack);
    }

    private class ContactsAdapter extends BaseAdapter {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final List<ParseUser> list;
        final HashMap<String, String> contactsDataMap;
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
