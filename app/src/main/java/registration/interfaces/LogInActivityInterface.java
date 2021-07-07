package registration.interfaces;

import com.google.firebase.auth.FirebaseAuth;

public interface LogInActivityInterface {
    interface Model {
        FirebaseAuth getFirebaseAuth();
    }
    interface View {
        void startRegistrationActivity();
        void onSuccess();
        void onError(int errorCode);
    }
    interface Presenter {
        void logIn(String email, String password);
    }
}
