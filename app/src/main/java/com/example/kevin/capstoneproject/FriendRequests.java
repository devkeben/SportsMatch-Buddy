package com.example.kevin.capstoneproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequests extends AppCompatActivity {

    private ListView lv_friendrequest;
    private ArrayList<String> requestList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();

    private String uid;
    private String selectedName;
    private String selectedID;
    private String nameOfuser;

    Context context = this;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();

    public Map<String, String> map = new HashMap<String, String>();

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        setTitle("Friend Requests");

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        authenticateUser();

        uid = currentUser.getUid();

        DatabaseReference userName = mDatabase.getReference().child("Users").child(uid);

        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> myMap = (Map<String, Object>)dataSnapshot.getValue();
                String fname = String.valueOf(myMap.get("First Name"));
                String lname = String.valueOf(myMap.get("Last Name"));
                nameOfuser = fname + " " + lname;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lv_friendrequest = (ListView)findViewById(R.id.lv_friendRequest);

        DatabaseReference mRef = mDatabase.getReference().child("Friend Requests").child(uid);
        final ArrayAdapter<String> arrayAdapter = new CustomListAdapter(this,requestList);
        lv_friendrequest.setAdapter(arrayAdapter);


        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    map.put(key,value);
                }

                if (map.get("Status").equals("Pending"))
                {
                    idList.add(map.get("From"));
                    requestList.add(map.get("Name"));
                    //status.add(map.get("Status"));
                    arrayAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    map.put(key,value);
                }

                if (map.get("Status").equals("Pending"))
                {
                    idList.add(map.get("From"));
                    requestList.add(map.get("Name"));
                    //status.add(map.get("Status"));
                    arrayAdapter.notifyDataSetChanged();
                }

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

        lv_friendrequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedName = String.valueOf(parent.getItemAtPosition(position));
                selectedID = idList.get(position);
                //String currentStat = status.get(position);

                AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
                myAlert.setMessage("Accept " + selectedName + "'s friend request?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //creates the database for friend requests
                                DatabaseReference FQ = root.child("Friend Requests");
                                DatabaseReference myRequest = FQ.child(uid);
                                DatabaseReference requestFrom = myRequest.child(selectedID);
                                requestFrom.child("Status").setValue("Accepted");

                                //creates the database for friends
                                DatabaseReference friend = root.child("User Friends");
                                DatabaseReference childFriend = friend.child(uid);
                                DatabaseReference detailFriend = childFriend.child(selectedID);
                                detailFriend.child("Name").setValue(selectedName);
                                detailFriend.child("ID").setValue(selectedID);

                                DatabaseReference uidFfriend = friend.child(selectedID);
                                DatabaseReference uidFdetail = uidFfriend.child(uid);
                                uidFdetail.child("Name").setValue(nameOfuser);
                                uidFdetail.child("ID").setValue(uid);

                                //database for chat
                                final DatabaseReference convo = root.child("Conversation");
                                String convoKey = convo.push().getKey();
                                final DatabaseReference keyConvo = convo.child(convoKey);
                                keyConvo.child("Key").setValue(convoKey);
                                keyConvo.child("User1").setValue(uid);
                                keyConvo.child("User2").setValue(selectedID);

                            }
                        })
                        .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DatabaseReference FQ = root.child("Friend Requests");
                                DatabaseReference myRequest = FQ.child(uid);
                                DatabaseReference requestFrom = myRequest.child(selectedID);
                                requestFrom.child("Status").setValue("Rejected");

                            }
                        });

                AlertDialog alert = myAlert.create();
                alert.show();



            }
        });



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
