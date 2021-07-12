package profileFragment.models;

import com.google.firebase.firestore.FirebaseFirestore;

import profileFragment.interfaces.ProfileFragmentIInterface;

public class ProfileFragmentModel implements ProfileFragmentIInterface.Model {

    private FirebaseFirestore db;

    public ProfileFragmentModel() {
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public FirebaseFirestore getDatabase() {
        return db;
    }
}
