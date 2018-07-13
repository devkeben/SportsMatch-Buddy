package com.example.kevin.capstoneproject;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class InviteDetails extends AppCompatActivity {

    private String uid;
    private String day, month, years, theDate;
    private String userID, name, game, myname;

    private DatePicker date;
    private Button btn_btninvite;
    private EditText et_venue;

    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRoot = mDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_details);

        userID = getIntent().getExtras().get("ID").toString();
        name = getIntent().getExtras().get("Name").toString();
        game = getIntent().getExtras().get("Game").toString();
        myname = getIntent().getExtras().get("MyName").toString();
        setTitle(name);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();

        date = (DatePicker)findViewById(R.id.datePicker);
        btn_btninvite = (Button)findViewById(R.id.btn_searchinvite);
        et_venue = (EditText) findViewById(R.id.et_venue);

        Calendar today = Calendar.getInstance();

        day = String.valueOf(today.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(today.get(Calendar.MONTH)+1);
        years = String.valueOf(today.get(Calendar.YEAR));
        theDate = month+"/"+day+"/"+years;

        date.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                day = String.valueOf(dayOfMonth);
                month = String.valueOf(monthOfYear+1);
                years = String.valueOf(year);
                theDate = month+"/"+day+"/"+years;

            }
        });

        btn_btninvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference invites = mRoot.child("Invites");
                DatabaseReference myInvites = invites.child(userID);
                DatabaseReference user = myInvites.child(uid);
                user.child("From").setValue(uid);
                user.child("Name").setValue(myname);
                user.child("Date").setValue(theDate);
                user.child("Venue").setValue(et_venue.getText().toString());
                user.child("Status").setValue("Pending");
                user.child("Game").setValue(game);

                Toast.makeText(InviteDetails.this, "Invite sent!", Toast.LENGTH_SHORT).show();

            }
        });




    }
}
