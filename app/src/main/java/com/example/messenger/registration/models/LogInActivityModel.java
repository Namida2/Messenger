package com.example.messenger.registration.models;

import com.example.messenger.registration.interfaces.LogInActivityInterface;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivityModel implements LogInActivityInterface.Model {

    private FirebaseAuth db;

    public LogInActivityModel() {
        this.db = FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseAuth getFirebaseAuth() {
        return db;
    }
}
