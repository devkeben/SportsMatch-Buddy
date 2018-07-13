package com.example.kevin.capstoneproject;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Games extends AppCompatActivity {

    Context context = this;

    private String uid;
    private int minAge, maxAge;
    private String gender, level, game;
    private String agility, strength, stamina;

    private ListView lv_games;
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> playerList = new ArrayList<>();
    private ArrayList<String> newIDlist = new ArrayList<>();
    private String[] array= new String[idList.size()];

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRoot = mDatabase.getReference();
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;

    private Map<String,String> myMap = new HashMap<>();
    private Map<String,String> otherMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        setTitle("Matched Players");

        lv_games = (ListView)findViewById(R.id.lv_games);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();

        final ArrayAdapter<String> arrayAdapter = new CustomListAdapter(Games.this,playerList);

        game = getIntent().getExtras().get("Game").toString();
        level = getIntent().getExtras().get("Level").toString();
        minAge = Integer.parseInt(getIntent().getExtras().get("MinAge").toString());
        maxAge = Integer.parseInt(getIntent().getExtras().get("MaxAge").toString());
        gender = getIntent().getExtras().get("Gender").toString();

        final DatabaseReference mUsers = mRoot.child("Users");

        mUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    otherMap.put(key,value);
                }

                if (Integer.parseInt(otherMap.get("Age"))>=minAge & Integer.parseInt(otherMap.get("Age"))<=maxAge)
                {
                    if(otherMap.get("Gender").equals(gender))
                    {
                        newIDlist.add(otherMap.get("ID"));
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


        DatabaseReference getUId = mRoot.child("My Sports").child(game);
        Query myQuery = getUId.orderByChild("Rating");
        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    myMap.put(key,value);
                }

                for (String id : newIDlist)
                {
                    if (id.equals(myMap.get("User ID")))
                    {
                        if (myMap.get("Level").equals(level) & !myMap.get("User ID").equals(uid))
                        {
                            idList.add(myMap.get("User ID"));
                            playerList.add(myMap.get("Name"));
                        }
                    }
                }


                lv_games.setAdapter(arrayAdapter);


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


        lv_games.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = String.valueOf(parent.getItemAtPosition(position));
                String idid = idList.get(position);
                final Intent intent = new Intent(context,ProfileSearched.class);
                intent.putExtra("UserID", idid);
                intent.putExtra("Name", name);
                intent.putExtra("Game", game);
                startActivity(intent);
            }
        });


    }
}
