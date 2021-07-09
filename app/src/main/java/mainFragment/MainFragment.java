package mainFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.R;

import org.jetbrains.annotations.NotNull;

import mainFragment.interfaces.MainFragmentInterface;
import mainFragment.presenters.MainFragmentPresenter;
import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.presenters.MessengerFragmentPresenter;
import tools.ErrorAlertDialog;

public class MainFragment extends Fragment implements MainFragmentInterface.View {

    private MainFragmentInterface.Presenter presenter;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainFragmentPresenter(this);
        presenter.setModelState();
        View contentView = presenter.getView();
        if (contentView != null) return;
        contentView = View.inflate(getContext(), R.layout.fragment_main, null);
        presenter.setRecyclerView(contentView.findViewById(R.id.users_recycler_view));
        presenter.setView(contentView);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return presenter.getView();
    }
    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist() && getActivity() != null)
            ErrorAlertDialog.getInstance(errorCode)
                .show(getActivity().getSupportFragmentManager(), "");
    }
}
