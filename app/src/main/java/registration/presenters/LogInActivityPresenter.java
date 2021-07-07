package registration.presenters;

import android.util.Log;

import registration.interfaces.LogInActivityInterface;
import registration.models.LogInActivityModel;

import tools.ErrorAlertDialog;

import static tools.Const.TAG;

public class LogInActivityPresenter implements LogInActivityInterface.Presenter {

    private LogInActivityInterface.View view;
    private static LogInActivityInterface.Model model;

    public LogInActivityPresenter (LogInActivityInterface.View view ) {
        this.view = view;
        if(model == null) model = new LogInActivityModel();
    }
    @Override
    public void logIn(String email, String password) {
        if( !isValid(email, password) )
            view.onError(ErrorAlertDialog.EMPTY_FIELD);
        else {
            model.getFirebaseAuth().signInWithEmailAndPassword(email.toLowerCase(), password).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                    view.onSuccess();
                else {
                    Log.d(TAG, "Wrong email or password.");
                    Log.d(TAG, task.getException().toString());
                    view.onError(ErrorAlertDialog.WRONG_EMAIL_OR_PASSWORD);
                }
            });
        }
    }
    public boolean isValid(String email, String password) {
        return !email.isEmpty() && !password.isEmpty() && !email.contains(" ") && !password.contains(" ");
    }
}
