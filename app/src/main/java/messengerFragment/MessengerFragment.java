package messengerFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;

import org.jetbrains.annotations.NotNull;

import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.presenters.MessengerFragmentPresenter;
import tools.ErrorAlertDialog;

public class MessengerFragment extends Fragment implements MessengerFragmentInterface.View {

    private MessengerFragmentInterface.Presenter presenter;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MessengerFragmentPresenter(this);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View contentView = presenter.getView();
        if(contentView != null) return contentView;

        contentView = View.inflate(container.getContext(), R.layout.fragment_messenger, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.users_recycler_view);

        return contentView;
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist() && getActivity() != null)
            ErrorAlertDialog.getInstance(errorCode)
                .show(getActivity().getSupportFragmentManager(), "");
    }
}
