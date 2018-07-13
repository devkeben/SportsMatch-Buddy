package com.example.kevin.capstoneproject;

/**
 * Created by Kevin on 1/3/2017.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabFriends extends Fragment {

    private String uid;
    private String selectedName;
    private String selectedID;
    private String nameOfuser;

    private ListView lv_friends;
    private ArrayList<String> friendList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();

    public Map<String, String> map = new HashMap<String, String>();
    private Map<String, String> myMap = new HashMap<String, String>();

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();
    private String roomID;

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabfriends, container, false);

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

        lv_friends = (ListView)rootView.findViewById(R.id.lv_friends);


        final ArrayAdapter<String> arrayAdapter = new CustomListAdapter(getActivity(),friendList);
        arrayAdapter.clear();
        lv_friends.setAdapter(arrayAdapter);

        DatabaseReference mRef = mDatabase.getReference().child("User Friends").child(uid);
        final Query friendQuery = mRef.orderByChild("Name");

        lv_friends.clearChoices();
        //map.clear();

        friendQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //map.clear();
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    map.put(key,value);
                }

                idList.add(map.get("ID"));
                friendList.add(map.get("Name"));
                arrayAdapter.notifyDataSetChanged();
                    //map.clear();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    map.put(key,value);
                }

                idList.add(map.get("ID"));
                friendList.add(map.get("Name"));
                arrayAdapter.notifyDataSetChanged();

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

        lv_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                selectedName = String.valueOf(parent.getItemAtPosition(position));
                selectedID = idList.get(position);
                final CharSequence [] options = {"Chat with " + selectedName, "View Profile", "Delete Friend"};



                AlertDialog.Builder myAlert = new AlertDialog.Builder(getContext());
                myAlert.setTitle("Options");
                myAlert.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case 0:
                                final Intent intent = new Intent(getContext(),Inbox.class);
                                DatabaseReference mRef = mDatabase.getReference().child("Conversation");
                                Query myQ = mRef.orderByChild("User1");

                                myQ.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                        for (DataSnapshot child: dataSnapshot.getChildren())
                                        {
                                            String key = child.getKey().toString();
                                            String value = child.getValue().toString();
                                            myMap.put(key,value);
                                        }

                                        if ((myMap.get("User1").equals(uid) & myMap.get("User2").equals(selectedID)) || (myMap.get("User1").equals(selectedID) & myMap.get("User2").equals(uid)) )
                                        {
                                            roomID = myMap.get("Key");
                                            intent.putExtra("RoomID",roomID);
                                            intent.putExtra("FriendName",selectedName );
                                            intent.putExtra("FriendID", selectedID);
                                            intent.putExtra("UserID", uid);
                                            intent.putExtra("UserName",nameOfuser);
                                            startActivity(intent);
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
                                break;
                            case 1:
                                Intent intent1 = new Intent(getContext(),ProfileFriend.class);
                                intent1.putExtra("UserID", selectedID);
                                intent1.putExtra("Name", selectedName);
                                startActivity(intent1);

                                break;
                            case 2:
                                final AlertDialog.Builder thisAlert = new AlertDialog.Builder(getContext());
                                thisAlert.setMessage("Are you sure you want to delete "+selectedName+ "?");
                                thisAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        DatabaseReference friend = root.child("User Friends");
                                        DatabaseReference myFriend = friend.child(uid);
                                        myFriend.child(selectedID).removeValue();

                                        DatabaseReference thisFriend = friend.child(selectedID);
                                        thisFriend.child(uid).removeValue();

                                        final DatabaseReference mRef = root.child("Conversation");

                                        mRef.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                for (DataSnapshot child: dataSnapshot.getChildren())
                                                {
                                                    String key = child.getKey().toString();
                                                    String value = child.getValue().toString();
                                                    myMap.put(key,value);
                                                }

                                                if ((myMap.get("User1").equals(uid) & myMap.get("User2").equals(selectedID)) || (myMap.get("User1").equals(selectedID) & myMap.get("User2").equals(uid)) )
                                                {
                                                    mRef.child(dataSnapshot.getKey()).removeValue();
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

                                        Toast.makeText(getContext(), "Successfully deleted friend.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                thisAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alerta = thisAlert.create();
                                alerta.show();

                                break;
                        }

                    }
                });

                AlertDialog alert = myAlert.create();
                alert.show();


            }
        });

        return rootView;
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