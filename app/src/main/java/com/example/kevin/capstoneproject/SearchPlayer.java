package com.example.kevin.capstoneproject;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchPlayer extends AppCompatActivity {

    private String uid;
    private String uidF;
    private String userName;
    private Boolean found=true;

    private ListView lv_search;
    private ArrayList<String> playerList = new ArrayList<>();
    private ArrayList<String> userList = new ArrayList<>();

    public Map<String, String> map = new HashMap<String, String>();
    public Map<String, String> friendmap = new HashMap<String, String>();

    final Context context = this;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = mDatabase.getReference().child("Users");
    private DatabaseReference root = mDatabase.getReference();

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);

        setTitle("Players Found");

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        authenticateUser();

        lv_search = (ListView)findViewById(R.id.lv_players);

        uid = currentUser.getUid();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,playerList);

        lv_search.setAdapter(arrayAdapter);

        final String query = getIntent().getStringExtra("EXTRA_SESSION_ID");
        final String name = query.toLowerCase();

        final Query userQuery = mRef.orderByChild("First Name");

        userQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                map.clear();
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey().toString();
                    String value = child.getValue().toString();
                    map.put(key,value);
                }

                if(map.get("ID").equals(uid))
                {
                    userName = map.get("First Name") + " " + map.get("Last Name");
                }

                if ((map.get("Last Name").toLowerCase()).equals(name) || (map.get("First Name").toLowerCase()).equals(name))
                {
                    uidF = map.get("ID");
                    String fname = map.get("First Name");
                    String lname = map.get("Last Name");

                    if (!uidF.equals(uid))
                    {
                        playerList.add(fname + " " + lname);
                        userList.add(uidF);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        userName = fname + " " + lname;

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

        if(!found)
        {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
            myAlert.setMessage("No profile found!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = myAlert.create();
            alert.show();
        }

        lv_search.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                        //map.clear();

                        final String player = String.valueOf(parent.getItemAtPosition(position));
                        final String friendID = userList.get(position);

                        final DatabaseReference mRef = mDatabase.getReference().child("User Friends").child(uid);

                        AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
                        myAlert.setMessage("Add " + player + " as friend?")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        DatabaseReference statusRequest = root.child("Friend Requests");
                                        DatabaseReference request = statusRequest.child(friendID);
                                        DatabaseReference statReqID = request.child(uid);
                                        statReqID.child("From").setValue(uid);
                                        statReqID.child("Name").setValue(userName);
                                        statReqID.child("Status").setValue("Pending");

                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = myAlert.create();
                        alert.show();

                    }
                }
        );
    }

    public void authenticateUser(){
        userAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                }
            }
        };
    }


}
