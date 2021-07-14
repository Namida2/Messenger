package com.example.messenger.presenters;

import android.util.Log;

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
import static tools.Const.CollectionChats.COLLECTION_CHATS;
import static tools.Const.CollectionChats.TYPE_DIALOG;
import static tools.Const.TAG;

public class UserProfileFragmentPresenter implements UserProfileFragmentInterface.Presenter {

    private UserProfileFragmentInterface.View view;
    private static MainFragmentInterface.Model model;
    private MessengerFragmentInterface.Model messengerModel;

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
        String id0 = User.getCurrentUser().getEmail()
            + CHAT_ID_DELIMITER + user.getEmail();
        String id1 = user.getEmail()
            + CHAT_ID_DELIMITER + User.getCurrentUser().getEmail();

        for(int i = 0; i < messengerModel.getChats().size(); ++i) {
            Chat chat = messengerModel.getChats().get(i);
            if(chat.getChatId().equals(id0) || chat.getChatId().equals(id1)) {
                view.startChatActivity(i);
                return;
            }
        }
//
//        model.getDatabase()
//            .collection(COLLECTION_CHATS)
//            .document(id0)
//            .get().addOnCompleteListener(task -> {
//                if(task.isSuccessful()) {
//                    Log.d(TAG, "UserProfileFragmentPresenter.addChat: success ");
//
//                    model.getDatabase()
//                        .collection(COLLECTION_CHATS)
//                        .document(id1)
//                        .get().addOnCompleteListener(task1 -> {
//                        if(task1.isSuccessful()) {
//                            Log.d(TAG, "UserProfileFragmentPresenter.addChat: success ");
//                        } else {
//                            Log.d(TAG, "UserProfileFragmentPresenter.addChat: " + task1.getException());
//                        }
//                    });
//
//
//                } else {
//                    Log.d(TAG, "UserProfileFragmentPresenter.addChat: " + task.getException());
//                }
//        });

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
