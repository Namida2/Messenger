package registration.presenters;

import android.util.Log;

import com.example.messenger.User;
import registration.interfaces.RegistrationActivityInterface;
import registration.models.RegistrationActivityModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.ErrorAlertDialog;

import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.TAG;

public class RegistrationActivityPresenter implements RegistrationActivityInterface.Presenter {

    private final RegistrationActivityInterface.View view;
    private static RegistrationActivityInterface.Model model;

    public RegistrationActivityPresenter (RegistrationActivityInterface.View view ) {
        this.view = view;
        if (model == null) model = new RegistrationActivityModel();
    }
    @Override
    public void registrate(String name, String email, String password, String confirmPassword) {
        if( isValid(email, password, confirmPassword) ) {
            model.getFirebaseAuth().fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().isEmpty()) {
                        User user = User.getCurrentUser();
                        user.setName(name);
                        user.setEmail(email.toLowerCase());
                        user.setPassword(password);
                        writeNewUser(user);
                    } else {
                        view.onError(ErrorAlertDialog.EMAIL_ALREADY_EXIST);
                    }
                } else {
                    Log.d(TAG, "RegistrationActivityPresenter.registrate: " + task.getException());
                    view.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
            });
        }
    }
    private void writeNewUser(User user) {
        model.getFirebaseAuth().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(taskRegistration -> {
                if(taskRegistration.isSuccessful()) {
                    model.getDatabase().collection(COLLECTION_USERS)
                        .document(user.getEmail())
                        .set(user).addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                            view.onSuccess();
                        else {
                            Log.d(TAG, "RegistrationActivityPresenter.writeNewUser: " + task.getException());
                            view.onError(ErrorAlertDialog.SOMETHING_WRONG);
                        }
                    });
                } else {
                    Log.d(TAG, "RegistrationActivityPresenter.writeNewUser: " + taskRegistration.getException());
                    view.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
            });
    }
    public boolean isValid(String email, String password, String confirmPassword) {
        if( !isEmailValid(email) ) {
            Log.d(TAG, "Email is invalid");
            view.onError(ErrorAlertDialog.WRONG_EMAIL);
            return false;
        }
        if ( password.length() < 6 ) {
            Log.d(TAG, "Password is too short.");
            view.onError(ErrorAlertDialog.SHORT_PASSWORD);
            return false;
        }
        if ( !password.equals(confirmPassword) ) {
            Log.d(TAG, "Invalid password  confirmation.");
            view.onError(ErrorAlertDialog.PASSWORD_CONFIRM_ERROR);
            return false;
        }
        return true;
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
