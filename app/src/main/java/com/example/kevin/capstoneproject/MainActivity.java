package com.example.kevin.capstoneproject;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchPlayer = (SearchView)item.getActionView();
        searchPlayer.setQueryHint("Search Player");
        searchPlayer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent myList = new Intent(MainActivity.this,SearchPlayer.class);
                myList.putExtra("EXTRA_SESSION_ID", query);
                startActivity(myList);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_preference)
        {
            Intent mySports = new Intent(MainActivity.this,MySports.class);
            startActivity(mySports);
        }
        else if (id == R.id.action_logout)
        {
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();
            Intent myLogin = new Intent(MainActivity.this,Login.class);
            startActivity(myLogin);
        }
        else if (id == R.id.action_FriendRequests)
        {
            Intent myRequest = new Intent(MainActivity.this,FriendRequests.class);
            startActivity(myRequest);
        }
        else if (id == R.id.action_edit)
        {
            Intent myEdit = new Intent(MainActivity.this,EditProfile.class);
            startActivity(myEdit);
        }
        else if (id == R.id.action_invites)
        {
            Intent myInvites = new Intent(MainActivity.this,InviteRequest.class);
            startActivity(myInvites);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 1:
                    TabSearch TabSearch = new TabSearch();
                    return TabSearch;
                case 0:
                    TabProfile TabProfile = new TabProfile();
                    return TabProfile;
                case 2:
                    TabFriends TabFriends = new TabFriends();
                    return TabFriends;
                case 3:
                    TabGames TabGames = new TabGames();
                    return TabGames;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return "SEARCH GAME";
                case 0:
                    return "PROFILE";
                case 2:
                    return "FRIENDS";
                case 3:
                    return "GAMES";
            }
            return null;
        }
    }

}
