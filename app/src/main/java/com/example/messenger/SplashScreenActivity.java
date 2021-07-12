package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messenger.interfaces.BaseInterface;
import com.example.messenger.interfaces.SplashScreenActivityInterface;
import com.example.messenger.presenters.SplashScreenActivityPresenter;

import messengerFragment.models.MessengerFragmentModel;
import registration.LogInActivity;
import tools.ErrorAlertDialog;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenActivityInterface.View, BaseInterface {

    private SplashScreenActivityInterface.Presenter presenter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if( !MessagesListenerService.isExits() ) {
            MessagesListenerService.setOnCrateConsumer(accept -> {
                presenter = new SplashScreenActivityPresenter(this,this);
            });
            startService(new Intent(this, MessagesListenerService.class));
        } else presenter = new SplashScreenActivityPresenter(this,this);
    }

    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist())
            ErrorAlertDialog.getInstance(errorCode)
                .show(getSupportFragmentManager(), "");
    }

    @Override
    public void onSuccess() {
        MessagesListenerService.getService().startMessagesListening(new MessengerFragmentModel().getChats());
        MessagesListenerService.getService().startChatsListening(User.getCurrentUser());
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void createNewUser() {
        startActivity(new Intent(this, LogInActivity.class));
    }
}
