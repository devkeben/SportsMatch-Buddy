package com.example.kevin.capstoneproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

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

public class EditProfile extends AppCompatActivity {

    Context context = this;
    private String uid;
    private String valueS, valueA, valueSt;

    private Spinner sp_editlevel;
    private EditText et_editfname;
    private EditText et_editlname;
    private EditText et_editage;
    private SeekBar sb_editStrength;
    private SeekBar sb_editAgility;
    private SeekBar sb_editStamina;
    private RadioButton rbButton;
    private RadioButton rb_editmale, rb_editfemal;
    private RadioGroup rg_editgender;
    private Button btn_update;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        setTitle("Edit Profile");


        et_editfname=(EditText)findViewById(R.id.et_editfname);
        et_editlname=(EditText)findViewById(R.id.et_editlname);
        et_editage=(EditText)findViewById(R.id.et_editage);
        rg_editgender = (RadioGroup)findViewById(R.id.rg_editgender);
        rb_editfemal = (RadioButton)findViewById(R.id.rb_editfemale);
        rb_editmale = (RadioButton)findViewById(R.id.rb_editmale);
        btn_update =(Button)findViewById(R.id.btn_editUpdate);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        uid = currentUser.getUid();

        DatabaseReference mUsers = root.child("Users").child(uid);

        mUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> thismap = (Map)dataSnapshot.getValue();

                String gender = thismap.get("Gender");

                if (gender.equals("Male"))
                {
                    rb_editmale.setChecked(true);
                    rb_editfemal.setChecked(false);
                }
                else if (gender.equals("Female"))
                {
                    rb_editmale.setChecked(false);
                    rb_editfemal.setChecked(true);
                }

                et_editfname.setText(thismap.get("First Name"));
                et_editlname.setText(thismap.get("Last Name"));
                et_editage.setText(thismap.get("Age"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fname = et_editfname.getText().toString();
                String lname = et_editlname.getText().toString();
                String age = et_editage.getText().toString();

                if(TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(age))
                {
                    Toast.makeText(EditProfile.this, "Some fields are missing.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DBref();
                    Intent myProfile = new Intent(EditProfile.this,MainActivity.class);
                    startActivity(myProfile);
                }

            }
        });

    }

    private void DBref(){

        rg_editgender = (RadioGroup)findViewById(R.id.rg_editgender);
        int selectedRB = rg_editgender.getCheckedRadioButtonId();
        rbButton = (RadioButton)findViewById(selectedRB);

        String fname = et_editfname.getText().toString();
        String lname = et_editlname.getText().toString();
        String age = et_editage.getText().toString();
        String gender = String.valueOf(rbButton.getText());

        DatabaseReference users = root.child("Users");
        DatabaseReference childRefname = users.child(uid);
        childRefname.child("First Name").setValue(fname);
        childRefname.child("Last Name").setValue(lname);
        childRefname.child("Age").setValue(age);
        childRefname.child("Gender").setValue(gender);

    }
}
