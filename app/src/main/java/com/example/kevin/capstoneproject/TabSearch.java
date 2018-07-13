package com.example.kevin.capstoneproject;

/**
 * Created by Kevin on 1/3/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabSearch extends Fragment {

    private String item, game;

    private String []level = {"Select player level...", "Beginner", "Intermediate", "Expert"};
    private String []games = {"Choose a sport...","Basketball", "Badminton", "Tennis", "Soccer", "Volleyball", "Table Tennis", "Billiards", "Others"};

    private Spinner sp_level;
    private Spinner sp_game;
    private Button btn_searchgame;
    private EditText et_minAge;
    private EditText et_maxAge;
    private RadioButton rbButton;
    private RadioGroup rg_gender;
    private EditText et_others;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tabsearch, container, false);

        sp_game = (Spinner)rootView.findViewById(R.id.sp_game);
        sp_level = (Spinner)rootView.findViewById(R.id.sp_level);
        btn_searchgame = (Button)rootView.findViewById(R.id.btn_searchgame);
        et_minAge = (EditText)rootView.findViewById(R.id.et_minage);
        et_maxAge = (EditText)rootView.findViewById(R.id.et_maxage);
        rg_gender = (RadioGroup)rootView.findViewById(R.id.rg_sex);
        et_others = (EditText)rootView.findViewById(R.id.et_othersss);

        et_others.setEnabled(false);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, level );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_level.setAdapter(adapter);*/

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, games ) {
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
        sp_game.setAdapter(adapter1);

        sp_game.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, level) {
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_level.setAdapter(adapter);

        sp_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //button activity
        btn_searchgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedRB = rg_gender.getCheckedRadioButtonId();
                rbButton = (RadioButton)rootView.findViewById(selectedRB);

                if (sp_game.getSelectedItemPosition()==(games.length-1))
                {
                    game = String.valueOf(et_others.getText().toString());
                }
                //String game = String.valueOf(sp_game.getSelectedItem());
                String gender = String.valueOf(rbButton.getText());
                String minAge = et_minAge.getText().toString();
                String maxAge = et_maxAge.getText().toString();
                String level = String.valueOf(sp_level.getSelectedItem());
                String other = et_others.getText().toString();

                if(TextUtils.isEmpty(minAge) || TextUtils.isEmpty(maxAge))
                {
                    Toast.makeText(getContext(), "Fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(sp_game.getSelectedItemPosition()==0)
                {//
                    Toast.makeText(getContext(), "Please select a sport.", Toast.LENGTH_SHORT).show();
                }//
                else if (sp_level.getSelectedItemPosition()==0)
                {
                    Toast.makeText(getContext(), "Please specify level.", Toast.LENGTH_SHORT).show();
                }
                else if(sp_game.getSelectedItemPosition()==(games.length-1) & TextUtils.isEmpty(other))
                {
                    Toast.makeText(getContext(), "Please specify your sport.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    int age1 = Integer.parseInt(minAge);
                    int age2 = Integer.parseInt(maxAge);

                    if (age2<=age1)
                    {
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(getContext());
                        myAlert.setMessage("Invalid age entry.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = myAlert.create();
                        alert.show();
                    }
                    else
                    {
                        final Intent intent = new Intent(getContext(),Games.class);
                        intent.putExtra("Game", game);
                        intent.putExtra("Level",level);
                        intent.putExtra("MinAge",minAge);
                        intent.putExtra("MaxAge",maxAge);
                        intent.putExtra("Gender",gender);
                        startActivity(intent);
                    }
                }



            }
        });

        return rootView;
    }


}
