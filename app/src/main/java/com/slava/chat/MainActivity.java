package com.slava.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;
import com.slava.chat.fragments.FragmentContacts;
import com.slava.chat.fragments.FragmentLogin;
import com.slava.chat.fragments.FragmentMain;
import com.slava.chat.fragments.FragmentMessages;
import com.slava.chat.fragments.FragmentProfile;
import com.slava.chat.fragments.FragmentRegistration;

public class MainActivity extends AppCompatActivity implements
        FragmentContacts.OnFragmentInteractionListener,
        FragmentLogin.OnFragmentInteractionListener,
        FragmentMain.OnFragmentInteractionListener,
        FragmentMessages.OnFragmentInteractionListener,
        FragmentProfile.OnFragmentInteractionListener,
        FragmentRegistration.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    public static final int FRAGMENT_MAIN = 0;
    public static final int FRAGMENT_MESSAGES = 1;
    public static final int FRAGMENT_LOGIN = 4;
    public static final int FRAGMENT_REG = 5;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private final int FRAGMENT_CONTACTS = 2;
    private final int FRAGMENT_PROFILE = 3;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        //when press button HomeAsUp
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Listener BackStackChanged
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    toggle.setDrawerIndicatorEnabled(false);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }
                } else {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.setDrawerIndicatorEnabled(true);
                    }

                    // unselect all selected item
                    if (navigationView != null) {
                        Menu menu = navigationView.getMenu();
                        for (int i = 0; i < menu.size(); i++) {
                            MenuItem item = menu.getItem(i);
                            item.setChecked(false);
                        }

                    }
                }
            }
        });

        Parse.initialize(this, "78vxcrQI4qOuwsNDMOWNovUqGOaGNREHGGMSChUL", "jXJXeTKSURpgqijsqkfAhgGQkDJbwxMNgEFusFwE");

        if (Account.getCurrentUser()) {
            Account.updateUserStatus(true);
            loadingFragment(FRAGMENT_MAIN);
        } else {
            loadingFragment(FRAGMENT_LOGIN);
        }

        //start service
        startService(new Intent(this, MyService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop service
        stopService(new Intent(this, MyService.class));

        // Update user status (set false)
        Account.updateUserStatus(false);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            loadingFragment(FRAGMENT_CONTACTS);
        } else if (id == R.id.nav_profile) {
            loadingFragment(FRAGMENT_PROFILE);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadingFragment(int i) {
        switch (i) {
            case FRAGMENT_MAIN: {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentMain()).commit();
                break;
            }
            case FRAGMENT_MESSAGES: {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentMessages()).addToBackStack(null).commit();
                break;
            }
            case FRAGMENT_CONTACTS: {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentContacts()).addToBackStack(null).commit();
                break;
            }
            case FRAGMENT_PROFILE: {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentProfile()).addToBackStack(null).commit();
                break;
            }
            case FRAGMENT_LOGIN: {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentLogin()).commit();
                break;
            }
            case FRAGMENT_REG: {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new FragmentRegistration()).addToBackStack(null).commit();
                break;
            }
        }
    }

    // implement method from fragment
    public void onFragmentInteraction(Uri uri) {

    }

    // implement method from fragment
    @Override
    public void setTitleToolbar(String s) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(s);
        }

    }

    @Override
    public void setDrawerLockMode(int i) {
        drawer.setDrawerLockMode(i);
    }
}
