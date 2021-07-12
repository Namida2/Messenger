package com.example.messenger.presenters;


import android.util.Log;

import com.example.messenger.Message;
import com.example.messenger.User;
import com.example.messenger.interfaces.BaseInterface;
import com.example.messenger.interfaces.SplashScreenActivityInterface;
import com.example.messenger.interfaces.UserInterface;
import com.example.messenger.models.SplashScreenActivityModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;

import messengerFragment.presenters.MessengerFragmentPresenter;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.TAG;


public class SplashScreenActivityPresenter implements SplashScreenActivityInterface.Presenter {

    private final SplashScreenActivityInterface.View view;
    private static SplashScreenActivityInterface.Model model;
    private final BaseInterface baseView;

    public SplashScreenActivityPresenter (BaseInterface baseView, SplashScreenActivityInterface.View view) {
        this.view = view;
        this.baseView = baseView;
        if(model == null) {
            model = new SplashScreenActivityModel();
        }
        readCurrentUser();
    }

    public void readCurrentUser() {
        FirebaseUser currentUser = model.getAuth().getCurrentUser();
        if(currentUser == null || currentUser.getEmail() == null) {
            view.createNewUser();
            return;
        }
        model.getDatabase().runTransaction(transaction -> {
            DocumentReference docRefUser = model.getDatabase()
                .collection(COLLECTION_USERS)
                .document(currentUser.getEmail());
            User.setCurrentUser(transaction.get(docRefUser).toObject(User.class));
            return true;
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                new MessengerFragmentPresenter(baseView, null);
            } else {
                Log.d(TAG, "MessengerFragmentPresenter.readMessages: " + task.getException());
                baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
            }
        });
    }


}
