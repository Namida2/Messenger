package com.example.messenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;

import com.example.messenger.interfaces.MessagesObservable;
import com.example.messenger.interfaces.UserInterface;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import messengerFragment.models.MessengerFragmentModel;

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

public class MessagesListenerService extends Service implements MessagesObservable.Observable {

    private int id = 0;
    private static final String channelId = "MessengerId";
    private static MessagesListenerService service;
    private NotificationManager notificationManager;
    private static Consumer<Boolean> onCrateConsumer;
    private static final String channelName = "MessengerChannel";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final AtomicBoolean isExist = new AtomicBoolean(false);
    private static final ArrayList<MessagesObservable.Subscriber> subscribers = new ArrayList<>();

    private ArrayList<Chat> chats = new MessengerFragmentModel().getChats();
    private ListenerRegistration registration;
    private Disposable disposable;

    public class Pair {
        public Chat chat;
        public Long messagesInChat;
        public Pair (Chat chat, Long messagesInChat) {
            this.chat = chat;
            this.messagesInChat = messagesInChat;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        service = this;
        isExist.set(true);
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        createChannel(notificationManager);
        try {
            onCrateConsumer.accept(true);
        } catch (Exception e) {
            Log.d(TAG, "MessagesListenerService: " + e.getMessage());
        }
    }

