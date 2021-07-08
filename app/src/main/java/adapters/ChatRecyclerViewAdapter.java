package adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Chat;
import com.example.messenger.User;

import org.jetbrains.annotations.NotNull;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private final Chat chat;

    public ChatRecyclerViewAdapter (Chat chat) {
        this.chat = chat;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

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
