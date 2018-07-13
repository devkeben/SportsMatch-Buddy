package com.example.kevin.capstoneproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TabGames extends android.support.v4.app.Fragment {

    private String uid, myName;
    private String strRated, strRatedR;
    private float rated, ratedR;
    private float result;
    private String selectedID, selectedName, selectedDate;
    private String day;
    private String month; private String years; private String theDate;

    private ArrayList<String> invitelist = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> dateList = new ArrayList<>();
    private Map<String, String> map = new HashMap<String, String>();
    private ListView lv_games;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.tabgames, container, false);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();
        getMyName();

        lv_games = (ListView)rootView.findViewById(R.id.lv_games);

        final ArrayAdapter<String> arrayAdapter = new CustomListAdapter(getActivity(),invitelist);
        arrayAdapter.clear();
        lv_games.setAdapter(arrayAdapter);

        Calendar today = Calendar.getInstance();


        day = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(today.get(Calendar.MONTH)+1);
        years = String.valueOf(today.get(Calendar.YEAR));
        theDate = month+"/"+day+"/"+years;

        DatabaseReference mRef = root.child("Games").child(uid);
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    map.put(key,value);
                }

                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
                    try {
                        Date date1 = formatter.parse(theDate);
                        Date date2 = formatter.parse(map.get("Date"));
                        if (date1.compareTo(date2)<1)
                        {
                            idList.add(map.get("ID"));
                            nameList.add(map.get("Name"));
                            dateList.add(map.get("Date"));
                            invitelist.add(map.get("Name")+"\n"+map.get("Game") +" "+ map.get("Date"));
                            arrayAdapter.notifyDataSetChanged();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
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

        lv_games.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedID = idList.get(position);
                selectedName = nameList.get(position);
                selectedDate = dateList.get(position);

                DatabaseReference user = root.child("Users").child(selectedID);

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

                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date1 = formatter.parse(theDate);
                    Date date2 = formatter.parse(selectedDate);
                    if(!date1.equals(date2))
                    {
                        Toast.makeText(getContext(), "Cannot rate player until "+ selectedDate, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showDialog();

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        return rootView;
    }

    public void showDialog ()
    {
        android.app.AlertDialog.Builder myAlert = new android.app.AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rating,null);
        RatingBar rating = (RatingBar)view.findViewById(R.id.rtbProductRating);
        rating.setStepSize((float) 0.5);

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar,float v, boolean b) {
                result = v;
            }
        });

        myAlert.setView(view);
        myAlert.setTitle("Rate Player");
        myAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                DatabaseReference ratings = root.child("Ratings");
                DatabaseReference mRatings = ratings.child(selectedID);
                DatabaseReference mRateDetails = mRatings.child(uid);
                mRateDetails.child("Value").setValue(result);
                mRateDetails.child("ID").setValue(uid);
                mRateDetails.child("Name").setValue(myName);

                DatabaseReference user = root.child("Users").child(selectedID);
                rated = Float.parseFloat(strRated);
                ratedR = Float.parseFloat(strRatedR);

                user.child("Rated").setValue(String.valueOf(rated+1));
                user.child("Rating").setValue(String.valueOf(ratedR+result));

                DatabaseReference mGames = root.child("Games").child(uid);
                mGames.child(selectedID).removeValue();

                dialog.dismiss();
            }
        });



        android.app.AlertDialog alert = myAlert.create();
        alert.show();
    }

    public void getMyName() {
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
