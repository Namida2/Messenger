package mainFragment.presenters;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.User;
import com.example.messenger.interfaces.BaseInterface;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import adapters.UsersRecyclerViewAdapter;
import mainFragment.interfaces.MainFragmentInterface;
import mainFragment.models.MainFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Base64.fromBase64;
import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.TAG;

public class MainFragmentPresenter implements MainFragmentInterface.Presenter {

    private MainFragmentInterface.View view;
    private BaseInterface baseView;
    private static MainFragmentInterface.Model model;

    public MainFragmentPresenter (BaseInterface baseView, MainFragmentInterface.View view, boolean newUser) {
        this.view = view;
        if(model == null || newUser) {
            model = new MainFragmentModel();
            model.setAdapter(new UsersRecyclerViewAdapter());
            onResume();
            setModelState();
        }
    }

    @Override
    public void showUsers(String name) {
        ArrayList<UserInterface> currentUsers = new ArrayList<>();
        for(UserInterface user : model.getUsersList()) {
            if(user.getName().contains(name))
                currentUsers.add(user);
        }
        model.getAdapter().setUsersList(currentUsers);
        model.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        if(model.getAdapter() == null) return;
        model.getAdapter().setAcceptUserConsumer(user -> {
            model.setChosenUser(user);
            view.startUserProfileFragment(user);
        });
    }
    @Override
    public void setModelState() {
        model.getDatabase()
            .collection(COLLECTION_USERS)
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        User user = queryDocumentSnapshot.toObject(User.class);
                        user.setAvatar(fromBase64(user.getAvatarString()));
                        model.getUsersList().add(user);
                    }
                    if (model.getRecyclerView() == null) return;
                    model.getRecyclerView().setAdapter(model.getAdapter());
                } else {
                    Log.d(TAG, "MessengerFragmentPresenter.readMessages: " + task.getException());
                    baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
        });
    }

    @Override
    public View getView() {
        return model.getView();
    }
    @Override
    public void setView(View view) {
        model.setView(view);
    }
    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(model.getAdapter());
        model.setRecyclerView(recyclerView);
    }
}
