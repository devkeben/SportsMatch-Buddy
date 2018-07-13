package com.example.kevin.capstoneproject;

/**
 * Created by Kevin on 1/3/2017.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class TabProfile extends Fragment {

    private String []games = {"Basketball", "Badminton", "Tennis"};
    private String theSport;
    private int counter = 0;
    private float finalRate;
    private ArrayList<String> theList = new ArrayList<>();
    private ArrayList<String> rateList = new ArrayList<>();
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
    private RatingBar profRating;

    private String uid;

    public Map<String, String> map = new HashMap<String, String>();
    private Map<String, String> ratemap = new HashMap<String, String>();

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference mRef = mDatabase.getReference().child("Users");
    private DatabaseReference mSport = root.child("My Sports");

    private FirebaseAuth userAuth;
    private FirebaseUser currentUser;

    private ProgressDialog myProgress;
    private static final int GALLERY_INTENT = 2;
    private Uri photoURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tabprofile, container, false);

        profile_image=(ImageView)rootView.findViewById(R.id.profile_image);
        tv_fullname=(TextView)rootView.findViewById(R.id.tv_fullname);
        tv_age = (TextView)rootView.findViewById(R.id.tv_Age);
        tv_gender = (TextView)rootView.findViewById(R.id.tv_gender);
        tv_Agility=(TextView)rootView.findViewById(R.id.tv_Agility);
        tv_Strength=(TextView)rootView.findViewById(R.id.tv_Strength);
        tv_Stamina=(TextView)rootView.findViewById(R.id.tv_Stamina);
        tv_level=(TextView)rootView.findViewById(R.id.tv_level);
        sp_game=(Spinner)rootView.findViewById(R.id.sp_game);
        profRating = (RatingBar)rootView.findViewById(R.id.profRating);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, theList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.clear();
        sp_game.setAdapter(adapter);

        myProgress = new ProgressDialog(getContext());

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();

        photoURL = currentUser.getPhotoUrl();

        Picasso.with(getContext()).load(photoURL).fit().centerCrop().into(profile_image);
        //Glide.with(getContext()).using(new FirebaseImageLoader()).load(filepath).into(profile_image);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_PICK);
                //intent.setType("image/*");
                //startActivityForResult(intent,GALLERY_INTENT);
            }
        });


        mSport.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    if (child.getKey().equals(uid))
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

        DatabaseReference profRef = mRef.child(uid);

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
                    String rated = map.get("Rated");
                    String rating = map.get("Rating");


                    tv_fullname.setText(fullName);
                    tv_gender.setText(gender);
                    tv_age.setText(age);


                }
                else
                {
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(getContext());
                    myAlert.setMessage("Please setup your profile.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent mySetup = new Intent(getActivity(),SetupProfile.class);
                                    startActivity(mySetup);
                                }
                            })
                            .setCancelable(false);
                    AlertDialog alert = myAlert.create();
                    alert.show();
                }

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
                DatabaseReference userSport = mySport.child(uid);

                userSport.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, String> map = (Map)dataSnapshot.getValue();

                        if (map==null)
                        {
                            tv_Agility.setText("Not set");
                            tv_Stamina.setText("Not set");
                            tv_Strength.setText("Not set");
                            tv_level.setText("Not set");
                        }
                        else
                        {
                            String level = map.get("Level");
                            String agility = map.get("Agility");
                            String stamina = map.get("Stamina");
                            String strength = map.get("Strength");

                            tv_Agility.setText(agility);
                            tv_Stamina.setText(stamina);
                            tv_Strength.setText(strength);
                            tv_level.setText(level);
                        }
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

        DatabaseReference rating = root.child("Ratings").child(uid);

        rating.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();
                    String value = child.getValue().toString();
                    ratemap.put(key,value);
                }

                rateList.add(ratemap.get("Value"));
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


        //getStarRating(uid);
        /*DatabaseReference user = root.child("Users").child(uid);

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map <String, String> map = (Map)dataSnapshot.getValue();

                if (map!=null)
                {
                    strRated = map.get("Rated");
                    strRatedR = map.get("Rating");

                    rated = Float.parseFloat(strRated);
                    ratedR = Float.parseFloat(strRatedR);

                    fResult = ratedR/rated;

                    profRating.setRating(fResult);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        return rootView;

    }

    public void getStarRating (String id) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            myProgress.setMessage("Uploading.");
            myProgress.show();
            Uri uri = data.getData();

            //StorageReference filepath = storage.child();
            StorageReference filepath = storage.getReferenceFromUrl("gs://sportsmatch-buddy.appspot.com");
            StorageReference userFolder = filepath.child(uid);

            userFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    myProgress.dismiss();

                    String url = String.valueOf(taskSnapshot.getDownloadUrl());

                    //Uri mine = taskSnapshot.getDownloadUrl();

                    Picasso.with(getContext()).load(url).fit().centerCrop().into(profile_image);
                }
            });
        }

    }

}