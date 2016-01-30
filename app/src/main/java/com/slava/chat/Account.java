package com.slava.chat;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.slava.chat.fragments.FragmentLogin;

public class Account {
    public static void logIn(String login, String password, final FragmentLogin.MyCallback callBack) {

        ParseUser.logInInBackground(login, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    callBack.loggedIn();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    callBack.e(e.getMessage());
                }
            }
        });

    }

    public void signUp(String login, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(login);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    public boolean getCurrentUser() {
        if (ParseUser.getCurrentUser() != null)
            return true;
        return false;
    }

    public void logOut() {
        ParseUser.logOut();
    }
}
