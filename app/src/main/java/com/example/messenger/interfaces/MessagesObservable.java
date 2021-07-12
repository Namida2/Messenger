package com.example.messenger.interfaces;

import com.example.messenger.Chat;

import io.reactivex.rxjava3.core.Observable;

public interface MessagesObservable {
    interface Observable {
        io.reactivex.rxjava3.core.Observable<Long> startDocumentListening();
        void subscribe(Subscriber subscriber);
        void unSubscribe(Subscriber subscriber);
        void notifySubscribers(Chat chat);
    }
    interface Subscriber {
        void notifyMe(Chat chat);
    }

}
