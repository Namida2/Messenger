package registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messenger.R;
import registration.interfaces.RegistrationActivityInterface;
import registration.presenters.RegistrationActivityPresenter;

import com.example.messenger.SplashScreenActivity;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import tools.ErrorAlertDialog;

import static tools.Const.TAG;
import static tools.NetworkConnection.isNetworkConnected;

public class RegistrationActivity extends AppCompatActivity implements RegistrationActivityInterface.View {

    private RegistrationActivityInterface.Presenter presenter;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button registration;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialisation();
    }
    private void initialisation() {
        presenter = new RegistrationActivityPresenter(this);
        name = findViewById(R.id.name_edit_text);
        email = findViewById(R.id.email_edit_text);
        password = findViewById(R.id.password_edit_text);
        confirmPassword = findViewById(R.id.confirm_password_edit_text);

        registration = findViewById(R.id.registration_button);
        registration.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        registration.setEnabled(false);
        createFieldsObservable();
        RxView.clicks(registration)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(next -> {
                if(isNetworkConnected(this)) {
                    presenter.registrate(
                        name.getText().toString(), email.getText().toString(),
                        password.getText().toString(), confirmPassword.getText().toString());
                }
                else onError(ErrorAlertDialog.INTERNET_CONNECTION);
            }, error -> {
                Log.d(TAG, "LogInActivity.onCreate: " + error.getMessage());
                onError(ErrorAlertDialog.SOMETHING_WRONG);
            }, () -> {});
    }



    private void createFieldsObservable() {
        RxTextView.afterTextChangeEvents(name).debounce(150, TimeUnit.MILLISECONDS)
            .mergeWith(RxTextView.afterTextChangeEvents(email)).debounce(150, TimeUnit.MILLISECONDS)
            .mergeWith(RxTextView.afterTextChangeEvents(confirmPassword)).debounce(150, TimeUnit.MILLISECONDS)
            .mergeWith(RxTextView.afterTextChangeEvents(password)).debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty()
                    || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()){
                    registration.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    registration.setEnabled(false);
                }
                else {
                    registration.setBackgroundColor(getResources().getColor(android.R.color.black));
                    registration.setEnabled(true);
                }
            }, error -> {
                Log.d(TAG, "LogInActivity.createFieldsObservable: " + error.getMessage());
                onError(ErrorAlertDialog.SOMETHING_WRONG);
            }, () -> {});
    }

    @Override
    public void onSuccess() {
        startActivity(new Intent(this, SplashScreenActivity.class));
    }
    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist())
            ErrorAlertDialog.getInstance(errorCode)
                .show(getSupportFragmentManager(), "");
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public Bitmap getBaseAvatar() {
         return ((BitmapDrawable)getResources().getDrawable(R.drawable.image_base_avatar)).getBitmap();
    }
}
