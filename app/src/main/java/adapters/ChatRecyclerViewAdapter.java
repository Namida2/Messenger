package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.R;
import com.example.messenger.User;

import org.jetbrains.annotations.NotNull;

import tools.Animations;


public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private Chat chat;

    public ChatRecyclerViewAdapter (Chat chat) {
        this.chat = chat;
    }

    public void clearChat() {
        this.chat = new Chat();
        this.notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout container;
        public TextView message;
        public TextView time;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text_view);
            time = itemView.findViewById(R.id.message_time_text_view);
            container = itemView.findViewById(R.id.container_relative_layout);
        }
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case 0: view = layoutInflater.inflate(R.layout.layout_message_left, parent, false); break;
            case 1: view = layoutInflater.inflate(R.layout.layout_message_right, parent, false); break;
        }
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.message.setText(chat.getMessages().get(position).getMessage());
        holder.time.setText(chat.getMessages().get(position).getTime());
    }
    @Override
    public int getItemCount() {
        return chat.getMessages().size();
    }

    @Override
    public int getItemViewType(int position) {
        String currentMessageEmail = chat.getMessages().get(position).getAuthorEmail();
        return User.getCurrentUser().getEmail().equals(currentMessageEmail) ? 1 : 0;
    }
}
