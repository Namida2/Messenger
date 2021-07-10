package com.example.messenger.presenters;

import com.example.messenger.Chat;
import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;
import com.example.messenger.interfaces.UserProfileFragmentInterface;

import java.util.ArrayList;

import mainFragment.interfaces.MainFragmentInterface;
import mainFragment.models.MainFragmentModel;
import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.models.MessengerFragmentModel;

import static tools.Const.CollectionChats.CHAT_ID_DELIMITER;
import static tools.Const.CollectionChats.TYPE_DIALOG;

public class UserProfileFragmentPresenter implements UserProfileFragmentInterface.Presenter {

    private UserProfileFragmentInterface.View view;
    private static MainFragmentInterface.Model model;
    private static MessengerFragmentInterface.Model messengerModel;

    public UserProfileFragmentPresenter (UserProfileFragmentInterface.View view) {
        this.view = view;
        messengerModel = new MessengerFragmentModel();
        if(model == null)
            model = new MainFragmentModel();
    }

    @Override
    public UserInterface getChosenUser() {
        return model.getChosenUser();
    }

    @Override
    public void addChat(UserInterface user) {
        Chat chat = new Chat();
        chat.setChatId(User.getCurrentUser().getEmail()
            + CHAT_ID_DELIMITER + user.getEmail());
        chat.setChatName("");
        chat.setLastMessageAt("");
        chat.setType(TYPE_DIALOG);
        chat.setMessagesInChat(0L);
        chat.getUsers().add(User.getCurrentUser());
        chat.getUsers().add(user);
        messengerModel.getChats().add(chat);
        ArrayList<Chat> chats = messengerModel.getChats();
        view.startChatActivity(messengerModel.getChats().size()-1);
    }
}
