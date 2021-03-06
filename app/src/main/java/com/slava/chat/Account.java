package com.slava.chat;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Account {
    public static final HashMap<String, String> contactsDataMap = new HashMap<>();

    public static void logIn(String login, String password, final Callback callBack) {

        ParseUser.logInInBackground(login, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    callBack.success();
                } else {
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
                    callBack.success();
                } else {
                    callBack.e(e.getMessage());
                }
            }
        });
    }

    public static boolean getCurrentUser() {
        return ParseUser.getCurrentUser() != null;
    }

    public static void updateUserStatus(boolean b) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("online", b);
        user.saveEventually();
    }

    public static void logOut(final Callback callback) {
        ParseUser.logOutInBackground(new LogOutCallback() {
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

    public static void getContactsList(final CallbackLoadUser callBack) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("username", contactsDataMap.keySet());
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    callBack.success(list);
                } else {
                    callBack.e(e.getMessage());
                }
            }
        });
    }

    public static void loadSelectedDialog(final String senderPhoneNumber, final String recipientPhoneNumber, final CallbackLoadObject callback) {

        String[] phones = {senderPhoneNumber, recipientPhoneNumber};

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Dialogs");
        query.whereContainedIn("sender", Arrays.asList(phones));
        query.whereContainedIn("recipient", Arrays.asList(phones));
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

    public static void createNewDialog(final String senderPhoneNumber, final String recipientPhoneNumber, final Callback callback) {

        List<ParseObject> parseObjects = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ParseObject dialog = new ParseObject("Dialogs");
            if (i == 0) {
                dialog.put("sender", senderPhoneNumber);
                dialog.put("recipient", recipientPhoneNumber);
            } else {
                dialog.put("sender", recipientPhoneNumber);
                dialog.put("recipient", senderPhoneNumber);
            }
            dialog.put("countUnread", 0);
            parseObjects.add(dialog);
        }

        ParseObject.saveAllInBackground(parseObjects, new SaveCallback() {
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

    public static void loadAllMessages(List<ParseObject> dialogParseObjectsList, final CallbackLoadObject callback) {
        for (int i = 0; i < dialogParseObjectsList.size(); i++) {
            ParseObject dialogObject = dialogParseObjectsList.get(i);
            if (dialogObject.get("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
                query.whereEqualTo("parent", dialogObject);
                query.setLimit(30);
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
                break;
            }
        }
    }

    public static void loadNewMessages(List<ParseObject> dialogParseObjectsList, Date timeLastMessage, final CallbackLoadObject callback) {
        for (int i = 0; i < dialogParseObjectsList.size(); i++) {
            ParseObject dialogObject = dialogParseObjectsList.get(i);
            if (dialogObject.get("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
                query.whereEqualTo("parent", dialogObject);
                query.whereGreaterThan("createdAt", timeLastMessage);
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
                break;
            }
        }
    }

    public static void sendMessage(final List<ParseObject> dialogParseObjectsList, final String textMessage, String senderPhoneNumber, final Callback callback) {

        List<ParseObject> parseObjects = new ArrayList<>();
        for (int i = 0; i < dialogParseObjectsList.size(); i++) {

            ParseObject dialogObject = dialogParseObjectsList.get(i);

            ParseObject messages = new ParseObject("Messages");
            messages.put("parent", ParseObject.createWithoutData("Dialogs", dialogObject.getObjectId()));
            messages.put("textMessage", textMessage);
            messages.put("senderPhoneNumber", senderPhoneNumber);
            messages.put("isRead", false);
            parseObjects.add(messages);

            // update table 'Dialogs'
            if (!(dialogObject.get("sender").equals(ParseUser.getCurrentUser().getUsername()))) {
                dialogObject.increment("countUnread");
            }
            dialogObject.put("lastMessage", textMessage);
            parseObjects.add(dialogObject);
        }

        ParseObject.saveAllInBackground(parseObjects, new SaveCallback() {
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

    public static void getDialogsList(final CallbackLoadObject callback) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Dialogs");
        query.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query.orderByDescending("updatedAt");
        query.setLimit(20);
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

    public static void readMessages(final List<ParseObject> dialogParseObjectsList, ArrayList<String> unReadMessagesId, final Callback callback) {

        final List<ParseObject> parseObjects = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.whereContainedIn("objectId", unReadMessagesId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).put("isRead", true);
                        parseObjects.add(list.get(i));
                    }

                    for (int i = 0; i < dialogParseObjectsList.size(); i++) {
                        if (dialogParseObjectsList.get(i).get("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                            dialogParseObjectsList.get(i).put("countUnread", 0);
                            parseObjects.add(dialogParseObjectsList.get(i));
                            break;
                        }
                    }

                    ParseObject.saveAllInBackground(parseObjects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                callback.success();
                            } else {
                                callback.e(e.getMessage());
                            }
                        }
                    });

                } else {
                    callback.e(e.getMessage());
                }
            }
        });
    }

    public static void getAllPhoneContacts() {
        contactsDataMap.clear();
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
        void success(List<ParseUser> list);

        void e(String s);
    }
}