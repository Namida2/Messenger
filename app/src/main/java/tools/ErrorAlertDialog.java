package tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.messenger.R;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class ErrorAlertDialog extends DialogFragment {

    public static final int SHORT_PASSWORD = 0;
    public static final int WRONG_EMAIL = 1;
    public static final int PASSWORD_CONFIRM_ERROR = 2;
    public static final int INTERNET_CONNECTION = 3;
    public static final int EMAIL_ALREADY_EXIST = 4;
    public static final int WRONG_EMAIL_OR_PASSWORD = 5;
    public static final int EMPTY_FIELD = 6;
    public static final int SOMETHING_WRONG = 7;

    private Consumer<Object> acceptAction;
    private static final AtomicBoolean isExist = new AtomicBoolean(false);
    private static int dialogType;

    public static ErrorAlertDialog getInstance(int dialogType) {
        isExist.set(true);
        ErrorAlertDialog.dialogType = dialogType;
        ErrorAlertDialog errorAlertDialog = new ErrorAlertDialog();
        errorAlertDialog.setCancelable(false);
        return errorAlertDialog;
    }
    public void setActionConsumer (Consumer<Object> acceptAction) {
        this.acceptAction = acceptAction;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alertDialogStyle);
        View view = View.inflate(getActivity(), R.layout.dialog_error, null);
        TextView title = view.findViewById(R.id.title);
        TextView text = view.findViewById(R.id.text);
        Button button = view.findViewById(R.id.ok);
        RxView.clicks(button).
            debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                if(acceptAction != null) acceptAction.accept(new Object());
                dismiss();
                isExist.set(false);
            });

        switch (dialogType) {
            case SHORT_PASSWORD:
                title.setText(R.string.error_alert_dialog_wrong_password_title);
                text.setText(R.string.error_alert_dialog_wrong_password_text);
                break;
            case WRONG_EMAIL:
                title.setText(R.string.error_alert_dialog_wrong_email_format_title);
                text.setText(R.string.error_alert_dialog_wrong_email_format_text);
                break;
            case PASSWORD_CONFIRM_ERROR:
                title.setText(R.string.error_alert_dialog_wrong_confirm_password_title);
                text.setText(R.string.error_alert_dialog_wrong_confirm_password_text);
                break;
            case INTERNET_CONNECTION:
                title.setText(R.string.error_alert_dialog_internet_connection_title);
                text.setText(R.string.error_alert_dialog_internet_connection_text);
                break;
            case EMAIL_ALREADY_EXIST:
                title.setText(R.string.error_alert_dialog_email_already_exist_title);
                text.setText(R.string.error_alert_dialog_email_already_exist_text);
                break;
            case WRONG_EMAIL_OR_PASSWORD:
                title.setText(R.string.error_alert_dialog_wrong_email_or_password_title);
                text.setText(R.string.error_alert_dialog_wrong_email_or_password_text);
                break;
            case EMPTY_FIELD:
                title.setText(R.string.error_alert_dialog_empty_field_title);
                text.setText(R.string.error_alert_dialog_empty_field_text);
                break;
            case SOMETHING_WRONG:
                title.setText(R.string.error_alert_dialog_something_wrong_title);
                text.setText(R.string.error_alert_dialog_something_wrong_text);
                break;

        }
        builder.setView(view);
        return builder.create();
    }
    public static boolean isIsExist() {
        return isExist.get();
    }
}