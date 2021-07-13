package profileFragment.presenters;

import android.util.Log;

import com.example.messenger.User;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

import profileFragment.interfaces.ProfileFragmentIInterface;
import profileFragment.models.ProfileFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Base64.toBase64;
import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.CollectionUsers.FIELD_AGE;
import static tools.Const.CollectionUsers.FIELD_AVATAR_STRING;
import static tools.Const.CollectionUsers.FIELD_CITY;
import static tools.Const.CollectionUsers.FIELD_EMAIL;
import static tools.Const.CollectionUsers.FIELD_NAME;
import static tools.Const.CollectionUsers.FIELD_PASSWORD;
import static tools.Const.CollectionUsers.FIELD_SEX;
import static tools.Const.CollectionUsers.FIELD_STATUS;
import static tools.Const.TAG;

public class ProfileFragmentPresenter implements ProfileFragmentIInterface.Presenter {

    private ProfileFragmentIInterface.View view;
    private static ProfileFragmentIInterface.Model model;

    public ProfileFragmentPresenter(ProfileFragmentIInterface.View view) {
        this.view = view;
        if(model == null) {
            model = new ProfileFragmentModel();
        }
    }

    @Override
    public void acceptChanges() {
        User user = User.getCurrentUser();
        user.setAvatarString(toBase64(user.getAvatar()));
        DocumentReference docRefUser =  model.getDatabase()
            .collection(COLLECTION_USERS)
            .document(user.getEmail());
        model.getDatabase().runTransaction(transaction -> {
            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_AGE, user.getAge());
            data.put(FIELD_AVATAR_STRING, user.getAvatarString());
            data.put(FIELD_CITY, user.getCity());
            data.put(FIELD_EMAIL, user.getEmail());
            data.put(FIELD_NAME, user.getName());
            data.put(FIELD_PASSWORD, user.getPassword());
            data.put(FIELD_SEX, user.getSex());
            data.put(FIELD_STATUS, user.getStatus());
            transaction.set(docRefUser, data);
            return true;
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                view.onSuccess();
            } else {
                Log.d(TAG, "MessengerFragmentPresenter.setModelState: " + task.getException());
                view.onError(ErrorAlertDialog.SOMETHING_WRONG);
            }
        });

    }


}
