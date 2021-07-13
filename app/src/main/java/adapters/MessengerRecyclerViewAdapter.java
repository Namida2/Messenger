package adapters;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.R;
import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import static tools.Const.CollectionChats.TYPE_DIALOG;
import static tools.Const.TAG;

public class MessengerRecyclerViewAdapter extends RecyclerView.Adapter<MessengerRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Chat> chats;
    private Consumer<Integer> acceptChatConsumer;

    public MessengerRecyclerViewAdapter (ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public void setAcceptChatConsumer(Consumer<Integer> acceptChatConsumer) {
        this.acceptChatConsumer = acceptChatConsumer;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView chatName;
        public TextView lastMessage;
        public TextView lastMessageAt;
        public ConstraintLayout container;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_image_view);
            chatName = itemView.findViewById(R.id.user_name_text_view);
            lastMessage = itemView.findViewById(R.id.last_message_text_view);
            lastMessageAt = itemView.findViewById(R.id.last_message_at_text_view);
            container = itemView.findViewById(R.id.chat_item_container_constraint_layout);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_chat, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.avatar.setClipToOutline(true);
        if(chats.get(position).getType().equals(TYPE_DIALOG)) {
            for(UserInterface user : chats.get(position).getUsers())
                if (!user.getEmail().equals(User.getCurrentUser().getEmail())) {
                    holder.chatName.setText(user.getName());
                    holder.avatar.setImageBitmap(user.getAvatar());
                    holder.avatar.setClipToOutline(true);
                    holder.avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
        }
        RxView.clicks(holder.container)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                acceptChatConsumer.accept(position);
            }, error -> {
                Log.d(TAG, "MessengerRecyclerViewAdapter.onBindViewHolder: " + error.getMessage());
            }, () -> {});
        if( chats.get(position).getMessages().size() == 0) return;
        holder.lastMessage.setText( chats.get(position).getMessages().get(
            chats.get(position).getMessages().size()-1
        ).getMessage() ); // add sort
        holder.lastMessageAt.setText( chats.get(position).getMessages().get(
            chats.get(position).getMessages().size()-1
        ).getTime() ); // add sort
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
}
