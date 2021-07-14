package messengerFragment.presenters;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.Message;
import com.example.messenger.MessagesListenerService;
import com.example.messenger.User;
import com.example.messenger.interfaces.BaseInterface;
import com.example.messenger.interfaces.MessagesObservable;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.MessengerRecyclerViewAdapter;
import messengerFragment.interfaces.MessengerFragmentInterface;
import messengerFragment.models.MessengerFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Base64.fromBase64;
import static tools.Const.CollectionChats.COLLECTION_CHATS;
import static tools.Const.CollectionChats.FIELD_CHAT_NAME;
import static tools.Const.CollectionChats.FIELD_LAST_MESSAGE_AT;
import static tools.Const.CollectionChats.FIELD_MESSAGES_IN_CHAT;
import static tools.Const.CollectionChats.FIELD_TYPE;
import static tools.Const.CollectionChats.FIELD_USERS;
import static tools.Const.CollectionMessages.COLLECTION_MESSAGES;
import static tools.Const.CollectionUsers.COLLECTION_MESSENGER;
import static tools.Const.CollectionUsers.COLLECTION_USERS;
import static tools.Const.CollectionUsers.DOCUMENT_CHATS;
import static tools.Const.CollectionUsers.FIELD_CHATS_IDS;
import static tools.Const.TAG;

public class MessengerFragmentPresenter implements MessengerFragmentInterface.Presenter, MessagesObservable.Subscriber {

    private final BaseInterface baseView;
    private final MessengerFragmentInterface.View view;
    private static MessengerFragmentInterface.Model model;

    public MessengerFragmentPresenter (BaseInterface baseView, MessengerFragmentInterface.View view, boolean newUser) {
        this.view = view;
        this.baseView = baseView;
        if(model == null || newUser) {
            model = new MessengerFragmentModel();
            model.setChats(new ArrayList<>());
            setModelState(User.getCurrentUser());
        }
        MessagesListenerService.getService().subscribe(this);
    }

