package messengerFragment.models;

import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

import adapters.MessengerRecyclerViewAdapter;
import messengerFragment.interfaces.MessengerFragmentInterface;

public class MessengerFragmentModel implements MessengerFragmentInterface.Model {

    private MessengerRecyclerViewAdapter adapter;
    private final FirebaseFirestore db;
    private View view;

    public MessengerFragmentModel () {
        this.db = FirebaseFirestore.getInstance();
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
    public View getView() {
        return view;
    }
    @Override
    public void setView(View view) {
        this.view = view;
    }
}
