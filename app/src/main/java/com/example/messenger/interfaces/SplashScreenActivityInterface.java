package com.example.messenger.interfaces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public interface SplashScreenActivityInterface {

    interface Model {
        FirebaseAuth getAuth();
        FirebaseFirestore getDatabase();
    }
    interface View {
        void createNewUser();
    }
    interface Presenter {
        void readCurrentUser();
    }


}
