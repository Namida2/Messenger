package adapters;

import android.annotation.SuppressLint;
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

import com.example.messenger.R;
import com.example.messenger.User;
import com.example.messenger.interfaces.UserInterface;
import com.jakewharton.rxbinding4.view.RxView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import static tools.Const.TAG;
import static tools.Const.USER_FIELDS_DELIMITER;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private ArrayList<UserInterface> usersList = new ArrayList<>();
    private Consumer<UserInterface> acceptUserConsumer;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout container;
        private TextView name;
        private TextView fields;
        private ImageView avatar;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar_image_view);
            name = itemView.findViewById(R.id.user_name_text_view);
            fields = itemView.findViewById(R.id.user_fields_text_view);
            container = itemView.findViewById(R.id.user_container_constraint_layout);
        }
    }
    public void setAcceptUserConsumer(Consumer<UserInterface> acceptUserConsumer) {
        this.acceptUserConsumer = acceptUserConsumer;
    }
    public void setUsersList(ArrayList<UserInterface> usersList) {
        this.usersList = usersList;
        this.notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_user, parent, false);
        return new ViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.avatar.setImageBitmap(usersList.get(position).getAvatar());
        holder.avatar.setClipToOutline(true);
        holder.avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.name.setText(usersList.get(position).getName());
        holder.fields.setText( usersList.get(position).getCity()
            + USER_FIELDS_DELIMITER + usersList.get(position).getSex()
            + USER_FIELDS_DELIMITER + usersList.get(position).getAge());
        RxView.clicks(holder.container)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(unit -> {
                acceptUserConsumer.accept(usersList.get(position));
            }, error -> {
                Log.d(TAG, "UsersRecyclerViewAdapter.onBindViewHolder: " + error.getMessage());
            }, () -> {});
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
