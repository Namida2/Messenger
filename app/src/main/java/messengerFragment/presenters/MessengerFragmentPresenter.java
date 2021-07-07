package messengerFragment.presenters;

import android.util.Log;
import android.view.View;

import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;

import java.util.List;
import java.util.Map;

import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.models.MessengerFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionUsers.COLLECTION_MESSENGER;
import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.CollectionUsers.FIELD_HAS_CHAT_WITH;
import static tools.Const.CollectionUsers.FIELD_NAMES;
import static tools.Const.TAG;

public class MessengerFragmentPresenter implements MessengerFragmentInterface.Presenter {

    private MessengerFragmentInterface.View view;
    private static MessengerFragmentInterface.Model model;

    public MessengerFragmentPresenter (MessengerFragmentInterface.View view) {
        this.view = view;
        if(model == null) {
            model = new MessengerFragmentModel();
            setModelState(User.getCurrentUser());
        }
    }

    @Override
    public void setModelState(UserInterface user) {
        model.getDatabase()
            .collection(COLLECTION_USERS)
            .document("aa@aa.com")
            .collection(COLLECTION_MESSENGER)
            .document(FIELD_HAS_CHAT_WITH)
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    List<String> hasChatWith = (List<String>) data.get(FIELD_NAMES);

                } else {
                    Log.d(TAG, "MessengerFragmentPresenter.setModelState: " + task.getException());
                    view.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
        });
    }

    private void readChats() {

    }

    @Override
    public View getView() {
        return model.getView();
    }
    @Override
    public void setView(View view) {
        model.setView(view);
    }
}
