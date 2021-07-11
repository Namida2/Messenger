package com.example.messenger.presenters;

import com.example.messenger.interfaces.SplashScreenActivityInterface;

public class SplashScreenActivityPresenter implements SplashScreenActivityInterface {

    private SplashScreenActivityInterface.View view;

    public SplashScreenActivityPresenter (SplashScreenActivityInterface.View view) {
        this.view = view;
    }

}
