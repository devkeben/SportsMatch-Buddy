package com.example.kevin.capstoneproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class CustomListAdapter extends ArrayAdapter<String>{

    private String uid;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    public CustomListAdapter(Context context, ArrayList<String> friendList ) {
        super(context,android.R.layout.simple_list_item_1, friendList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View CustomView = myInflater.inflate(R.layout.customlistadapter,parent,false);

        //Get Reference
        StorageReference filepath = storage.getReferenceFromUrl("gs://sportsmatch-buddy.appspot.com/"  + uid);
        //StorageReference userFolder = filepath.child(uid);

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        authenticateUser();

        uid = currentUser.getUid();

        String name = getItem(position);
        TextView myText = (TextView) CustomView.findViewById(R.id.tv_name);
        ImageView myImage = (ImageView) CustomView.findViewById(R.id.iv_picture);


        myText.setText(name);


        return CustomView;

    }

    public void authenticateUser(){
        userAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                }
            }
        };
    }


}
