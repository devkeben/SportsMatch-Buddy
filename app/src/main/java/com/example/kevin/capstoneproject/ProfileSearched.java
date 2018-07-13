package com.example.kevin.capstoneproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileSearched extends AppCompatActivity {

    Context context = this;

    private String strRated, strRatedR;
    private float rated, ratedR;
    private float fResult;
    private String uid;
    private String userID, name, game;
    private String myName;

    private TextView tv_level;
    private TextView tv_agility;
    private TextView tv_strength;
    private TextView tv_stamina;
    private Button btn_invite;
    private Button btn_addfriend;
    private RatingBar rtbProductRating;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRoot = mDatabase.getReference();
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_searched);

        userID = getIntent().getExtras().get("UserID").toString();
        name = getIntent().getExtras().get("Name").toString();
        game = getIntent().getExtras().get("Game").toString();
        setTitle(name);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();

        tv_level = (TextView)findViewById(R.id.tv_level);
        tv_agility = (TextView)findViewById(R.id.tv_Agility);
        tv_strength = (TextView)findViewById(R.id.tv_Strength);
        tv_stamina = (TextView)findViewById(R.id.tv_Stamina);
        btn_invite = (Button)findViewById(R.id.btn_Invite);
        btn_addfriend = (Button)findViewById(R.id.btn_add);
        rtbProductRating = (RatingBar)findViewById(R.id.rtbProductRating);

        getStarRating(userID);



        DatabaseReference myProfile = mRoot.child("Users").child(uid);

        myProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();

                myName = map.get("Name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference getProfile = mRoot.child("My Sports").child(game).child(userID);

        getProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();

                tv_level.setText(map.get("Level"));
                tv_stamina.setText(map.get("Stamina"));
                tv_strength.setText(map.get("Strength"));
                tv_agility.setText(map.get("Agility"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
                myAlert.setMessage("Add " + name + " as friend?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference statusRequest = mRoot.child("Friend Requests");
                                DatabaseReference request = statusRequest.child(userID);
                                DatabaseReference statReqID = request.child(uid);
                                statReqID.child("From").setValue(uid);
                                statReqID.child("Name").setValue(myName);
                                statReqID.child("Status").setValue("Pending");

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = myAlert.create();
                alert.show();
            }
        });

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(context,InviteDetails.class);
                intent.putExtra("Game", game);
                intent.putExtra("Name",name);
                intent.putExtra("ID",userID);
                intent.putExtra("MyName", myName);
                startActivity(intent);
            }
        });


    }

    public void getStarRating (String id) {
        DatabaseReference user = mRoot.child("Users").child(id);

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
