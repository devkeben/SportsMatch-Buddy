package com.example.kevin.capstoneproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InviteRequest extends AppCompatActivity {

    private ListView lv_invites;
    private String uid, myName;
    private String selectedID, selectedName, selectedGame, selectedDate, selectedVenue;

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;

    Context context = this;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();

    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> gameList = new ArrayList<>();
    private ArrayList<String> dateList = new ArrayList<>();
    private ArrayList<String> venueList = new ArrayList<>();
    private ArrayList<String> requestList = new ArrayList<>();
    public Map<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_request);

        lv_invites = (ListView)findViewById(R.id.lv_invites);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();
        getmyName();

        DatabaseReference mRef = root.child("Invites").child(uid);
        final ArrayAdapter<String> arrayAdapter = new CustomListAdapter(this,requestList);
        lv_invites.setAdapter(arrayAdapter);

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
                    nameList.add(map.get("Name"));
                    gameList.add(map.get("Game"));
                    dateList.add(map.get("Date"));
                    venueList.add(map.get("Venue"));
                    requestList.add(map.get("Name")+"\n"+map.get("Game")+" - "+map.get("Date"));
                    //status.add(map.get("Status"));
                    arrayAdapter.notifyDataSetChanged();
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


        lv_invites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedID = idList.get(position);
                selectedName = nameList.get(position);
                selectedGame = gameList.get(position);
                selectedDate = dateList.get(position);
                selectedVenue = venueList.get(position);
                final CharSequence [] options = {"Accept Invite", "View Profile"};

                AlertDialog.Builder myAlert = new AlertDialog.Builder(context);
                myAlert.setTitle("Options")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which)
                                {
                                    case 0:
                                        //Toast.makeText(context, selectedID, Toast.LENGTH_SHORT).show();
                                        DatabaseReference mRef = root.child("Invites").child(uid).child(selectedID);
                                        mRef.child("Status").setValue("Accepted");

                                        DatabaseReference mRefs = root.child("Games").child(uid);
                                        DatabaseReference with = mRefs.child(selectedID);
                                        with.child("Name").setValue(selectedName);
                                        with.child("ID").setValue(selectedID);
                                        with.child("Date").setValue(selectedDate);
                                        with.child("Venue").setValue(selectedVenue);
                                        with.child("Game").setValue(selectedGame);

                                        DatabaseReference mRefss = root.child("Games").child(selectedID);
                                        DatabaseReference withh = mRefss.child(uid);
                                        withh.child("Name").setValue(myName);
                                        withh.child("ID").setValue(uid);
                                        withh.child("Date").setValue(selectedDate);
                                        withh.child("Venue").setValue(selectedVenue);
                                        withh.child("Game").setValue(selectedGame);

                                        break;
                                    case 1:
                                        final Intent intent = new Intent(context,ProfileInvite.class);
                                        intent.putExtra("Game", selectedGame);
                                        intent.putExtra("Name",selectedName);
                                        intent.putExtra("ID",selectedID);
                                        startActivity(intent);
                                        break;
                                }

                            }
                        });

                AlertDialog alert = myAlert.create();
                alert.show();

            }
        });



    }

    private void getmyName()
    {
        DatabaseReference myProfile = root.child("Users").child(uid);

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
    }
}
