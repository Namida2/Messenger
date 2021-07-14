package mainFragment.models;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import adapters.UsersRecyclerViewAdapter;
import mainFragment.interfaces.MainFragmentInterface;

public class MainFragmentModel implements MainFragmentInterface.Model {

    private View view;
    private FirebaseFirestore db;
    private ArrayList<UserInterface> allUserList;
    private ArrayList<UserInterface> currentUsersList;
    private RecyclerView recyclerView;
    private UsersRecyclerViewAdapter adapter;
    private static UserInterface user;

    public MainFragmentModel () {
        db = FirebaseFirestore.getInstance();
        allUserList = new ArrayList<>();
    }

    @Override
    public View getView() {
        return view;
    }
    @Override
    public UserInterface getChosenUser() {
        return user;
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
    public ArrayList<UserInterface> getUsersList() {
        return allUserList;
    }

    @Override
    public void setCurrentUsersList(ArrayList<UserInterface> currentUsersList) {
        this.currentUsersList = currentUsersList;
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
    @Override
    public void setChosenUser(UserInterface user) {
        this.user = user;
    }
}
