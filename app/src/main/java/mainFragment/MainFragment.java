package mainFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.interfaces.BaseInterface;
import com.example.messenger.interfaces.UserInterface;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxTextView;

import org.jetbrains.annotations.NotNull;

import mainFragment.interfaces.MainFragmentInterface;
import mainFragment.presenters.MainFragmentPresenter;
import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.presenters.MessengerFragmentPresenter;
import tools.ErrorAlertDialog;

public class MainFragment extends Fragment implements MainFragmentInterface.View, BaseInterface {

    private MainFragmentInterface.Presenter presenter;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainFragmentPresenter(this, this, false);
        View contentView = presenter.getView();
        if (contentView != null) return;
        contentView = View.inflate(getContext(), R.layout.fragment_main, null);
        presenter.setRecyclerView(contentView.findViewById(R.id.users_recycler_view));

        ( (EditText) contentView.findViewById(R.id.user_name_edit_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.showUsers(s.toString());
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        presenter.setView(contentView);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return presenter.getView();
    }

    @Override
    public void startUserProfileFragment(UserInterface user) {
        if (getActivity() == null) return;
        ( (MainActivity) getActivity()).showProfileFragment(user);
    }

    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist() && getActivity() != null)
            ErrorAlertDialog.getInstance(errorCode)
                .show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }
}