    @Override
    public void setModelState(UserInterface user) {
        model.getDatabase()
            .collection(COLLECTION_USERS)
            .document(user.getEmail())
            .collection(COLLECTION_MESSENGER)
            .document(DOCUMENT_CHATS)
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    List<String> chatsIds = (List<String>) data.get(FIELD_CHATS_IDS);
                    readChats(chatsIds);
                } else {
                    Log.d(TAG, "MessengerFragmentPresenter.setModelState: " + task.getException());
                    baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
        });
    }

    @Override
    public void removeChatFromDatabase(int position) {

        try {
            model.getChats().get(position);
        } catch (Exception e) {
            return;
        }

        DocumentReference docRefChat = model.getDatabase()
            .collection(COLLECTION_CHATS)
            .document( model.getChats().get(position).getChatId() );

        CollectionReference collRefMessages = model.getDatabase()
            .collection(COLLECTION_CHATS)
            .document( model.getChats().get(position).getChatId() )
            .collection(COLLECTION_MESSAGES);

        collRefMessages.get().addOnCompleteListener(taskMessages -> {
                if(taskMessages.isSuccessful()) {

                    model.getDatabase().runTransaction(transaction -> {

                        ArrayList<List<String>> usersChatsIds = new ArrayList<>();

                        for(UserInterface user : model.getChats().get(position).getUsers()) {
                            DocumentReference docRefUser = model.getDatabase()
                                .collection(COLLECTION_USERS)
                                .document(user.getEmail())
                                .collection(COLLECTION_MESSENGER)
                                .document(DOCUMENT_CHATS);
                            List<String> chatsIds = (List<String>) transaction.get(docRefUser).getData().get(FIELD_CHATS_IDS);
                            usersChatsIds.add(chatsIds);
                        }

                        for(int i = 0; i < model.getChats().get(position).getUsers().size(); ++i ) {
                            UserInterface user = model.getChats().get(position).getUsers().get(i);
                            DocumentReference docRefUser = model.getDatabase()
                                .collection(COLLECTION_USERS)
                                .document(user.getEmail())
                                .collection(COLLECTION_MESSENGER)
                                .document(DOCUMENT_CHATS);
                            usersChatsIds.get(i).remove(model.getChats().get(position).getChatId());
                            Map<String, Object> data = new HashMap<>();
                            data.put(FIELD_CHATS_IDS, usersChatsIds.get(i));
                            transaction.set(docRefUser, data);
                            if(user.getEmail().equals(User.getCurrentUser().getEmail())) {
                                User.getCurrentUser().setChatIds(usersChatsIds.get(i));
                            }
                        }

                        ArrayList<DocumentSnapshot> documentSnapshots = new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot : taskMessages.getResult()){
                            documentSnapshots.add(documentSnapshot);
                            transaction.delete(collRefMessages.document(documentSnapshot.getId()));
                        }
                        transaction.delete(docRefChat);

                        return true;
                    }).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            try {
                                model.getChats().remove(position);
                            } catch (Exception e) {
                                Log.d(TAG, "MessengerFragmentPresenter.removeChatFromDatabase: " + e.getMessage());
                            }
                            baseView.onSuccess();
                        } else {
                            Log.d(TAG, "MessengerFragmentPresenter.removeChatFromDatabase: " + task.getException());
                            baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
                        }
                    });


                } else {
                    Log.d(TAG, "MessengerFragmentPresenter.removeChatFromDatabase: " + taskMessages.getException());
                    baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
                }
        });


    }

    private void readChats(List<String> chatsIds) {
        if(chatsIds == null || chatsIds.size() == 0) {
            baseView.onSuccess();
            return;
        }
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
                    user.setAvatar( fromBase64(user.getAvatarString()) );

                    chat.getUsers().add(user);
                }
                User.getCurrentUser().setChatIds(chatsIds);
                model.getChats().add(chat);
            }
            return true;
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                readMessages(chatsIds);
            } else {
                Log.d(TAG, "MessengerFragmentPresenter.readChats: " + task.getException());
                baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
            }
        });
    }

    private void readMessages(List<String> chatsIds) {
        String chatId;
        for(int i = 0; i < chatsIds.size(); ++i) {
            chatId = chatsIds.get(i);
            int position = i;
            int finalI = i;
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

                        if(finalI == chatsIds.size()-1) baseView.onSuccess();

                    } else {
                        Log.d(TAG, "MessengerFragmentPresenter.readMessages: " + task.getException());
                        baseView.onError(ErrorAlertDialog.SOMETHING_WRONG);
                    }
            });
        }
    }
    @Override
    public View getView(MessengerFragmentInterface.View view) {
        if(model.getView() == null) return null;
        model.setAdapter( new MessengerRecyclerViewAdapter(model.getChats()) );
        model.getRecyclerView().setAdapter(model.getAdapter());
        model.getAdapter().setAcceptChatConsumer(this.view::startChatActivity);
        model.getAdapter().setDeleteChatConsumer(position -> {
            try {
                String name = "";
                for(UserInterface user : model.getChats().get(position).getUsers()) {
                    if(!user.getEmail().equals(User.getCurrentUser().getEmail()))
                        name = user.getName();
                }
                view.showAcceptOrCancelDialog(position, name);
            } catch (Exception e) {
                Log.d(TAG, "MessengerFragmentPresenter.onResume: " + e.getMessage());
            }
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
        model.getAdapter().setAcceptChatConsumer(view::startChatActivity);
        model.getAdapter().setDeleteChatConsumer(position -> {
            try {
                String name = "";
                for(UserInterface user : model.getChats().get(position).getUsers()) {
                    if(!user.getEmail().equals(User.getCurrentUser().getEmail()))
                        name = user.getName();
                }
                view.showAcceptOrCancelDialog(position, name);
            } catch (Exception e) {
                Log.d(TAG, "MessengerFragmentPresenter.onResume: " + e.getMessage());
            }
        });
        model.getAdapter().notifyDataSetChanged();
    }
    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        model.setRecyclerView(recyclerView);
    }

    @Override
    public void notifyMe(Chat chat) {
        String name = Thread.currentThread().getName();
        if (model.getAdapter() == null) return;
        model.getAdapter()
            .notifyItemChanged(model.getChats().indexOf(chat));

    }

    @Override
    public void notifyChatDeleted(String chatId) {
        try {
            for(Chat chat : model.getChats()) {
                if(chat.getChatId().equals(chatId)) {
                    model.getChats().remove(chat);
                    model.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "MessengerFragmentPresenter.notifyChatDeleted: " + e.getMessage());
        }
    }
}
