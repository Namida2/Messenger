package registration.models;

import registration.interfaces.LogInActivityInterface;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivityModel implements LogInActivityInterface.Model {

    private FirebaseAuth db;

    public LogInActivityModel() {
        this.db = FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseAuth getFirebaseAuth() {
        return db;
    }
}
