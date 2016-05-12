package com.slava.chat;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Account {
    public static void logIn(String login, String password, final Callback callBack) {

        ParseUser.logInInBackground(login, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    callBack.success();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    callBack.e(e.getMessage());
                }
            }
        });

    }

    public static void signUp(String login, String password, final Callback callBack) {
        ParseUser user = new ParseUser();
        user.setUsername(login);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    callBack.success();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    callBack.e(e.getMessage());
                }
            }
        });
    }

    public static boolean getCurrentUser() {
        return ParseUser.getCurrentUser() != null;
    }

    public static void updateUserStatus(boolean b) {

        if (getCurrentUser()) {
            ParseUser user = ParseUser.getCurrentUser();
            user.put("online", b);
            user.saveEventually();
        }
    }

    public static void loadUserDialogs(final CallbackLoadObject callBack) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("dialog");
        query.whereEqualTo("parent", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    callBack.success(list);
                } else {
                    callBack.e(e.getMessage());
                }
            }
        });
    }

    public static void loadMessageList(String dialogId, final CallbackLoadObject callBack) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("message");
        query.whereEqualTo("parent", dialogId);
        // query.whereEqualTo("sender", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    callBack.success(list);
                } else {
                    callBack.e(e.getMessage());
                }
            }
        });

    }

    public static void loadContactsList(final CallbackLoadUser callBack) {

        final HashMap<String, String> contactsDataMap = new HashMap<>();
        Cursor cursor = App.applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = phoneNumber.replaceAll(" ", "");
                contactsDataMap.put(phoneNumber, name);

            }
            cursor.close();
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("username", contactsDataMap.keySet());
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    callBack.success(list, contactsDataMap);
                } else {
                    callBack.e(e.getMessage());
                }
            }
        });
    }

    public static void logOut() {
        ParseUser.logOut();
    }

    public static void loadSelectedDialog(final String senderPhoneNumber, final String receiverPhoneNumber, final CallbackLoadObject callback) {

        String[] phones = {senderPhoneNumber, receiverPhoneNumber};

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Dialogs");
        query.whereContainedIn("user1", Arrays.asList(phones));
        query.whereContainedIn("user2", Arrays.asList(phones));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    callback.success(list);
                } else {
                    callback.e(e.getMessage());
                }
            }
        });
    }

    public static void createNewDialog(final String senderPhoneNumber, final String receiverPhoneNumber, final Callback callback) {
        ParseObject dialog = new ParseObject("Dialogs");
        dialog.put("user1", senderPhoneNumber);
        dialog.put("user2", receiverPhoneNumber);
        dialog.put("lastMessage", "");
        dialog.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.success();
                } else {
                    callback.e(e.getMessage());
                }
            }
        });

    }

    public static void loadMessages(ParseObject dialogObject, final CallbackLoadObject callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.whereEqualTo("parent", dialogObject);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    callback.success(list);
                } else {
                    callback.e(e.getMessage());
                }
            }
        });

    }

    public static void sendMessage(final ParseObject dialogObject, final String textMessage, String senderPhoneNumber, String receiverPhoneNumber, final Callback callback) {
        ParseObject messages = new ParseObject("Messages");
        messages.put("parent", ParseObject.createWithoutData("Dialogs", dialogObject.getObjectId()));
        messages.put("textMessage", textMessage);
        messages.put("senderPhoneNumber", senderPhoneNumber);
        messages.put("receiverPhoneNumber", receiverPhoneNumber);
        messages.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // update a row 'lastMessage' in the dialogue table
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Dialogs");
                    query.getInBackground(dialogObject.getObjectId(), new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object.put("lastMessage", textMessage);
                                object.saveInBackground();
                            }
                        }
                    });
                    callback.success();

                } else {
                    callback.e(e.getMessage());

                }

            }
        });


    }


    public interface Callback {
        void success();

        void e(String s);
    }

    public interface CallbackLoadObject {
        void success(List<ParseObject> list);

        void e(String s);
    }

    public interface CallbackLoadUser {
        void success(List<ParseUser> list, HashMap<String, String> contactsDataMap);

        void e(String s);
    }
}

