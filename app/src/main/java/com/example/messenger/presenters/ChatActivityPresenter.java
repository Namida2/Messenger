package com.example.messenger.presenters;

import android.util.Log;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.Message;
import com.example.messenger.User;
import com.example.messenger.interfaces.ChatActivityInterface;
import com.example.messenger.interfaces.UserInterface;
import com.example.messenger.models.ChatActivityModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.ChatRecyclerViewAdapter;
import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.models.MessengerFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionChats.COLLECTION_CHATS;
import static tools.Const.CollectionChats.FIELD_CHAT_NAME;
import static tools.Const.CollectionChats.FIELD_LAST_MESSAGE_AT;
import static tools.Const.CollectionChats.FIELD_MESSAGES_IN_CHAT;
import static tools.Const.CollectionChats.FIELD_TYPE;
import static tools.Const.CollectionChats.FIELD_USERS;
import static tools.Const.CollectionMessages.COLLECTION_MESSAGES;
import static tools.Const.CollectionUsers.COLLECTION_MESSENGER;
import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.CollectionUsers.FIELD_CHATS;
import static tools.Const.CollectionUsers.FIELD_CHATS_IDS;
import static tools.Const.TAG;
import static tools.Const.TIME_DELIMITER;

public class ChatActivityPresenter implements ChatActivityInterface.Presenter {

    private int position;
    private ChatActivityInterface.View view;
    private static ChatActivityInterface.Model model;
    private MessengerFragmentInterface.Model messengerModel;

    public ChatActivityPresenter (ChatActivityInterface.View view, int position) {
        this.view = view;
        this.position = position;
        messengerModel = new MessengerFragmentModel();
        if(model == null) {
            model = new ChatActivityModel();
        }
        Chat chat = new MessengerFragmentModel().getChats().get(position);
        ArrayList<Chat> chats = messengerModel.getChats();
        model.setChat(messengerModel.getChats().get(position));
        model.setAdapter(new ChatRecyclerViewAdapter(model.getChat()));
        model.setRecyclerView(view.getRecyclerView());
        model.getRecyclerView().setAdapter(model.getAdapter());
        model.getAdapter().notifyDataSetChanged();
        model.getRecyclerView().scrollToPosition(model.getChat().getMessages().size()-1);
    }

    @Override
    public void sendMessage(String message) {
        User.getCurrentUser().setEmail("aa@aa.com"); // delete
        int newPosition;
        User user = User.getCurrentUser();
        Message newMessage = new Message();
        newMessage.setMessage(message);
        newMessage.setAuthorEmail(user.getEmail());
        newMessage.setAuthorName(user.getName());

        Date currentTime = Calendar.getInstance().getTime();
        String hours = Integer.toString(currentTime.getHours());
        String minutes = Integer.toString(currentTime.getMinutes());
        newMessage.setTime(hours + TIME_DELIMITER + minutes);
        model.getChat().getMessages().add(newMessage);

        newPosition = model.getChat().getMessages().size()-1;
        model.getAdapter().notifyItemChanged(newPosition);
        view.scrollToPosition(model.getRecyclerView(), newPosition);

        CollectionReference collRefMessages = model.getDatabase()
            .collection(COLLECTION_CHATS)
            .document(model.getChat().getChatId())
            .collection(COLLECTION_MESSAGES);
        DocumentReference docRefChat = model.getDatabase()
            .collection(COLLECTION_CHATS)
            .document(model.getChat().getChatId());
        CollectionReference collRefUsers = model.getDatabase()
            .collection(COLLECTION_USERS);

        model.getDatabase().runTransaction(transaction -> {
            Long messagesInChat = 0L;
            if (model.getChat().getMessages().size() != 1)
                messagesInChat = ((Long) transaction.get(docRefChat).getData().get(FIELD_MESSAGES_IN_CHAT)) + 1;

            newMessage.setId(messagesInChat);
            transaction.set(collRefMessages.document(messagesInChat.toString()), newMessage);
            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_MESSAGES_IN_CHAT, messagesInChat);
            transaction.set(docRefChat, data, SetOptions.merge());

            if(model.getChat().getMessages().size() == 1) {
                Chat chat = model.getChat();
                DocumentReference docRefNewChat = model.getDatabase()
                    .collection(COLLECTION_CHATS).document(chat.getChatId());
                data = new HashMap<>();
                data.put(FIELD_CHAT_NAME, chat.getChatName());
                data.put(FIELD_LAST_MESSAGE_AT, chat.getMessages().get(chat.getMessages().size()-1).getTime());
                data.put(FIELD_MESSAGES_IN_CHAT, chat.getMessages().size());
                data.put(FIELD_TYPE, chat.getType());
                List<String> listUsers = new ArrayList<>();
                for(UserInterface chatUser : chat.getUsers()) {
                    listUsers.add(chatUser.getEmail());
                }
                data.put(FIELD_USERS, listUsers);
                transaction.set(docRefNewChat, data);

                for(UserInterface userInterface : chat.getUsers()) {
                    DocumentReference docRefChats = collRefUsers
                        .document(userInterface.getEmail())
                        .collection(COLLECTION_MESSENGER)
                        .document(FIELD_CHATS);
                    List<String> chatId = new ArrayList<>();
                    chatId.add(chat.getChatId());
                    transaction.update(docRefChats, FIELD_CHATS_IDS, chatId);
                }
            }

            return true;
        }).addOnCompleteListener(task -> {
            if( !task.isSuccessful() ) {
                Log.d(TAG, "MessengerFragmentPresenter.sendMessage: " + task.getException());
                view.onError(ErrorAlertDialog.SOMETHING_WRONG);
            }
        });

    }

    @Override
    public int getLastAdapterPosition() {
        return model.getChat().getMessages().size() == 0 ? 0
            : model.getChat().getMessages().size()-1;
    }
}
