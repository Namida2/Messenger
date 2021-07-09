package com.example.messenger.presenters;

import android.util.Log;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.Message;
import com.example.messenger.User;
import com.example.messenger.interfaces.ChatActivityInterface;
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
import java.util.Map;

import adapters.ChatRecyclerViewAdapter;
import messengerFragment.models.MessengerFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionChats.COLLECTION_CHATS;
import static tools.Const.CollectionChats.FIELD_MESSAGES_IN_CHAT;
import static tools.Const.CollectionMessages.COLLECTION_MESSAGES;
import static tools.Const.TAG;
import static tools.Const.TIME_DELIMITER;

public class ChatActivityPresenter implements ChatActivityInterface.Presenter {

    private int position;
    private ChatActivityInterface.View view;
    private static ChatActivityInterface.Model model;

    public ChatActivityPresenter (ChatActivityInterface.View view, int position) {
        this.view = view;
        this.position = position;
        if(model == null) {
            model = new ChatActivityModel();
            Chat chat = new MessengerFragmentModel().getChats().get(position);
            model.setChat(new MessengerFragmentModel().getChats().get(position));
            model.setAdapter(new ChatRecyclerViewAdapter(model.getChat()));
        }
        model.setRecyclerView(view.getRecyclerView());
        model.getRecyclerView().setAdapter(model.getAdapter());
        model.getAdapter().notifyDataSetChanged();
        model.getRecyclerView().scrollToPosition(model.getChat().getMessages().size()-1);
    }

    @Override
    public void sendMessage(String message) {
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

        model.getDatabase().runTransaction(transaction -> {
            Long messagesInChat = ((Long) transaction.get(docRefChat).getData().get(FIELD_MESSAGES_IN_CHAT)) + 1;
            newMessage.setId(messagesInChat);

            transaction.set(collRefMessages.document(messagesInChat.toString()), newMessage);
            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_MESSAGES_IN_CHAT, messagesInChat);
            transaction.set(docRefChat, data, SetOptions.merge());

            return true;
        }).addOnCompleteListener(task -> {
            if( !task.isSuccessful() ) {
                Log.d(TAG, "MessengerFragmentPresenter.readMessages: " + task.getException());
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
