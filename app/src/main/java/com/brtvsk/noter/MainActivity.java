package com.brtvsk.noter;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String[] titles;
    private ListView drawerList;
    private Fragment fragment;
    private ActionBarDrawerToggle drawerToggle;

    private NotesListFragment noteListFragment;
    private SettingsFragment settingsFragment;

    private DrawerLayout drawerLayout;

    public static final String NOTES_LIST_TAG = "notesListTag";
    public static final String SETTINGS_TAG = "settingsTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        manageFragmentTransaction(NOTES_LIST_TAG);

        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                fragment.setHasOptionsMenu(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                fragment.setHasOptionsMenu(false);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);


        PreferenceManager
                .setDefaultValues(this, R.xml.preference_layout, false);

        SharedPreferences sharedPref =
                PreferenceManager
                        .getDefaultSharedPreferences(this);

        String username = sharedPref.getString
                (SettingsFragment.KEY_PREF_USERNAME, "ERROR");

        Toast.makeText(this, "Hello " + username + " !",
                Toast.LENGTH_LONG).show();

        drawerLayout = findViewById(R.id.drawer_layout);

        setActionBarTitle(0);
//Закрытие выдвижной панели.
        drawerLayout.closeDrawer(drawerList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setActionBarTitle(int position) {
        String[] titles = getResources().getStringArray(R.array.titles);
        getSupportActionBar().setTitle(titles[position]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.closeDrawer(drawerList);
            setActionBarTitle(position);
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                noteListFragment.updateUI();
                manageFragmentTransaction(NOTES_LIST_TAG);
                return;
            case 1:
                manageFragmentTransaction(SETTINGS_TAG);
                return;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void manageFragmentTransaction(String selectedFrag) {
        switch (selectedFrag) {
            case NOTES_LIST_TAG: {
                if (getSupportFragmentManager().findFragmentByTag(NOTES_LIST_TAG) != null) {
                    //if the fragment exists, show it.
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag(NOTES_LIST_TAG)).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    noteListFragment = new NotesListFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, noteListFragment, NOTES_LIST_TAG).commit();
                }
                if (getSupportFragmentManager().findFragmentByTag(SETTINGS_TAG) != null) {
                    //if the other fragment is visible, hide it.
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(SETTINGS_TAG)).commit();
                }
                fragment = noteListFragment;
                return;
            }
            case SETTINGS_TAG: {
                if (getSupportFragmentManager().findFragmentByTag(SETTINGS_TAG) != null) {
                    //if the fragment exists, show it.
                    getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag(SETTINGS_TAG)).commit();
                } else {
                    //if the fragment does not exist, add it to fragment manager.
                    settingsFragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, settingsFragment, SETTINGS_TAG).commit();
                }
                if (getSupportFragmentManager().findFragmentByTag(NOTES_LIST_TAG) != null) {
                    //if the other fragment is visible, hide it.
                    getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(NOTES_LIST_TAG)).commit();
                }
                fragment = settingsFragment;
                return;
            }
        }
    }

}
