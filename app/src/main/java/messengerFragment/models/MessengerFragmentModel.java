package messengerFragment.models;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.interfaces.BaseInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import adapters.MessengerRecyclerViewAdapter;
import messengerFragment.interfaces.MessengerFragmentInterface;

public class MessengerFragmentModel implements MessengerFragmentInterface.Model {

    private static ArrayList<Chat> chats = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessengerRecyclerViewAdapter adapter;
    private final FirebaseFirestore db;
    private View view;

    public MessengerFragmentModel () {
        this.db = FirebaseFirestore.getInstance();

    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    @Override
    public ArrayList<Chat> getChats() {
        return chats;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }
    @Override
    public MessengerRecyclerViewAdapter getAdapter() {
        return adapter;
    }
    @Override
    public void setAdapter(MessengerRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }
    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
    @Override
    public void setChats(ArrayList<Chat> chats) {
        MessengerFragmentModel.chats = chats;
    }
}
