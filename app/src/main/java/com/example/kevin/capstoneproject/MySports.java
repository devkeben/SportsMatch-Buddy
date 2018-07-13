package com.example.kevin.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.kevin.capstoneproject.SetupProfile.AGILITY_WEIGHT;
import static com.example.kevin.capstoneproject.SetupProfile.LEVEL_WEIGHT;
import static com.example.kevin.capstoneproject.SetupProfile.RATING_WEIGHT;
import static com.example.kevin.capstoneproject.SetupProfile.STAMINA_WEIGHT;
import static com.example.kevin.capstoneproject.SetupProfile.STRENGTH_WEIGHT;

public class MySports extends AppCompatActivity {

    private String uid, fullname;
    private String strRated, strRatedR;
    private float rated, ratedR;
    private String []games = {"Basketball", "Badminton", "Tennis", "Soccer", "Volleyball", "Table Tennis", "Billiards"};
    private String []level = {"Beginner", "Intermediate", "Expert"};

    private String valueS, valueA, valueSt;
    private double agi, str, sta, lvlScore, dobRating;

    private TextView strstr, stasta, agiagi;
    private Button btn_save;
    private EditText et_others;
    private Spinner sp_setsports;
    private Spinner sp_setlevel;
    private SeekBar sb_strength, sb_agility, sb_stamina;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();

    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sports);
        setTitle("My Sports");

        btn_save = (Button)findViewById(R.id.btn_save);
        et_others = (EditText)findViewById(R.id.et_others);
        sp_setsports = (Spinner)findViewById(R.id.sp_setsport);
        sb_strength = (SeekBar)findViewById(R.id.sb_strength);
        sb_agility = (SeekBar)findViewById(R.id.sb_agility);
        sb_stamina = (SeekBar)findViewById(R.id.sb_stamina);
        sp_setlevel = (Spinner)findViewById(R.id.sp_setlevel);
        strstr = (TextView)findViewById(R.id.strstr);
        agiagi = (TextView)findViewById(R.id.agiagi);
        stasta = (TextView)findViewById(R.id.stasta);

        valueS = String.valueOf(sb_strength.getProgress());
        valueA = String.valueOf(sb_agility.getProgress());
        valueSt = String.valueOf(sb_stamina.getProgress());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MySports.this, android.R.layout.simple_spinner_item, games);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_setsports.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MySports.this, android.R.layout.simple_spinner_item, level );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_setlevel.setAdapter(adapter1);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();
        getStarRating(uid);
        myName();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                theSports();
            }
        });

        sb_strength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int min = 1;

                if (progress<min)
                {
                    sb_strength.setProgress(min);
                    strstr.setText("Strength - " + min);
                    valueS = String.valueOf(min);
                }
                else
                {
                    strstr.setText("Strength - " + progress);
                    valueS = String.valueOf(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sb_agility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int min = 1;

                if (progress<min)
                {
                    sb_agility.setProgress(min);
                    agiagi.setText("Agility - " + min);
                    valueA = String.valueOf(min);
                }
                else
                {
                    agiagi.setText("Agility - " + progress);
                    valueA = String.valueOf(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sb_stamina.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int min = 1;

                if (progress<min)
                {
                    sb_stamina.setProgress(min);
                    stasta.setText("Stamina - " + min);
                    valueSt = String.valueOf(min);
                }
                else
                {
                    stasta.setText("Stamina - " + progress);
                    valueSt = String.valueOf(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void theSports() {


        String games = String.valueOf(sp_setsports.getSelectedItem());
        String level = String.valueOf(sp_setlevel.getSelectedItem());
        String strength = valueS;
        String agility = valueA;
        String stamina = valueSt;

        switch(level)
        {
            case "Beginner":
                lvlScore = 0.50;
                break;
            case "Intermediate":
                lvlScore = 0.75;
                break;
            case "Expert":
                lvlScore = 1.00;
                break;

        }

        getAgi(agility);
        getStr(strength);
        getSta(stamina);


        rated = Float.parseFloat(strRated);
        ratedR = Float.parseFloat(strRatedR);

        double fResult = ratedR/rated;
        getUserRating(String.valueOf(fResult));

        double avgrating = ((agi*AGILITY_WEIGHT)+(lvlScore*LEVEL_WEIGHT)+(str*STRENGTH_WEIGHT)+(sta*STAMINA_WEIGHT)+(dobRating*RATING_WEIGHT));
        double finalRating = avgrating*100;
        String result = String.format("%.2f", finalRating);
        double theResult = Double.parseDouble(result)*-1;

        DatabaseReference sports = root.child("My Sports");
        DatabaseReference mySports = sports.child(games);
        DatabaseReference theSport = mySports.child(uid);
        theSport.child("Agility").setValue(agility);
        theSport.child("Strength").setValue(strength);
        theSport.child("Stamina").setValue(stamina);
        theSport.child("User ID").setValue(uid);
        theSport.child("Level").setValue(level);
        theSport.child("Rating").setValue(theResult);
        theSport.child("Name").setValue(fullname);

        Intent myProfile = new Intent(MySports.this,MainActivity.class);
        startActivity(myProfile);

    }

    public void getAgi (String thisAgi) {
        switch (thisAgi)
        {
            case "1":
                agi = 0.10;
                break;
            case "2":
                agi = 0.20;
                break;
            case "3":
                agi = 0.30;
                break;
            case "4":
                agi = 0.40;
                break;
            case "5":
                agi = 0.50;
                break;
            case "6":
                agi = 0.60;
                break;
            case "7":
                agi = 0.70;
                break;
            case "8":
                agi = 0.80;
                break;
            case "9":
                agi = 0.90;
                break;
            case "10":
                agi = 1.00;
                break;
        }

    }

    public void getStr (String thisStr) {
        switch (thisStr)
        {
            case "1":
                str = 0.10;
                break;
            case "2":
                str = 0.20;
                break;
            case "3":
                str = 0.30;
                break;
            case "4":
                str = 0.40;
                break;
            case "5":
                str = 0.50;
                break;
            case "6":
                str = 0.60;
                break;
            case "7":
                str = 0.70;
                break;
            case "8":
                str = 0.80;
                break;
            case "9":
                str = 0.90;
                break;
            case "10":
                str = 1.00;
                break;
        }

    }

    public void getSta (String thisSta) {
        switch (thisSta)
        {
            case "1":
                sta = 0.10;
                break;
            case "2":
                sta = 0.20;
                break;
            case "3":
                sta = 0.30;
                break;
            case "4":
                sta = 0.40;
                break;
            case "5":
                sta = 0.50;
                break;
            case "6":
                sta = 0.60;
                break;
            case "7":
                sta = 0.70;
                break;
            case "8":
                sta = 0.80;
                break;
            case "9":
                sta = 0.90;
                break;
            case "10":
                sta = 1.00;
                break;
        }

    }

    public void getUserRating (String thisRating){
        switch(thisRating)
        {
            case "0.5":
                dobRating = 0.10;
                break;
            case "1.0":
                dobRating = 0.20;
                break;
            case "1.5":
                dobRating = 0.30;
                break;
            case "2.0":
                dobRating = 0.40;
                break;
            case "2.5":
                dobRating = 0.50;
                break;
            case "3.0":
                dobRating = 0.60;
                break;
            case "3.5":
                dobRating = 0.70;
                break;
            case "4.0":
                dobRating = 0.80;
                break;
            case "4.5":
                dobRating = 0.90;
                break;
            case "5.0":
                dobRating = 1.0;
                break;

        }

    }

    public void getStarRating (String id) {
        DatabaseReference user = root.child("Users").child(id);

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map <String, String> map = (Map)dataSnapshot.getValue();
                strRated = map.get("Rated");
                strRatedR = map.get("Rating");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void myName () {
        DatabaseReference myProfile = root.child("Users").child(uid);

        myProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();

                fullname = map.get("Name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
