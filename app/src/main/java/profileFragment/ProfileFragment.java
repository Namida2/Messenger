package profileFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.R;
import com.example.messenger.User;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import profileFragment.interfaces.ProfileFragmentIInterface;
import profileFragment.presenters.ProfileFragmentPresenter;
import registration.LogInActivity;
import tools.ErrorAlertDialog;
import tools.NetworkConnection;

import static android.app.Activity.RESULT_OK;
import static tools.Base64.toBase64;
import static tools.Const.ERROR;
import static tools.Const.SUCCESS;
import static tools.Const.TAG;

public class ProfileFragment extends Fragment implements ProfileFragmentIInterface.View {

    private static final int RESULT_LOAD_IMAGE = 1;
    private View contentView;
    private ProfileFragmentIInterface.Presenter presenter;

    private EditText name;
    private EditText status;
    private EditText city;
    private EditText age;
    private EditText sex;
    private ImageView avatar;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProfileFragmentPresenter(this);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        User user = User.getCurrentUser();
        View contentView = inflater.inflate(R.layout.fragment_profile, container, false);
        name = contentView.findViewById(R.id.user_name_text_view);
        name.setText(user.getName());
        status = contentView.findViewById(R.id.user_status_text_view);
        status.setText(user.getStatus());
        city = contentView.findViewById(R.id.user_city_text_view);
        city.setText(user.getCity());
        age = contentView.findViewById(R.id.user_age_text_view);
        age.setText(user.getAge());
        sex = contentView.findViewById(R.id.user_sex_text_view);
        sex.setText(user.getSex());
        avatar = contentView.findViewById(R.id.user_avatar_image_view);
        avatar.setImageBitmap(user.getAvatar());
        avatar.setClipToOutline(true);
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RxView.clicks(contentView.findViewById(R.id.user_avatar_image_view))
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                loadImage();
            }, error -> {
                Log.d(TAG, "UserProfileFragment.onCreateView: " + error.getMessage());
            }, () -> {});

        RxView.clicks(contentView.findViewById(R.id.accept_changes_button))
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                user.setAvatar(((BitmapDrawable) avatar.getDrawable()).getBitmap());
                user.setName(name.getText().toString());
                user.setAge(age.getText().toString());
                user.setCity(city.getText().toString());
                user.setSex(sex.getText().toString());
                user.setStatus(status.getText().toString());
                presenter.acceptChanges();
            }, error -> {
                Log.d(TAG, "UserProfileFragment.onCreateView: " + error.getMessage());
            }, () -> {});

        RxView.clicks(contentView.findViewById(R.id.out_button))
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(User.getCurrentUser().getEmail(),
                    User.getCurrentUser().getPassword()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(NetworkConnection.isNetworkConnected(getContext())) {
                            firebaseAuth.signOut();
                            Intent logInActivity = new Intent(getContext(), LogInActivity.class);
                            startActivity(logInActivity);
                        } else if(!ErrorAlertDialog.isIsExist()) {
                            ErrorAlertDialog.getInstance(ErrorAlertDialog.INTERNET_CONNECTION)
                                .show(getActivity().getSupportFragmentManager(), "");
                        }
                    } else {
                        if(!ErrorAlertDialog.isIsExist()) {
                            ErrorAlertDialog.getInstance(ErrorAlertDialog.SOMETHING_WRONG)
                                .show(getActivity().getSupportFragmentManager(), "");
                        }
                        Log.d(TAG, "ProfileFragment.onCreateView" + task.getException().toString());
                    }
                });
            }, error -> {
                Log.d(TAG, "UserProfileFragment.onCreateView: " + error.getMessage());
            }, () -> {});

        this.contentView = contentView;
        return contentView;
    }

    private void loadImage() {
        Intent intent = new Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                String avatarBase64 = toBase64(bitmap);
                if(avatarBase64.equals(ERROR)) {
                    onError(ErrorAlertDialog.TOO_BIG_IMAGE);
                    return;
                }
                ((ImageView) contentView.findViewById(R.id.user_avatar_image_view)).setImageBitmap(bitmap);
                ((ImageView) contentView.findViewById(R.id.user_avatar_image_view)).setScaleType(ImageView.ScaleType.CENTER_CROP);

            } catch (IOException e) {
                Log.d(TAG, "UserProfileFragment.onCreateView: " + e.getMessage());
            }

        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(getContext(), SUCCESS, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist() && getActivity() != null)
            ErrorAlertDialog.getInstance(errorCode)
                .show(getActivity().getSupportFragmentManager(), "");
    }
}
