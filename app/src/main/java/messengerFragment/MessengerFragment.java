package messengerFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.messenger.Chat;
import com.example.messenger.ChatActivity;
import com.example.messenger.R;
import com.example.messenger.interfaces.BaseInterface;

import org.jetbrains.annotations.NotNull;

import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.presenters.MessengerFragmentPresenter;
import tools.AcceptOrCancelAlertDialog;
import tools.ErrorAlertDialog;

import static tools.Const.EXTRA_TAG_POSITION;
import static tools.Const.TAG;

public class MessengerFragment extends Fragment implements MessengerFragmentInterface.View, BaseInterface {

    private MessengerFragmentInterface.Presenter presenter;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MessengerFragmentPresenter(this,this, false);
        View contentView = presenter.getView(this);
        if(contentView != null) return;
        contentView = View.inflate(getContext(), R.layout.fragment_messenger, null);
        presenter.setRecyclerView(contentView.findViewById(R.id.chats_recycler_view));
        presenter.setView(contentView);
    }
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return presenter.getView(this);
    }
    @Override
    public void startChatActivity(int position) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(EXTRA_TAG_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void showAcceptOrCancelDialog(int position, String title) {
        new AcceptOrCancelAlertDialog(accept -> {
            presenter.removeChatFromDatabase(position);
        }, title, null).show(getActivity().getSupportFragmentManager(), "");
    }


    @Override
    public void onSuccess() {
        try {
            presenter.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "MessengerFragment.onSuccess: " + e.getMessage());
        }
    }
    @Override
    public void onError(int errorCode) {
        if(!ErrorAlertDialog.isIsExist() && getActivity() != null)
            ErrorAlertDialog.getInstance(errorCode)
                .show(getActivity().getSupportFragmentManager(), "");
    }
    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }
}
