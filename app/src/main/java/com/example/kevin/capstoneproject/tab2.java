package com.example.kevin.capstoneproject;

/**
 * Created by Kevin on 1/3/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.client.core.Context;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.ResultSetMetaData;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class tab2 extends Fragment {


    private ImageView picture;
    private Button Upload;
    private String uid;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private FirebaseAuth userAuth;
    private FirebaseAuth.AuthStateListener userAuthListener;
    private FirebaseUser currentUser;

    private ProgressDialog myProgress;

    private static final int GALLERY_INTENT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);

        myProgress = new ProgressDialog(getContext());

        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        authenticateUser();

        uid = currentUser.getUid();

        StorageReference filepath = storage.getReferenceFromUrl("gs://sportsmatch-buddy.appspot.com/");
        StorageReference photo = filepath.child(uid+".jpg");
        //StorageReference userFolder = filepath.child(uid);
        //storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sportsmatch-buddy.appspot.com");

        Upload = (Button)rootView.findViewById(R.id.Upload);

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);

                
            }
        });

        //get specific photo
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(filepath).into(picture);



        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            myProgress.setMessage("Uploading.");
            myProgress.show();
            Uri uri = data.getData();

            //StorageReference filepath = storage.child();
            StorageReference filepath = storage.getReferenceFromUrl("gs://sportsmatch-buddy.appspot.com");
            StorageReference userFolder = filepath.child(uid);
            String name = filepath.getName();

            userFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    myProgress.dismiss();

                    String url = String.valueOf(taskSnapshot.getDownloadUrl());

                    //storage =  FirebaseStorage.getInstance().getReferenceFromUrl(url);

                    //Glide.with(getContext()).using(new FirebaseImageLoader()).load(storage).into(picture);

                    Uri download = taskSnapshot.getDownloadUrl();

                    Picasso.with(getActivity()).load(download).fit().centerCrop().into(picture);
                }
            });
        }

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