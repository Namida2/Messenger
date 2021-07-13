package profileFragment.interfaces;

import com.google.firebase.firestore.FirebaseFirestore;

public interface ProfileFragmentIInterface {

    interface Model {
        FirebaseFirestore getDatabase();
    }
    interface View {
        void onError(int errorCode);
        void onSuccess();
    }
    interface Presenter {
        void acceptChanges();

    }
}
