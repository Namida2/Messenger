package com.example.messenger.interfaces;

import com.example.messenger.Chat;
import com.google.firebase.firestore.FirebaseFirestore;

public interface ChatActivityInterface {
    interface Model {
        FirebaseFirestore getDatabase();

        void setChat(Chat chat);
        Chat getChat();
    }
    interface View {

    }
    interface Presenter {

    }

}
