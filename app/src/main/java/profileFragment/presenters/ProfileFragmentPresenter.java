package profileFragment.presenters;

import com.example.messenger.User;

import profileFragment.interfaces.ProfileFragmentIInterface;
import profileFragment.models.ProfileFragmentModel;

public class ProfileFragmentPresenter implements ProfileFragmentIInterface.Presenter {

    private ProfileFragmentIInterface.View view;
    private static ProfileFragmentIInterface.Model model;

    public ProfileFragmentPresenter(ProfileFragmentIInterface.View view) {
        this.view = view;
        if(model == null) {
            model = new ProfileFragmentModel();
        }
    }

    @Override
    public void acceptChanges() {
        User user = User.getCurrentUser();

    }
}
