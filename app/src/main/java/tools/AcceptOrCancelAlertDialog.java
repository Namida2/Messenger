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

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class AcceptOrCancelAlertDialog extends DialogFragment {

    private Consumer<Object> accept;
    private String title;
    private String text;

    public AcceptOrCancelAlertDialog(Consumer<Object> accept, String title, String text) {
        this.accept = accept;
        this.title = title;
        this.text = text;
    }
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alertDialogStyle);
        View contentView = View.inflate(getContext(), R.layout.dialog_accept_or_cancel, null);
        builder.setView(contentView);
        Button cancel = contentView.findViewById(R.id.cancel);
        Button accept = contentView.findViewById(R.id.accept);
        if(title != null)
            ((TextView)contentView.findViewById(R.id.title)).setText(title);
        if(text != null)
            ((TextView)contentView.findViewById(R.id.text)).setText(text);

        RxView.clicks(cancel)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                if(text != null ) this.accept.accept(false);
                dismiss();
            });
        RxView.clicks(accept)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                if(text != null ) this.accept.accept(true);
                else this.accept.accept(title);
                dismiss();
            });
        return builder.create();
    }
}