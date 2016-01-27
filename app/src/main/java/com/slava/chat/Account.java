package com.slava.chat;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Home on 27.01.2016.
 */
public class Account {
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

    public void logIn(String login, String password) {

        ParseUser.logInInBackground(login, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });

    }

    public void logOut() {
        ParseUser.logOut();
    }
}
