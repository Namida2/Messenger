package com.example.messenger;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.messenger.interfaces.UserInterface;
import com.example.messenger.interfaces.UserProfileFragmentInterface;
import com.example.messenger.presenters.UserProfileFragmentPresenter;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import static tools.Const.EXTRA_TAG_POSITION;
import static tools.Const.TAG;

public class UserProfileFragment extends Fragment implements UserProfileFragmentInterface.View {

    private UserProfileFragmentInterface.Presenter presenter;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new UserProfileFragmentPresenter(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        UserInterface user = presenter.getChosenUser();
        View contentView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ( (TextView) contentView.findViewById(R.id.user_name_text_view)).setText(user.getName());
        ( (TextView) contentView.findViewById(R.id.user_city_text_view)).setText(user.getCity());
        ( (TextView) contentView.findViewById(R.id.user_age_text_view)).setText(user.getAge());
        ( (TextView) contentView.findViewById(R.id.user_sex_text_view)).setText(user.getSex());
        ( (ImageView) contentView.findViewById(R.id.user_avatar_image_view)).setClipToOutline(true);

        RxView.clicks(contentView.findViewById(R.id.go_to_dialog_button))
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                presenter.addChat(user);
            }, error -> {
                Log.d(TAG, "UserProfileFragment.onCreateView: " + error.getMessage());
            }, () -> {});
        return contentView;
    }

    @Override
    public void startChatActivity(int position) {
        if(getActivity() == null) return;
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(EXTRA_TAG_POSITION, position);
        startActivity(new Intent(getActivity(), ChatActivity.class));
    }
}
