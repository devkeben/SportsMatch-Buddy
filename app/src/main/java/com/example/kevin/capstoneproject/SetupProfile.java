package com.example.kevin.capstoneproject;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupProfile extends AppCompatActivity {

    private String []level = {"Beginner", "Intermediate", "Expert"};
    private String []games = {"Choose a sport...","Basketball", "Badminton", "Tennis", "Soccer", "Volleyball", "Table Tennis", "Billiards", "Others"};

    public static final double AGILITY_WEIGHT = 0.10;
    public static final double LEVEL_WEIGHT = 0.20;
    public static final double STRENGTH_WEIGHT = 0.10;
    public static final double STAMINA_WEIGHT = 0.10;
    public static final double RATING_WEIGHT = 0.50;

    private EditText et_fname, et_lname, et_age;
    private RadioButton rbButton;
    private RadioGroup rg_gender;
    private Spinner sp_setLevel;
    private Spinner sp_setSport;
    private TextView strstr, stasta, agiagi;
    private EditText et_others;

    private SeekBar sb_strength, sb_agility, sb_stamina;
    private Button btn_finish;

    private String valueS, valueA, valueSt;
    private double agi, str, sta, lvlScore;
    private String uid;
    private String game;

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth userAuth;
    private FirebaseUser userFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        setTitle("Setup Profile");

        userAuth = FirebaseAuth.getInstance();
        userFirebase = userAuth.getCurrentUser();

        et_fname = (EditText)findViewById(R.id.et_fname);
        et_lname = (EditText)findViewById(R.id.et_lname);
        et_age = (EditText)findViewById(R.id.et_age);
        strstr = (TextView)findViewById(R.id.str);
        et_others = (EditText)findViewById(R.id.et_others);
        agiagi = (TextView)findViewById(R.id.agi);
        stasta = (TextView)findViewById(R.id.sta);

        sb_strength = (SeekBar)findViewById(R.id.sb_strength);
        sb_agility = (SeekBar)findViewById(R.id.sb_agility);
        sb_stamina = (SeekBar)findViewById(R.id.sb_stamina);
        sp_setLevel = (Spinner)findViewById(R.id.sp_setlevel);
        sp_setSport = (Spinner)findViewById(R.id.sp_setsport);

        btn_finish = (Button)findViewById(R.id.btn_finish);

        valueS = String.valueOf(sb_strength.getProgress());
        valueA = String.valueOf(sb_agility.getProgress());
        valueSt = String.valueOf(sb_stamina.getProgress());

        et_others.setEnabled(false);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, games ) {
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_setSport.setAdapter(adapter1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetupProfile.this, android.R.layout.simple_spinner_item, level );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_setLevel.setAdapter(adapter);


        if(userFirebase != null){
            uid = userFirebase.getUid();
            if(userFirebase.getDisplayName()!=null)
            {
                String fullname = userFirebase.getDisplayName();
                int index = fullname.lastIndexOf(' ');
                String lastName = fullname.substring(index + 1);
                String firstName = fullname.substring(0, index);
                et_fname.setText(firstName);
                et_lname.setText(lastName);

            }

        }

        sp_setSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==games.length-1)
                {
                    et_others.setEnabled(true);
                    et_others.setHint("Please specify...");
                    game = String.valueOf(et_others.getText().toString());

                }
                else
                {
                    game = String.valueOf(parent.getSelectedItem());
                    et_others.setEnabled(false);
                    et_others.setHint("Others");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //button listener
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){

                String fname = et_fname.getText().toString();
                String lname = et_lname.getText().toString();
                String age = et_age.getText().toString();
                String other = et_others.getText().toString();

                try
                {
                    Integer.parseInt(age);
                }
                catch(NumberFormatException e)
                {
                    Toast.makeText(SetupProfile.this, "Age not valid.", Toast.LENGTH_SHORT).show();
                }

                //make sure edit text is not empty
                if(TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname))
                {
                    Toast.makeText(SetupProfile.this, "Some fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(sp_setSport.getSelectedItemPosition()==0)
                {//
                    Toast.makeText(SetupProfile.this, "Please select a sport.", Toast.LENGTH_SHORT).show();
                }//
                else if(sp_setSport.getSelectedItemPosition()==(games.length-1) & TextUtils.isEmpty(other))
                {
                    Toast.makeText(SetupProfile.this, "Please specify your sport.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DBref();
                    Intent myProfile = new Intent(SetupProfile.this,MainActivity.class);
                    startActivity(myProfile);
                }
            }
        });

        sb_strength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int min = 1;

                if (progress<min)
                {
                    sb_strength.setProgress(min);
                    strstr.setText("Strength - " + min);
                    valueS = String.valueOf(min);
                }
                else
                {
                    strstr.setText("Strength - " + progress);
                    valueS = String.valueOf(progress);
                }



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //Toast.makeText(SetupProfile.this,"Strength: " + valueS,Toast.LENGTH_LONG).show();
            }
        });

        sb_agility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                int min = 1;

                if (progress<min)
                {
                    sb_agility.setProgress(min);
                    agiagi.setText("Agility - " + min);
                    valueA = String.valueOf(min);
                }
                else
                {
                    agiagi.setText("Agility - " + progress);
                    valueA = String.valueOf(progress);
                }



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sb_stamina.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int min = 1;

                if (progress<min)
                {
                    sb_stamina.setProgress(min);
                    stasta.setText("Stamina - " + min);
                    valueSt = String.valueOf(min);
                }
                else
                {
                    stasta.setText("Stamina - " + progress);
                    valueSt = String.valueOf(progress);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }//end of oncreate

    //store data to firebase database
    private void DBref(){

        rg_gender = (RadioGroup)findViewById(R.id.rg_gender);
        int selectedRB = rg_gender.getCheckedRadioButtonId();
        rbButton = (RadioButton)findViewById(selectedRB);

        String fname = et_fname.getText().toString();
        String lname = et_lname.getText().toString();
        String age = et_age.getText().toString();
        String strength = valueS;
        String agility = valueA;
        String stamina = valueSt;
        String gender = String.valueOf(rbButton.getText());
        String levels = String.valueOf(sp_setLevel.getSelectedItem());
        String fullname = fname + " " + lname;


        if (sp_setSport.getSelectedItemPosition()==4)
        {
            game = String.valueOf(et_others.getText().toString());
        }

        switch(levels)
        {
            case "Beginner":
                lvlScore = 0.50;
                break;
            case "Intermediate":
                lvlScore = 0.75;
                break;
            case "Expert":
                lvlScore = 1.00;
                break;

        }

        getAgi(agility);
        getStr(strength);
        getSta(stamina);

        double avgrating = ((agi*AGILITY_WEIGHT)+(lvlScore*LEVEL_WEIGHT)+(str*STRENGTH_WEIGHT)+(sta*STAMINA_WEIGHT));
        double finalRating = avgrating*100;
        String result = String.format("%.2f", finalRating);
        double theResult = Double.parseDouble(result)*-1;

        DatabaseReference users = rootRef.child("Users");
        DatabaseReference childRefname = users.child(uid);
        childRefname.child("ID").setValue(uid);
        childRefname.child("Name").setValue(fullname);
        childRefname.child("First Name").setValue(fname);
        childRefname.child("Last Name").setValue(lname);
        childRefname.child("Age").setValue(age);
        childRefname.child("Gender").setValue(gender);
        childRefname.child("Rating").setValue("0.0");
        childRefname.child("Rated").setValue("0.0");

        DatabaseReference sports = rootRef.child("My Sports");
        DatabaseReference mySports = sports.child(game);
        DatabaseReference theSport = mySports.child(uid);
        theSport.child("User ID").setValue(uid);
        theSport.child("Level").setValue(levels);
        theSport.child("Agility").setValue(agility);
        theSport.child("Strength").setValue(strength);
        theSport.child("Stamina").setValue(stamina);
        theSport.child("Rating").setValue(theResult);
        theSport.child("Name").setValue(fullname);
    }

    public void getAgi (String thisAgi) {
        switch (thisAgi)
        {
            case "1":
                agi = 0.10;
                break;
            case "2":
                agi = 0.20;
                break;
            case "3":
                agi = 0.30;
                break;
            case "4":
                agi = 0.40;
                break;
            case "5":
                agi = 0.50;
                break;
            case "6":
                agi = 0.60;
                break;
            case "7":
                agi = 0.70;
                break;
            case "8":
                agi = 0.80;
                break;
            case "9":
                agi = 0.90;
                break;
            case "10":
                agi = 1.00;
                break;
        }

    }

    public void getStr (String thisStr) {
        switch (thisStr)
        {
            case "1":
                str = 0.10;
                break;
            case "2":
                str = 0.20;
                break;
            case "3":
                str = 0.30;
                break;
            case "4":
                str = 0.40;
                break;
            case "5":
                str = 0.50;
                break;
            case "6":
                str = 0.60;
                break;
            case "7":
                str = 0.70;
                break;
            case "8":
                str = 0.80;
                break;
            case "9":
                str = 0.90;
                break;
            case "10":
                str = 1.00;
                break;
        }

    }

    public void getSta (String thisSta) {
        switch (thisSta)
        {
            case "1":
                sta = 0.10;
                break;
            case "2":
                sta = 0.20;
                break;
            case "3":
                sta = 0.30;
                break;
            case "4":
                sta = 0.40;
                break;
            case "5":
                sta = 0.50;
                break;
            case "6":
                sta = 0.60;
                break;
            case "7":
                sta = 0.70;
                break;
            case "8":
                sta = 0.80;
                break;
            case "9":
                sta = 0.90;
                break;
            case "10":
                sta = 1.00;
                break;
        }

    }


    //disable back press button in activity
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
