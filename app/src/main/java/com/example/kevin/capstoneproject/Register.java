package com.example.kevin.capstoneproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText et_unameReg;
    private EditText et_pwReg;
    private Button btn_signup;

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;

    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_unameReg = (EditText)findViewById(R.id.et_nameReg);
        et_pwReg = (EditText)findViewById(R.id.et_pwReg);

        btn_signup = (Button)findViewById(R.id.btn_signup);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();

        //new line
        //authenticateUser();

        btn_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view)
            {

                if(TextUtils.isEmpty(et_unameReg.getText().toString()) || TextUtils.isEmpty(et_pwReg.getText().toString()))
                {
                    Toast.makeText(Register.this, "Fields are empty.", Toast.LENGTH_SHORT).show();
                }
                else if(et_pwReg.getText().toString().length()<8)
                {
                    Toast.makeText(Register.this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    createUser();
                    authenticateUser();
                }
            }
        });
    }


    //creating login with email and password
    public void createUser(){

        String user = et_unameReg.getText().toString();
        String pass = et_pwReg.getText().toString();

        if (!isValidEmail(user))
        {
            Toast.makeText(Register.this, "Invalid Email", Toast.LENGTH_SHORT).show();
        }
        //make sure edit text is not empty
        else
        {
            userAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {

                        Toast.makeText(Register.this, "Sign Up Successful. Please verify your email to login.", Toast.LENGTH_SHORT).show();
                        sendEmail();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Register.this,Login.class));
                        finish();
                        //startActivity(new Intent(Register.this,SetupProfile.class));
                        //finish();
                    }
                    else {
                        Toast.makeText(Register.this, "Sign Up Failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    //login user after registration
    /*public void signinUser(){
        String user = et_unameReg.getText().toString();
        String pass = et_pwReg.getText().toString();

        userAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                startActivity(new Intent(Register.this,SetupProfile.class));
                finish();
            }
        });
    }*/

    public void sendEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("EMAIL", "Email verification sent.");
                            //Toast.makeText(Register.this, "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void authenticateUser(){
        userAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    //uid = currentUser.getUid();
                }
            }
        };
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
