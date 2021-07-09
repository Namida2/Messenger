package com.example.messenger.models;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.interfaces.ChatActivityInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import adapters.ChatRecyclerViewAdapter;

public class ChatActivityModel implements ChatActivityInterface.Model {

    private Chat chat;
    private FirebaseFirestore db;
    private ChatRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    public ChatActivityModel () {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    @Override
    public ChatRecyclerViewAdapter getAdapter() {
        return adapter;
    }
    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }
    @Override
    public Chat getChat() {
        return chat;
    }
    @Override

    public void setChat(Chat chat) {
        this.chat = chat;
    }
    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
    @Override
    public void setAdapter(ChatRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }
}
