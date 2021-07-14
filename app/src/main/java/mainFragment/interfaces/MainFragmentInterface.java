package mainFragment.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import adapters.UsersRecyclerViewAdapter;

public interface MainFragmentInterface {
    interface Model {
        void setChosenUser(UserInterface user);
        UserInterface getChosenUser();
        ArrayList<UserInterface> getUsersList();
        void setCurrentUsersList(ArrayList<UserInterface> currentUsersList);
        FirebaseFirestore getDatabase();
        void setAdapter(UsersRecyclerViewAdapter adapter);
        UsersRecyclerViewAdapter getAdapter();
        android.view.View getView();
        void setView(android.view.View view);
        void setRecyclerView(RecyclerView recyclerView);
        RecyclerView getRecyclerView();

    }
    interface View {
        void startUserProfileFragment(UserInterface user);
        void onError(int errorCode);
    }
    interface Presenter {
        void showUsers(String name);
        void onResume();
        void setModelState();
        android.view.View getView();
        void setView(android.view.View view);
        void setRecyclerView(RecyclerView recyclerView);
    }
}
