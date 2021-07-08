package com.example.messenger;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messenger.interfaces.ChatActivityInterface;
import com.example.messenger.presenters.ChatActivityPresenter;

import static tools.Const.EXTRA_TAG_POSITION;

public class ChatActivity extends AppCompatActivity implements ChatActivityInterface.View {

    private ChatActivityInterface.Presenter presenter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_chat);
        initialisation();
    }
    private void initialisation() {
        int position = getIntent().getIntExtra(EXTRA_TAG_POSITION, 0);
        findViewById(R.id.avatar_image_view).setClipToOutline(true);
        presenter = new ChatActivityPresenter(this, position);
    }
}
