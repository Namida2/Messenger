package com.example.messenger;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messenger.interfaces.SplashScreenActivityInterface;
import com.example.messenger.presenters.SplashScreenActivityPresenter;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenActivityInterface.View {

    private SplashScreenActivityInterface.Presenter presenter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        presenter = new SplashScreenActivityPresenter(this);
    }
}
