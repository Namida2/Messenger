package com.example.messenger;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.messenger.interfaces.UserInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import static tools.Const.TAG;

public class MainActivity extends AppCompatActivity {


    private static final int RESULT_LOAD_IMAGE = 11;
    private BottomNavigationView bottomNavigationView;
    private NavHostFragment navHostFragment;
    private NavHostController navHostController;
    private Disposable disposable;
    private TextView title;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialisation();
        disposable = getBottomAppBarObservable()
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(itemId -> {
                NavDestination currentDestination = navHostController.getCurrentDestination();
                switch ( itemId ) {
                    case R.id.mainFragment:
                        if(currentDestination.getId() != R.id.mainFragment)
                            navHostController.navigate(R.id.mainFragment,
                                null
                                //new NavOptions.Builder().setEnterAnim(R.anim.fragment_show).build()
                            );
                        title.setText(getResources().getString(R.string.main));
                        break;
                    case R.id.messengerFragment:
                        if(currentDestination.getId() != R.id.messengerFragment)
                            navHostController.navigate(R.id.messengerFragment,
                                null
                                //new NavOptions.Builder().setEnterAnim(R.anim.fragment_show).build()
                            );
                        title.setText(getResources().getString(R.string.messenger));
                        break;
                    case R.id.profileFragment:
                        if(currentDestination.getId() != R.id.profileFragment)
                            navHostController.navigate(R.id.profileFragment,
                                null
                                //new NavOptions.Builder().setEnterAnim(R.anim.fragment_show).build()
                            );
                        title.setText(getResources().getString(R.string.profile));
                        break;
                }
            }, error -> {
                Log.d(TAG, Thread.currentThread().getName());
            });
    }


    public void showProfileFragment(UserInterface user) {
        navHostController.navigate(R.id.userProfileFragment,
            null,
            new NavOptions.Builder().setPopEnterAnim(android.R.anim.slide_in_left)
                .setEnterAnim(R.anim.slide_left).build()
        );
    }

    private void initialisation () {
        title = findViewById(R.id.title);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navHostController = (NavHostController) navHostFragment.getNavController();
    }
    private Observable<Integer> getBottomAppBarObservable () {
        return Observable.create(emitter -> {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                emitter.onNext(item.getItemId());
                return true;
            });
        });
    }

}
