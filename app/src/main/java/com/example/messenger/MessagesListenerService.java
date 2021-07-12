package com.example.messenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;

import com.example.messenger.interfaces.MessagesObservable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import messengerFragment.models.MessengerFragmentModel;
import tools.ErrorAlertDialog;

import static tools.Const.CollectionChats.COLLECTION_CHATS;
import static tools.Const.CollectionChats.FIELD_LAST_MESSAGE_AT;
import static tools.Const.CollectionChats.FIELD_MESSAGES_IN_CHAT;
import static tools.Const.CollectionMessages.COLLECTION_MESSAGES;
import static tools.Const.TAG;

public class MessagesListenerService extends Service implements MessagesObservable.Observable {

    private static final String channelId = "MessengerId";
    private static final String channelName = "MessengerChannel";
    private NotificationManager notificationManager;
    private static final ArrayList<MessagesObservable.Subscriber> subscribers = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static MessagesListenerService service;
    private static final AtomicBoolean firstCall = new AtomicBoolean(true);
    private static Consumer<Boolean> onCrateConsumer;
    private int id = 0;


    private final ArrayList<Chat> chats = new MessengerFragmentModel().getChats();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        service = this;
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        createChannel(notificationManager);
        startDocumentListening()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(messagesInChat -> {

            })
        onCrateConsumer.accept(true);
    }

    public static void setOnCrateConsumer(Consumer<Boolean> onCreateConsumer) {
        MessagesListenerService.onCrateConsumer = onCreateConsumer;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public Observable<Long> startDocumentListening() {
        return Observable.create(emitter -> {
            for (Chat chat : chats) {
                db.collection(COLLECTION_CHATS)
                    .document(chat.getChatId())
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null) {
                            Log.d(TAG, "startDocumentListening: " + error.getMessage());
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Long messagesInChat = (Long) snapshot.getData().get(FIELD_MESSAGES_IN_CHAT);
                            if (chat.getMessages().size() - messagesInChat < 0) {
                                emitter.onNext(messagesInChat);
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    });
            }
        });
    }
    private void readMessages(Chat chat, Long messagesInChat) {
        db.runTransaction(transaction -> {
            for(int i = chat.getMessages().size() + 1; i <= messagesInChat; ++i) {
                DocumentReference docRefMessage = db.collection(COLLECTION_CHATS)
                    .document(chat.getChatId())
                    .collection(COLLECTION_MESSAGES)
                    .document(Integer.toString(i));
                Message message = transaction.get(docRefMessage).toObject(Message.class);
                chat.getMessages().add( message );
                notifySubscribers(chat);
                showNotification(message.getAuthorName(), message.getMessage());

            }
            return true;
        }).addOnCompleteListener(task -> {

        });

    }
    public void showNotification(String title, String name) {
        //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_email)
            //.setLargeIcon(icon)
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

    public static MessagesListenerService getService() {
        return service;
    }
}
