package com.example.funtimer.palmtree;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BaseProfileActivity extends AppCompatActivity {
    public static final String TAG = "BaseProfileActivity";
    private AdView mdAdView;
    private InterstitialAd mInterstitialAd;

    public class HomeUser
    {
        private String name;
        private String dob;
        private String gender;

        public HomeUser() {}

        public HomeUser(String name, String dob, String gender)
        {
            this.name = name;
            this.dob = dob;
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public String getDob() {
            return dob;
        }

        public String getgender() {
            return gender;
        }
    };

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //MainActivity.super.onBackPressed();
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                }).create().show();
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupBannerAd()
    {
        mdAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mdAdView.loadAd(adRequest);

        mdAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(getApplicationContext(), "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(getApplicationContext(), "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Toast.makeText(getApplicationContext(), "onAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Toast.makeText(getApplicationContext(), "onAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(getApplicationContext(), "onAdClosed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupInterstitialAd()
    {
        mInterstitialAd = new InterstitialAd(this);

        //TODO:: This loads sample apps only - use  "ca-app-pub-6556406589111577/7820424164" for our ads
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.setAdUnitId("ca-app-pub-6556406589111577/7820424164");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(getApplicationContext(), "Int onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(getApplicationContext(), "Int onAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Toast.makeText(getApplicationContext(), "Int onAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Toast.makeText(getApplicationContext(), "Int onAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(getApplicationContext(), "Int onAdClosed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profile);

        SharedPreferences prefs = getSharedPreferences(MainActivity.PREF_FILENAME, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            int idName = prefs.getInt("idName", 0); //0 is the default value.
        }


        MobileAds.initialize(this, "ca-app-pub-6556406589111577~9907531453");
        setupBannerAd();
        setupInterstitialAd();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                else {
                Snackbar.make(view, "Sorry ad not loaded yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
            }
        });

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userid = "uniqueid1";
        DatabaseReference myRef = database.getReference("userDB/"+userid);

        FirebaseAuth.getInstance();

        //myRef.setValue("Hello, World!");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //HomeUser value = messageSnapshot.getValue(HomeUser.class);
                DataSnapshot messageSnapshot = dataSnapshot;

                TextView nameTextView = (TextView) findViewById(R.id.temp_user_name);
                TextView dobTextView = (TextView) findViewById(R.id.temp_user_dob);
                TextView genderTextView = (TextView) findViewById(R.id.temp_user_gender);

                Toast.makeText(getApplicationContext(), "DB Read value is : " + messageSnapshot.getValue(), Toast.LENGTH_SHORT).show();

                nameTextView.setText((String)messageSnapshot.child("name").getValue());
                dobTextView.setText((String)messageSnapshot.child("dob").getValue());
                genderTextView.setText((String)messageSnapshot.child("gender").getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                Toast.makeText(getApplicationContext(), "Action search" , Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_location_found:
                // location found
                movetoTestingActivity();
                Toast.makeText(getApplicationContext(), "location found", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:
                // refresh
                Toast.makeText(getApplicationContext(), "sdfbs", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_help:
                // help action
                Toast.makeText(getApplicationContext(), "sxfbsdb", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_check_updates:
                // check for updates action
                Toast.makeText(getApplicationContext(), "asdgasg", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launching new activity
     * */
    private void movetoTestingActivity() {
        //Intent i = new Intent(TestingTempActivity1.this, LocationFound.class);
        Intent intent = new Intent(this, TestingTempActivity1.class);
        startActivity(intent);
    }

}
