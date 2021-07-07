package messengerFragment.interfaces;

import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import adapters.MessengerRecyclerViewAdapter;

public interface MessengerFragmentInterface {
    interface Model {
        FirebaseFirestore getDatabase();
        MessengerRecyclerViewAdapter getAdapter();
        void setAdapter(MessengerRecyclerViewAdapter adapter);
        android.view.View getView();
        void setView(android.view.View view);
    }
    interface View {
        void onSuccess();
        void onError(int errorCode);
    }
    interface Presenter {
        void setModelState(UserInterface user);
        android.view.View getView();
        void setView(android.view.View view);
    }
}
