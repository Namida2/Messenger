package messengerFragment.interfaces;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

import adapters.MessengerRecyclerViewAdapter;

public interface MessengerFragmentInterface {
    interface Model {
        RecyclerView getRecyclerView();
        void setRecyclerView(RecyclerView recyclerView);
        ArrayList<Chat> getChats();
        void setChats(ArrayList<Chat> chats);
        FirebaseFirestore getDatabase();
        MessengerRecyclerViewAdapter getAdapter();
        void setAdapter(MessengerRecyclerViewAdapter adapter);
        android.view.View getView();
        void setView(android.view.View view);
    }
    interface View {
        void startChatActivity(int position);
        void showAcceptOrCancelDialog(int position, String title);
    }
    interface Presenter {
        void onResume();
        void setRecyclerView(RecyclerView recyclerView);
        MessengerRecyclerViewAdapter getAdapter();
        void setModelState(UserInterface user);
        android.view.View getView(MessengerFragmentInterface.View view);
        void setView(android.view.View view);
        void removeChatFromDatabase(int position);
    }
}
