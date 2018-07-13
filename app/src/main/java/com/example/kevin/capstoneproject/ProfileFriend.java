package com.example.kevin.capstoneproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFriend extends AppCompatActivity {

    private String strRated, strRatedR;
    private float rated, ratedR;
    private float fResult;
    private ImageView profile_image;
    private TextView tv_fullname;
    private TextView tv_age;
    private TextView tv_gender;
    private TextView tv_Agility;
    private TextView tv_Strength;
    private TextView tv_Stamina;
    private TextView tv_level;
    private Spinner sp_game;
    private RatingBar rtbProductRating;

    private String theSport;
    private ArrayList<String> theList = new ArrayList<>();

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference mRef = mDatabase.getReference().child("Users");
    private DatabaseReference mSport = root.child("My Sports");

    private Map<String, String> map = new HashMap<String, String>();

    private String idid, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friend);

        idid = getIntent().getExtras().get("UserID").toString();
        name = getIntent().getExtras().get("Name").toString();

        //getStarRating(idid);
        //float fResult = ratedR/rated;
        //rtbProductRating.setRating(fResult);

        profile_image=(ImageView)findViewById(R.id.profile_image);
        tv_fullname=(TextView)findViewById(R.id.tv_fullname);
        tv_age = (TextView)findViewById(R.id.tv_Age);
        tv_gender = (TextView)findViewById(R.id.tv_gender);
        tv_Agility=(TextView)findViewById(R.id.tv_Agility);
        tv_Strength=(TextView)findViewById(R.id.tv_Strength);
        tv_Stamina=(TextView)findViewById(R.id.tv_Stamina);
        tv_level=(TextView)findViewById(R.id.tv_level);
        sp_game=(Spinner)findViewById(R.id.sp_game);
        rtbProductRating = (RatingBar)findViewById(R.id.rtbProductRating);


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileFriend.this, android.R.layout.simple_spinner_item, theList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.clear();
        sp_game.setAdapter(adapter);

        tv_fullname.setText(name);

        DatabaseReference profRef = mRef.child(idid);

        profRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();

                if(map!=null)
                {
                    String fname = map.get("First Name");
                    String lname = map.get("Last Name");
                    String age = map.get("Age");
                    String gender = map.get("Gender");
                    String fullName = fname + " " + lname;

                    tv_gender.setText(gender);
                    tv_age.setText(age);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSport.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    if (child.getKey().equals(idid))
                    {
                        theList.add(dataSnapshot.getKey());
                        adapter.notifyDataSetChanged();

                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sp_game.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                theSport = String.valueOf(parent.getItemAtPosition(position));
                //theSport = String.valueOf(parent.getSelectedItem());

                DatabaseReference mySport = mSport.child(theSport);
                DatabaseReference userSport = mySport.child(idid);

                userSport.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, String> map = (Map)dataSnapshot.getValue();

                            String level = map.get("Level");
                            String agility = map.get("Agility");
                            String stamina = map.get("Stamina");
                            String strength = map.get("Strength");

                            tv_Agility.setText(agility);
                            tv_Stamina.setText(stamina);
                            tv_Strength.setText(strength);
                            tv_level.setText(level);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    public void getStarRating (String id) {
        DatabaseReference user = root.child("Users").child(id);

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map <String, String> map = (Map)dataSnapshot.getValue();
                strRated = map.get("Rated");
                strRatedR = map.get("Rating");


                rated = Float.parseFloat(strRated);
                ratedR = Float.parseFloat(strRatedR);


                fResult = ratedR/rated;

                rtbProductRating.setRating(fResult);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
