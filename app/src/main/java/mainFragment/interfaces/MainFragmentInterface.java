package mainFragment.interfaces;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import adapters.UsersRecyclerViewAdapter;

public interface MainFragmentInterface {
    interface Model {
        ArrayList<UserInterface> getUsersList();
        FirebaseFirestore getDatabase();
        void setAdapter(UsersRecyclerViewAdapter adapter);
        UsersRecyclerViewAdapter getAdapter();
        android.view.View getView();
        void setView(android.view.View view);
        void setRecyclerView(RecyclerView recyclerView);
        RecyclerView getRecyclerView();
    }
    interface View {
        void onError(int errorCode);
    }
    interface Presenter {
        void setModelState();
        android.view.View getView();
        void setView(android.view.View view);
        void setRecyclerView(RecyclerView recyclerView);
    }
}