    public void startMessagesListening(ArrayList<Chat> chats) {
        this.chats = chats;
        disposable = getMessagesObservable(chats)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pair -> {
                readMessages(pair.chat, pair.messagesInChat);
            }, error -> {
                Log.d(TAG, "MessagesListenerService.onCreate: " + error.getMessage());
            }, () -> {});
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public Observable<Pair> getMessagesObservable(ArrayList<Chat> chats) {
        return Observable.create(emitter -> {
            for (Chat chat : chats) {
                db.collection(COLLECTION_CHATS)
                    .document(chat.getChatId())
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            isExist.set(false);
                            unSubscribeFromDatabase();
                            stopSelf();
                            Log.d(TAG, "MessagesListenerService.startMessagesListening: " + error.getMessage());
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Long messagesInChat = (Long) snapshot.getData().get(FIELD_MESSAGES_IN_CHAT);
                            if (chat.getMessagesInChat() - messagesInChat < 0) {
                                emitter.onNext(new Pair(chat, messagesInChat));
                            }
                        }
                    });
            }
        });
    }
    private void unSubscribeFromDatabase() {
        try {
            registration.remove();
            disposable.dispose();
        } catch (Exception e) {
            Log.d(TAG, "unSubscribeFromDatabase: " + e.getMessage() );
        }
    }

    @Override
    public void startChatsListening(User user) {
        registration = db.collection(COLLECTION_USERS)
            .document(user.getEmail())
            .collection(COLLECTION_MESSENGER)
            .document(DOCUMENT_CHATS)
            .addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    isExist.set(false);
                    unSubscribeFromDatabase();
                    stopSelf();
                    Log.d(TAG, "MessagesListenerService.tartChatsListening: " + error.getMessage());
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    List<String> chatIds = (List<String>) snapshot.getData().get(FIELD_CHATS_IDS);
                    if(user.getMyChatIds() == null) {
                        user.setChatIds(new ArrayList<>(chatIds));
                        return;
                    }
                    Set<String> newChatIds = new HashSet<>(chatIds);
                    if(newChatIds.size() < user.getMyChatIds().size()) {
                        newChatIds = new HashSet<>(user.getMyChatIds());
                        newChatIds.removeAll(chatIds);
                        if(!newChatIds.iterator().hasNext()) return;
                        String next = newChatIds.iterator().next();
                        user.getMyChatIds().remove(next);
                        notifySubscribersChatDeleted(next);
                    } else {
                        newChatIds.removeAll(user.getMyChatIds());

                        if(!newChatIds.iterator().hasNext()) return;

                        String next = newChatIds.iterator().next();
                        if (newChatIds.iterator().hasNext())
                            readChat(next);
                        if(!user.getMyChatIds().contains(next)) {
                            user.getMyChatIds().add(next);
                        }
                    }
                }
            });
    }

    private void readChat(String chatId) {
        db.runTransaction(transaction -> {
            DocumentReference docRefChat = db.collection(COLLECTION_CHATS).document(chatId);

            DocumentSnapshot docSnapshotChat = transaction.get(docRefChat);
            Chat chat = new Chat();
            chat.setChatId(docSnapshotChat.getId());
            chat.setType( (String) docSnapshotChat.getData().get(FIELD_TYPE));
            chat.setChatName( (String) docSnapshotChat.getData().get(FIELD_CHAT_NAME));
            chat.setMessagesInChat( (Long) docSnapshotChat.getData().get(FIELD_MESSAGES_IN_CHAT));
            chat.setLastMessageAt( (String) docSnapshotChat.getData().get(FIELD_LAST_MESSAGE_AT));

            List<String> usersInChat = (List<String>) docSnapshotChat.getData().get(FIELD_USERS);
            for(String userName : usersInChat) {
                DocumentReference docRefUser = db.collection(COLLECTION_USERS).document(userName);
                User user = transaction.get(docRefUser).toObject(User.class);
                user.setAvatar( fromBase64(user.getAvatarString()) );
                chat.getUsers().add(user);
            }
            if(!chats.contains(chat)) {
                chats.add(chat);
            }
            readMessages(chat);
            return chat;
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {

            } else {
                Log.d(TAG, "MessagesListenerService.readChat: " + task.getException());
            }
        });
    }

    private void readMessages(Chat chat) {
        db.collection(COLLECTION_CHATS)
            .document(chat.getChatId()).collection(COLLECTION_MESSAGES)
            .get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Message message = queryDocumentSnapshot.toObject(Message.class);
                        message.setId( Long.parseLong(queryDocumentSnapshot.getId()) );
                        chat.getMessages().add(message);
                    }
                    Collections.sort(chat.getMessages(),
                        (message1, message2) -> (int) (message1.getId() - message2.getId()));
                    if(chat.getMessages().size() == 0) return;
                    Message message = chat.getMessages().get(chat.getMessages().size()-1);
                    notifySubscribers(chat);
                    startMessagesListening(chats);
                    if(!message.getAuthorEmail().equals(User.getCurrentUser().getEmail())) {
                        Bitmap bitmap = null;
                        for(UserInterface user : chat.getUsers()) {
                            if(message.getAuthorEmail().equals(user.getEmail())){
                                bitmap = user.getAvatar();
                            }
                        }
                        showNotification(message.getAuthorName(), message.getMessage(), bitmap);
                    }
                } else {
                    Log.d(TAG, "MessagesListenerService.readChat: " + task.getException());
                }
            });
    }

    private void readMessages(Chat chat, Long messagesInChat) {
        AtomicInteger addSize = new AtomicInteger();
        db.runTransaction(transaction -> {
            for(Long i = chat.getMessagesInChat() + 1; i <= messagesInChat; ++i) {
                DocumentReference docRefMessage = db.collection(COLLECTION_CHATS)
                    .document(chat.getChatId())
                    .collection(COLLECTION_MESSAGES)
                    .document(Long.toString(i));
                Message message = transaction.get(docRefMessage).toObject(Message.class);
                if(!message.getAuthorEmail().equals(User.getCurrentUser().getEmail()) && !chat.getMessages().contains(message)) {
                    Bitmap bitmap = null;
                    for(UserInterface user : chat.getUsers()) {
                        if(message.getAuthorEmail().equals(user.getEmail())){
                            bitmap = user.getAvatar();
                        }
                    }
                    showNotification(message.getAuthorName(), message.getMessage(), bitmap);
                }
                if(!chat.getMessages().contains(message)) {
                    chat.getMessages().add( message );
                    addSize.incrementAndGet();
                }
            }
            return true;
        }).addOnCompleteListener(task -> {
            chat.setMessagesInChat(chat.getMessagesInChat() + addSize.get());
            if(task.isSuccessful()) notifySubscribers(chat);
            else Log.d(TAG, "MessagesListenerService.readMessages: " + task.getException());
        });

    }
    @Override
    public void subscribe(MessagesObservable.Subscriber subscriber) {
        for(int i = 0; i < subscribers.size(); ++i) {
            MessagesObservable.Subscriber mySubscriber = subscribers.get(i);
            if (mySubscriber.getClass() == subscriber.getClass()) {
                subscribers.remove(i);
                break;
            }
        }
        subscribers.add(subscriber);
    }
    public void showNotification(String title, String name, Bitmap icon) {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_email)
            .setLargeIcon(icon)
            .setColor(getResources().getColor(R.color.fui_transparent))
            .setContentTitle(title)
            .setContentText(name)
            //.setGroup(group)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build();
        notificationManager.notify(id++, notification); //important thing
    }
    private void createChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void unSubscribe(MessagesObservable.Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(Chat chat) {
        for(MessagesObservable.Subscriber subscriber : subscribers) {
            subscriber.notifyMe(chat);
        }
    }

    @Override
    public void notifySubscribersChatDeleted(String chatId) {
        for(MessagesObservable.Subscriber subscriber : subscribers) {
            subscriber.notifyChatDeleted(chatId);
        }
    }

    public static MessagesListenerService getService() {
        return service;
    }

    public static void setOnCrateConsumer(Consumer<Boolean> onCreateConsumer) {
        MessagesListenerService.onCrateConsumer = onCreateConsumer;
    }

    public static Boolean isExits() {
        return isExist.get();
    }
}
