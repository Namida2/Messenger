package mainFragment.models;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import adapters.UsersRecyclerViewAdapter;
import mainFragment.interfaces.MainFragmentInterface;

public class MainFragmentModel implements MainFragmentInterface.Model {

    private View view;
    private FirebaseFirestore db;
    private ArrayList<UserInterface> userList;
    private RecyclerView recyclerView;
    private UsersRecyclerViewAdapter adapter;

    public MainFragmentModel () {
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
    }

    @Override
    public ArrayList<UserInterface> getUsersList() {
        return userList;
    }
    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }
    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    @Override
    public UsersRecyclerViewAdapter getAdapter() {
        return adapter;
    }
    @Override
    public View getView() {
        return view;
    }
    @Override
    public void setAdapter(UsersRecyclerViewAdapter adapter) {
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
}
