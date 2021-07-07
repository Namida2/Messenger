package registration.interfaces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public interface RegistrationActivityInterface {
    interface Model {
        FirebaseAuth getFirebaseAuth();
        FirebaseFirestore getDatabase();
    }
    interface View {
        void onSuccess();
        void onError(int errorCode);
    }
    interface Presenter{
        void registrate(String name, String email, String password, String confirmPassword);
    }
}
