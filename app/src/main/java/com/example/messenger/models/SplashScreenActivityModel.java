package com.example.messenger.models;

import com.example.messenger.interfaces.SplashScreenActivityInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivityModel implements SplashScreenActivityInterface.Model {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public SplashScreenActivityModel () {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public FirebaseAuth getAuth() {
        return auth;
    }

    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }
}
