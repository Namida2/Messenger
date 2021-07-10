package mainFragment.presenters;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import adapters.UsersRecyclerViewAdapter;
import mainFragment.interfaces.MainFragmentInterface;
import mainFragment.models.MainFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.TAG;

public class MainFragmentPresenter implements MainFragmentInterface.Presenter {

    private MainFragmentInterface.View view;
    private static MainFragmentInterface.Model model;

    public MainFragmentPresenter (MainFragmentInterface.View view) {
        this.view = view;
        if(model == null) {
            model = new MainFragmentModel();
            model.setAdapter(new UsersRecyclerViewAdapter());
            onResume();
            setModelState();
        }
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
                        model.getUsersList().add( queryDocumentSnapshot.toObject(User.class) );
                    }
                    model.getAdapter().setUsersList(model.getUsersList());
                    if (model.getRecyclerView() == null) return;
                    model.getRecyclerView().setAdapter(model.getAdapter());
                } else {
                    Log.d(TAG, "MessengerFragmentPresenter.readMessages: " + task.getException());
                    view.onError(ErrorAlertDialog.SOMETHING_WRONG);
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
