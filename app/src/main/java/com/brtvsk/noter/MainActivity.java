package com.brtvsk.noter;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    private Fragment fragment = null;
    private ActionBarDrawerToggle drawerToggle;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        FragmentManager fm = getSupportFragmentManager();

        fragment = new NotesListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        /*
        fragment = new NotesListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
*/
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.println(Log.ERROR,"DESTROYED","DESTROUED ????");
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
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                fragment = new NotesListFragment();
                break;
            case 1:
                fragment = new SettingsFragment();
                break;
        }
        /*
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.
                fragment_container, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();*/
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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

}
