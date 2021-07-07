package registration.models;

import registration.interfaces.RegistrationActivityInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivityModel implements RegistrationActivityInterface.Model {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;

    public RegistrationActivityModel () {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }
    @Override
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }
}
