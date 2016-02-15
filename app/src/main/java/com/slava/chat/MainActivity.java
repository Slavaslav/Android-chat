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

    private FragmentContacts fcontacts;
    private FragmentLogin flogin;
    private FragmentMain fmain;
    private FragmentMessages fmessages;
    private FragmentProfile fprofile;
    private FragmentRegistration freg;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this, "78vxcrQI4qOuwsNDMOWNovUqGOaGNREHGGMSChUL", "jXJXeTKSURpgqijsqkfAhgGQkDJbwxMNgEFusFwE");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fcontacts = new FragmentContacts();
        flogin = new FragmentLogin();
        fmain = new FragmentMain();
        fmessages = new FragmentMessages();
        fprofile = new FragmentProfile();
        freg = new FragmentRegistration();

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
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.setDrawerIndicatorEnabled(true);

                    // uncheck all selected item
                    Menu menu = navigationView.getMenu();
                    for (int i = 0; i < menu.size(); i++) {
                        MenuItem item = menu.getItem(i);
                        item.setChecked(false);
                    }

                }
            }
        });

        if (Account.getCurrentUser()) {
            Account.updateUserStatus(true);
            loadingFragment("fragmentMain");
        } else {
            loadingFragment("fragmentLogin");
        }

        //start service
        startService(new Intent(this, MyService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop service
        stopService(new Intent(this, MyService.class));

        // Update user status (set fasle)
        Account.updateUserStatus(false);
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            loadingFragment("fragmentContacts");
        } else if (id == R.id.nav_profile) {
            loadingFragment("fragmentProfile");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadingFragment(String s) {
        switch (s) {
            case "fragmentMain": {
                getSupportActionBar().show();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fmain).commit();
                break;
            }
            case "fragmentMessages": {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fmessages).addToBackStack(null).commit();
                break;
            }
            case "fragmentContacts": {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fcontacts).addToBackStack(null).commit();
                break;
            }
            case "fragmentProfile": {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fprofile).addToBackStack(null).commit();
                break;
            }
            case "fragmentLogin": {
                getSupportActionBar().hide();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, flogin).commit();
                break;
            }
            case "fragmentReg": {
                getSupportActionBar().hide();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, freg).addToBackStack(null).commit();
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
        getSupportActionBar().setTitle(s);
    }
}
