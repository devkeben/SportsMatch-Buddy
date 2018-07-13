package com.example.kevin.capstoneproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordReset extends AppCompatActivity {

    String email;

    private Button btn_resetPW;
    private EditText et_resetEmail;

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);
        setTitle("Reset Password");

        btn_resetPW = (Button)findViewById(R.id.btn_reset);
        et_resetEmail = (EditText)findViewById(R.id.et_resetEmail);


        btn_resetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userAuth = FirebaseAuth.getInstance();
                email = String.valueOf(et_resetEmail.getText());

                userAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(PasswordReset.this);
                            myAlert.setMessage("Password reset email sent to " + email)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent myLogin = new Intent(PasswordReset.this, Login.class);
                                            startActivity(myLogin);
                                        }
                                    });




                            AlertDialog alert = myAlert.create();
                            alert.show();
                        }
                    }
                });

            }
        });



    }

}
