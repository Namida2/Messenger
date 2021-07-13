package registration.interfaces;

import android.graphics.Bitmap;

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
        Bitmap getBaseAvatar();
    }
    interface Presenter{
        void registrate(String name, String email, String password, String confirmPassword);
    }
}
