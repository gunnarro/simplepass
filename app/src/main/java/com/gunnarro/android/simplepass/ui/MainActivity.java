package com.gunnarro.android.simplepass.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.simplepass.R;
import com.gunnarro.android.simplepass.ui.fragment.AdminFragment;
import com.gunnarro.android.simplepass.ui.fragment.CredentialAddFragment;
import com.gunnarro.android.simplepass.ui.fragment.CredentialListFragment;
import com.gunnarro.android.simplepass.ui.fragment.PreferencesFragment;
import com.gunnarro.android.simplepass.ui.login.LoginActivity;
import com.gunnarro.android.simplepass.utility.AESCrypto;
import com.gunnarro.android.simplepass.utility.Utility;

import java.io.File;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Inject
    final CredentialListFragment credentialStoreListFragment;
    @Inject
    final CredentialAddFragment credentialAddFragment;
    @Inject
    final PreferencesFragment preferencesFragment;
    @Inject
    final AdminFragment adminFragment;

    private DrawerLayout drawer;
    private Long loggedInUserId = null;

    public MainActivity() {
        this.credentialStoreListFragment = new CredentialListFragment();
        this.credentialAddFragment = new CredentialAddFragment();
        this.preferencesFragment = new PreferencesFragment();
        this.adminFragment = new AdminFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggedInUserId = getIntent().getExtras().getLong(LoginActivity.LOGGED_IN_USER_ID_INTENT_KEY);

        Log.i(Utility.buildTag(getClass(), "onCreate"), "Credential store for userId=" + loggedInUserId);

        // Adding this line will prevent taking screenshot in your app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (!new File(getApplicationContext().getFilesDir().getPath()).exists()) {
            Log.d(Utility.buildTag(getClass(), "onCreate"), "app file dir missing! " + getApplicationContext().getFilesDir().getPath());
        }

        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            Log.e(Utility.buildTag(getClass(), "onCreate"), "Failed starting! " + e.getMessage());
        }
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.title_credential_add, R.string.title_credential_add);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        // display home button for actionbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // navigation view select credential list menu as default
        navigationView.setCheckedItem(R.id.nav_credential_list);

        if (savedInstanceState == null) {
            viewFragment(credentialStoreListFragment);
        }
        // check and grant or deny permissions
        checkPermissions();
        // Finally, start timer for automatically logout user after å given period of time
        startAutoLogoutUserTimer(600000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(Utility.buildTag(getClass(), "onOptionsItemSelected"), "selected: " + item);
        if (item.getItemId() == android.R.id.home) {// Open Close Drawer Layout
            if (drawer.isOpen()) {
                drawer.closeDrawers();
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        try {
            Log.d("MainActivity.onNavigationItemSelected", "selected: " + menuItem.getItemId());
            Bundle args = new Bundle();
            args.putLong(LoginActivity.LOGGED_IN_USER_ID_INTENT_KEY, loggedInUserId);
            int id = menuItem.getItemId();
            if (id == R.id.nav_settings) {
                ((NavigationView) findViewById(R.id.navigationView)).setCheckedItem(R.id.nav_settings);
                viewFragment(preferencesFragment);
            } else if (id == R.id.nav_credential_list) {
                ((NavigationView) findViewById(R.id.navigationView)).setCheckedItem(R.id.nav_credential_list);
                credentialStoreListFragment.setArguments(args);
                viewFragment(credentialStoreListFragment);
            } else if (id == R.id.nav_admin) {
                ((NavigationView) findViewById(R.id.navigationView)).setCheckedItem(R.id.nav_admin);
                viewFragment(adminFragment);
            } else if (id == R.id.nav_logout) {
                // always clear the key
                AESCrypto.reset();
                startAutoLogoutUserTimer(1);
            }
            // close drawer after clicking the menu item
            DrawerLayout tmpDrawer = findViewById(R.id.drawer_layout);
            tmpDrawer.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception e) {
            Log.e(Utility.buildTag(getClass(), "onNavigationItemSelected"), e.getMessage());
            return false;
        }
    }

    private void viewFragment(@NonNull Fragment fragment) {
        Log.d(Utility.buildTag(getClass(), "viewFragment"), "fragment: " + fragment.getTag());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment) // remove current fragment and add the new one
                .commit();
    }

    private void checkPermissions() {
        Log.i(Utility.buildTag(getClass(), "checkPermissions"), "Start check permissions...no permission needed");
    }


    /**
     * logout user automatically after a given period of time.
     * redirect back to login page after timeout
     */
    private void startAutoLogoutUserTimer(int timeMs) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                AESCrypto.reset();
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        }, timeMs);
    }
}