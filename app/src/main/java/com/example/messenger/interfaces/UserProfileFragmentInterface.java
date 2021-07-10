package com.example.messenger.interfaces;

public interface UserProfileFragmentInterface {
    interface Model {}
    interface View {
        void startChatActivity(int position);
    }
    interface Presenter {
        UserInterface getChosenUser();
        void addChat(UserInterface user);
    }
}
