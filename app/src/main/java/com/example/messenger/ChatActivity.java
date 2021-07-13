package com.example.messenger;

import android.app.Activity;
import android.icu.text.IDNA;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.interfaces.ChatActivityInterface;
import com.example.messenger.presenters.ChatActivityPresenter;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import tools.ErrorAlertDialog;
import tools.NetworkConnection;

import static tools.Const.EXTRA_TAG_POSITION;
import static tools.Const.TAG;

public class ChatActivity extends AppCompatActivity implements ChatActivityInterface.View {

    private ChatActivityInterface.Presenter presenter;
    private EditText messageEditText;
    private Button sendButton;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_chat);
        initialisation();
    }
    private void initialisation() {
        int position = getIntent().getIntExtra(EXTRA_TAG_POSITION, 0);
        presenter = new ChatActivityPresenter(this, position);
        ImageView imageView = findViewById(R.id.avatar_image_view);
        imageView.setClipToOutline(true);
        imageView.setImageBitmap(presenter.getDialogBitmap());
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);
        RxView.clicks(sendButton)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                if(NetworkConnection.isNetworkConnected(this)) {
                    if (messageEditText.getText().toString().equals("")) return;
                    String aaaaa = messageEditText.getText().toString();
                    presenter.sendMessage(messageEditText.getText().toString());
                    messageEditText.setText("");
                } else onError(ErrorAlertDialog.INTERNET_CONNECTION);
            }, error -> {
                Log.d(TAG, "LogInActivity.onCreate: " + error.getMessage());
                onError(ErrorAlertDialog.SOMETHING_WRONG);
            }, () -> {});
    }

    @Override
    public void scrollToPosition(RecyclerView recyclerView, int position) {
        RecyclerView.SmoothScroller scroller = new LinearSmoothScroller(this);
        scroller.setTargetPosition(position);
        recyclerView.getLayoutManager().startSmoothScroll(scroller);
    }

    @Override
    public RecyclerView getRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);
            recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                if (bottom < oldBottom)
                    scrollToPosition(recyclerView, presenter.getLastAdapterPosition());
            });
        return recyclerView;
    }
    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist())
            ErrorAlertDialog.getInstance(errorCode)
                .show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
