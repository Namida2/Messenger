package com.example.messenger.presenters;

import com.example.messenger.interfaces.ChatActivityInterface;
import com.example.messenger.models.ChatActivityModel;

public class ChatActivityPresenter implements ChatActivityInterface.Presenter {

    private int position;
    private ChatActivityInterface.View view;
    private static ChatActivityInterface.Model model;

    public ChatActivityPresenter (ChatActivityInterface.View view, int position) {
        this.view = view;
        this.position = position;
        if(model == null)
            model = new ChatActivityModel();
    }

}
