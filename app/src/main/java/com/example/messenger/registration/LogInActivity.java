package com.example.messenger.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.registration.interfaces.LogInActivityInterface;
import com.example.messenger.registration.presenters.LogInActivityPresenter;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import tools.ErrorAlertDialog;
import tools.NetworkConnection;

import static tools.GlobalConstants.TAG;

public class LogInActivity extends AppCompatActivity implements LogInActivityInterface.View {

    private LogInActivityInterface.Presenter presenter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialisation();
    }
    private void initialisation() {
        presenter = new LogInActivityPresenter(this);
        RxView.clicks(findViewById(R.id.log_in_button))
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                    if(NetworkConnection.isNetworkConnected(this))
                        presenter.logIn(
                            ((TextView) findViewById(R.id.email)).getText().toString(),
                            ((TextView) findViewById(R.id.password)).getText().toString()
                        );
                    else onError(ErrorAlertDialog.INTERNET_CONNECTION);
                }, error -> {
                    Log.d(TAG, "LogInActivity.onCreate: " + error.getMessage());
                    onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
                , () -> {});

        RxView.clicks(findViewById(R.id.new_account_button))
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                    if(NetworkConnection.isNetworkConnected(this))
                        startRegistrationActivity();
                    else onError(ErrorAlertDialog.INTERNET_CONNECTION);
                }, error -> {
                    Log.d(TAG, "LogInActivity.onCreate: " + error.getMessage());
                    onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
                , () -> {});
    }

    @Override
    public void startRegistrationActivity() {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    @Override
    public void onSuccess() {
        startActivity(new Intent(this, MainActivity.class));
    }
    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist())
            ErrorAlertDialog.getInstance(errorCode)
                .show(getSupportFragmentManager(), "");
    }
}