package profileFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.R;
import com.example.messenger.User;
import com.example.messenger.interfaces.BaseInterface;
import com.example.messenger.interfaces.UserInterface;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.internal.operators.observable.ObservableConcatMap;
import profileFragment.interfaces.ProfileFragmentIInterface;
import profileFragment.presenters.ProfileFragmentPresenter;
import tools.ErrorAlertDialog;

import static android.app.Activity.RESULT_OK;
import static tools.Const.TAG;

public class ProfileFragment extends Fragment implements ProfileFragmentIInterface.View {

    private static final int RESULT_LOAD_IMAGE = 1;
    private View contentView;
    private ProfileFragmentIInterface.Presenter presenter;

    private EditText name;
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
        UserInterface user = User.getCurrentUser();
        View contentView = inflater.inflate(R.layout.fragment_profile, container, false);
        name = contentView.findViewById(R.id.user_name_text_view);
        name.setText(user.getName());
        city = contentView.findViewById(R.id.user_city_text_view);
        city.setText(user.getCity());
        age = contentView.findViewById(R.id.user_age_text_view);
        age.setText(user.getAge());
        sex = contentView.findViewById(R.id.user_sex_text_view);
        sex.setText(user.getSex());
        avatar = contentView.findViewById(R.id.user_avatar_image_view);
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
                User.getCurrentUser().setAvatar();
                presenter.acceptChanges();
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
            Uri selectedImage = data.getData();
            ((ImageView) contentView.findViewById(R.id.user_avatar_image_view)).setImageURI(selectedImage);
            ((ImageView) contentView.findViewById(R.id.user_avatar_image_view)).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist() && getActivity() != null)
            ErrorAlertDialog.getInstance(errorCode)
                .show(getActivity().getSupportFragmentManager(), "");
    }
}
