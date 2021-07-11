package messengerFragment.presenters;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.Message;
import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import adapters.MessengerRecyclerViewAdapter;
import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.models.MessengerFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionChats.COLLECTION_CHATS;
import static tools.Const.CollectionChats.FIELD_CHAT_NAME;
import static tools.Const.CollectionChats.FIELD_LAST_MESSAGE_AT;
import static tools.Const.CollectionChats.FIELD_MESSAGES_IN_CHAT;
import static tools.Const.CollectionChats.FIELD_TYPE;
import static tools.Const.CollectionChats.FIELD_USERS;
import static tools.Const.CollectionMessages.COLLECTION_MESSAGES;
import static tools.Const.CollectionUsers.COLLECTION_MESSENGER;
import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.CollectionUsers.FIELD_CHATS;
import static tools.Const.CollectionUsers.FIELD_CHATS_IDS;
import static tools.Const.TAG;

public class MessengerFragmentPresenter implements MessengerFragmentInterface.Presenter {

    private MessengerFragmentInterface.View view;
    private static MessengerFragmentInterface.Model model;

    public MessengerFragmentPresenter (MessengerFragmentInterface.View view) {
        this.view = view;
        if(model == null) {
            model = new MessengerFragmentModel();
            setModelState(User.getCurrentUser());
        }
    }

    @Override
    public void setModelState(UserInterface user) {
        User.getCurrentUser().setEmail("aa@aa.com"); // delete
        model.getDatabase()
            .collection(COLLECTION_USERS)
            .document("aa@aa.com")
            .collection(COLLECTION_MESSENGER)
            .document(FIELD_CHATS)
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    List<String> chatsIds = (List<String>) data.get(FIELD_CHATS_IDS);
                    readChats(chatsIds);
                } else {
                    Log.d(TAG, "MessengerFragmentPresenter.setModelState: " + task.getException());
                    view.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
        });
    }

    private void readChats(List<String> chatsIds) {
        model.getDatabase().runTransaction(transaction -> {
            for(String chatId : chatsIds) {
                DocumentReference docRefChat = model.getDatabase()
                    .collection(COLLECTION_CHATS)
                    .document(chatId);

                DocumentSnapshot docSnapshotChat = transaction.get(docRefChat);
                Chat chat = new Chat();
                chat.setChatId(docSnapshotChat.getId());
                chat.setType( (String) docSnapshotChat.getData().get(FIELD_TYPE));
                chat.setChatName( (String) docSnapshotChat.getData().get(FIELD_CHAT_NAME));
                chat.setMessagesInChat( (Long) docSnapshotChat.getData().get(FIELD_MESSAGES_IN_CHAT));
                chat.setLastMessageAt( (String) docSnapshotChat.getData().get(FIELD_LAST_MESSAGE_AT));

                List<String> usersInChat = (List<String>) docSnapshotChat.getData().get(FIELD_USERS);
                for(String userName : usersInChat) {
                    DocumentReference docRefUser = model.getDatabase()
                        .collection(COLLECTION_USERS)
                        .document(userName);
                    User user = transaction.get(docRefUser).toObject(User.class);
                    chat.getUsers().add(transaction.get(docRefUser).toObject(User.class));
                }

                model.getChats().add(chat);
            }
            return true;
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                readMessages(chatsIds);
            } else {
                Log.d(TAG, "MessengerFragmentPresenter.readChats: " + task.getException());
                view.onError(ErrorAlertDialog.SOMETHING_WRONG);
            }
        });
    }

    private void readMessages(List<String> chatsIds) {
        String chatId;
        for(int i = 0; i < chatsIds.size(); ++i) {
            chatId = chatsIds.get(i);
            int position = i;
            model.getDatabase()
                .collection(COLLECTION_CHATS)
                .document(chatId)
                .collection(COLLECTION_MESSAGES)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Message message = queryDocumentSnapshot.toObject(Message.class);
                            message.setId( Long.parseLong(queryDocumentSnapshot.getId()) );
                            model.getChats().get(position).getMessages().add(message);
                        }
                        ArrayList<Message> messages = model.getChats().get(position).getMessages();
                        Collections.sort(model.getChats().get(position).getMessages(),
                            (message1, message2) -> (int) (message1.getId() - message2.getId()));
                        messages = model.getChats().get(position).getMessages();
                        onSuccess();
                    } else {
                        Log.d(TAG, "MessengerFragmentPresenter.readMessages: " + task.getException());
                        view.onError(ErrorAlertDialog.SOMETHING_WRONG);
                    }
            });
        }
    }
    private void onSuccess() {
//        model.setAdapter( new MessengerRecyclerViewAdapter(model.getChats()) );
//        model.getRecyclerView().setAdapter(model.getAdapter());
//        model.getAdapter().setAcceptChatConsumer(position -> {
//            view.startChatActivity(position);
//        });
        view.onSuccess();
    }
    @Override
    public View getView() {
        model.setAdapter( new MessengerRecyclerViewAdapter(model.getChats()) );
        model.getRecyclerView().setAdapter(model.getAdapter());
        model.getAdapter().setAcceptChatConsumer(position -> {
            view.startChatActivity(position);
        });
        return model.getView();
    }
    @Override
    public MessengerRecyclerViewAdapter getAdapter() {
        return model.getAdapter();
    }
    @Override
    public void setView(View view) {
        model.setView(view);
    }
    @Override
    public void onResume() {
        if(model.getAdapter() == null) return;
        model.getAdapter().setAcceptChatConsumer(position -> {
            view.startChatActivity(position);
        });
    }
    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        model.setRecyclerView(recyclerView);
    }
}
