package com.example.messenger.interfaces;

import com.example.messenger.Chat;
import com.example.messenger.MessagesListenerService;
import com.example.messenger.User;

import java.util.ArrayList;

public interface MessagesObservable {
    interface Observable {
        io.reactivex.rxjava3.core.Observable<MessagesListenerService.Pair> getMessagesObservable(ArrayList<Chat> chats);
        void startChatsListening(User user);
        void subscribe(Subscriber subscriber);
        void unSubscribe(Subscriber subscriber);
        void notifySubscribers(Chat chat);
        void notifySubscribersChatDeleted(String chatId);
    }
    interface Subscriber {
        void notifyMe(Chat chat);
        void notifyChatDeleted(String chatId);
    }

}
