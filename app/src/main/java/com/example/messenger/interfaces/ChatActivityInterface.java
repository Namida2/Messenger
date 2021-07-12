package com.example.messenger.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.Message;
import com.google.firebase.firestore.FirebaseFirestore;

import adapters.ChatRecyclerViewAdapter;

public interface ChatActivityInterface {
    interface Model {
        void setRecyclerView (RecyclerView recyclerView);
        RecyclerView getRecyclerView ();
        void setAdapter(ChatRecyclerViewAdapter adapter);
        ChatRecyclerViewAdapter getAdapter();
        FirebaseFirestore getDatabase();
        void setChat(Chat chat);
        Chat getChat();
    }
    interface View {
        void scrollToPosition(RecyclerView recyclerView, int position);
        RecyclerView getRecyclerView();
        void onError(int errorCode);
    }
    interface Presenter {
        void sendMessage(String message);
        int getLastAdapterPosition();
        void onDestroy();
    }

}
