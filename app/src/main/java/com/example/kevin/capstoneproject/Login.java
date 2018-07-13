package com.example.kevin.capstoneproject;

import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Set;

public class Login extends AppCompatActivity {

    private LoginButton btn_fbLogin;

    private EditText et_uname;
    private EditText et_pw;
    private TextView tv_register;
    private TextView tv_resetPw;
    private Button btn_login;
    private TextView tv_internet;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        et_uname = (EditText)findViewById(R.id.et_uname);
        et_pw = (EditText)findViewById(R.id.et_pass);
        tv_register = (TextView)findViewById(R.id.tv_reg);
        tv_resetPw = (TextView)findViewById(R.id.tv_resetPW);
        tv_internet = (TextView)findViewById(R.id.tv_internet);

        btn_fbLogin = (LoginButton)findViewById(R.id.btn_fb_login);
        btn_login = (Button)findViewById(R.id.btn_login);

        if (!isNetworkAvailable())
        {
            tv_internet.setText("No internet connection!");
        }

        callbackManager = CallbackManager.Factory.create();
        btn_fbLogin.setReadPermissions("email", "public_profile");
        btn_fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB", "facebook:onSuccess:" + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //check if user is authenticated or logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //FirebaseUser user = firebaseAuth.getCurrentUser();
                if(firebaseAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(Login.this,MainActivity.class));

                }
            }
        };

        //reset password
        tv_resetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myReset = new Intent(Login.this,PasswordReset.class);
                startActivity(myReset);

            }
        });

        //register button
        tv_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view1){
                Intent myRegister = new Intent(Login.this,Register.class);
                startActivity(myRegister);
            }
        });


        //login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });
    }

    private void startSignIn() {
        String user = et_uname.getText().toString();
        String pass = et_pw.getText().toString();

        if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
            Toast.makeText(Login.this, "Fields are empty.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        FirebaseUser myUser = mAuth.getCurrentUser();
                        if (myUser.isEmailVerified())
                        {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            mAuth.signOut();
                            Intent myRegister = new Intent(Login.this,Login.class);
                            startActivity(myRegister);
                            Toast.makeText(Login.this, "Please verify your email to login.", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Incorrect email or password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FB", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FB", "signInWithCredential:onComplete:" + task.isSuccessful());


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("FB", "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            startActivity(new Intent(Login.this,MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Login.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
