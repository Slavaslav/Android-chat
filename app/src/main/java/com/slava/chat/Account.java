package com.slava.chat;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class Account {
    public static void logIn(String login, String password, final MainActivity.MyCallback callBack) {

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

    public static void signUp(String login, String password, final MainActivity.MyCallback callBack) {
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
        if (ParseUser.getCurrentUser() != null)
            return true;
        return false;
    }

    public static void updateUserStatus(boolean b) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("online", b);
        user.saveEventually();
    }

    public static void loadUserDialogs(final MainActivity.MyCallback callBack) {

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

    public void logOut() {
        ParseUser.logOut();
    }
}
