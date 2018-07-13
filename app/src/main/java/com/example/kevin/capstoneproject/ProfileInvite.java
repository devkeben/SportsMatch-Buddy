package com.example.kevin.capstoneproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfileInvite extends AppCompatActivity {

    private String uid;
    private String userID, name, game;

    private TextView tv_level;
    private TextView tv_agility;
    private TextView tv_strength;
    private TextView tv_stamina;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRoot = mDatabase.getReference();
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_invite);

        userID = getIntent().getExtras().get("ID").toString();
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

        DatabaseReference getProfile = mRoot.child("My Sports").child(game).child(userID);

        getProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();

                tv_level.setText(map.get("Level"));
                tv_stamina.setText(map.get("Stamina"));
                tv_strength.setText(map.get("Strength"));
                tv_agility.setText(map.get("Agility"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
