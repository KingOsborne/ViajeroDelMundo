package com.example.viajerodelmundo;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<GreatDeals> mDeals;
    private static ListActivity caller;
    private static final int RC_SIGN_IN = 123;
    public static boolean isAdmin;

    private FirebaseUtil(){}

    public static void openFirebaseRef(String reference, final ListActivity callerActivity){
        if (mFirebaseUtil == null){
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }else{
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Snackbar snackbar = Snackbar.make(callerActivity.findViewById(android.R.id.content), "Welcome Back!!", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(3000);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
                    snackbar.show();
                }
            };
        }
        mDeals = new ArrayList<GreatDeals>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(reference);
    }

    private static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private static void checkAdmin(String uid){
        FirebaseUtil.isAdmin = false;

        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators").child(uid);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(listener);
    }

    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

}
