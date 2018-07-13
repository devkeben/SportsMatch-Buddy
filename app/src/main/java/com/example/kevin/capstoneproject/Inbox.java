package com.example.kevin.capstoneproject;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Inbox extends AppCompatActivity {

    private String friendName, friendID, uid, userName, currentUserID, keyConvo;
    private String temp_key, temp_key1;
    private String roomID;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference root = mDatabase.getReference();

    private Button btn_send;
    private TextView tv_msg;
    private EditText et_message;
    private ScrollView sv_inbox;

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        authenticateUser();
        currentUserID = currentUser.getUid();

        btn_send = (Button)findViewById(R.id.btn_send);
        tv_msg  = (TextView)findViewById(R.id.tv_msg);
        et_message  = (EditText)findViewById(R.id.et_message);
        sv_inbox = (ScrollView)findViewById(R.id.sv_inbox);
        sv_inbox.fullScroll(View.FOCUS_DOWN);

        roomID = getIntent().getExtras().get("RoomID").toString();
        friendName = getIntent().getExtras().get("FriendName").toString();
        friendID = getIntent().getExtras().get("FriendID").toString();
        uid = getIntent().getExtras().get("UserID").toString();
        userName = getIntent().getExtras().get("UserName").toString();
        setTitle(friendName);

        final DatabaseReference room = root.child("Messages");
        final DatabaseReference msgDetailTo = room.child(roomID);


        btn_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = msgDetailTo.push().getKey();
                msgDetailTo.updateChildren(map);

                DatabaseReference message_root = msgDetailTo.child(temp_key);
                //DatabaseReference message_rootFrom = msgDetailFrom.child(temp_key1);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("Name", userName);
                map2.put("Message", et_message.getText().toString());

                message_root.updateChildren(map2);
                et_message.setText("");
            }
        });

        msgDetailTo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);

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
    }



    private String chat_msg,chat_user_name;
    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {

            chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            tv_msg.append(chat_user_name + " : " + chat_msg + " \n");
        }
    }


    public void authenticateUser(){
        userAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {

                }
            }
        };
    }
}
