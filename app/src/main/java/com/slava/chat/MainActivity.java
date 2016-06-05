package com.slava.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slava.chat.fragments.FragmentContacts;
import com.slava.chat.fragments.FragmentLogin;
import com.slava.chat.fragments.FragmentMain;
import com.slava.chat.fragments.FragmentMessages;
import com.slava.chat.fragments.FragmentRegistration;
import com.slava.chat.fragments.FragmentSetting;

public class MainActivity extends AppCompatActivity implements
        FragmentContacts.OnFragmentInteractionListener,
        FragmentLogin.OnFragmentInteractionListener,
        FragmentMain.OnFragmentInteractionListener,
        FragmentMessages.OnFragmentInteractionListener,
        FragmentSetting.OnFragmentInteractionListener,
        FragmentRegistration.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
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

        getPermissionReadContacts();

        //start service
        //startService(new Intent(this, MyService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop service
        stopService(new Intent(this, MyService.class));

        //Account.updateUserStatus(false);
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
            loadFragment(new FragmentContacts(), true, true);
        } else if (id == R.id.nav_profile) {
            loadFragment(new FragmentSetting(), true, true);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setTitleToolbar(String title, String subTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setSubtitle(subTitle);
        }

    }

    @Override
    public void setUserPhone(String s) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView userPhoneMenu = (TextView) headerView.findViewById(R.id.user_phone_number_menu);
            userPhoneMenu.setText(s);
        }
    }

    @Override
    public void setDrawerLockMode(int i) {
        drawer.setDrawerLockMode(i);
    }

    @Override
    public void loadFragment(Fragment fragment, boolean showActionBar, boolean addBackStack) {
        if (showActionBar && addBackStack) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
        } else if (showActionBar && !addBackStack) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        } else if (!showActionBar && addBackStack) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
        } else if (!showActionBar && !addBackStack) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }
    }

    private void getPermissionReadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            loadEntryFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadEntryFragment();
                } else {
                    getPermissionReadContacts();
                }
            }
        }
    }

    private void loadEntryFragment() {
        if (Account.getCurrentUser()) {
            loadFragment(new FragmentMain(), true, false);
        } else {
            loadFragment(new FragmentLogin(), false, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
