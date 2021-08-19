package com.example.splitmonk.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitmonk.R;
import com.example.splitmonk.models.Event_User;
import com.example.splitmonk.models.Group;
import com.example.splitmonk.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EventUserAdapter extends RecyclerView.Adapter<EventUserAdapter.ViewHolder> {

    Context context;
    Activity activity;
    ArrayList<User> data = new ArrayList<>();
    ArrayList<Event_User> selected_users = new ArrayList<>();

    public EventUserAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.user_name.setText(data.get(position).getName());

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.user_amount.equals("")){
                    Toast.makeText(context, "Please enter some amount", Toast.LENGTH_SHORT).show();
                }else{
                    User curr_user = data.get(position);
                    Event_User curr = new Event_User(curr_user.getName(), curr_user.getUpi(), holder.user_amount.getText().toString(),
                            "0", curr_user.getUid());
                    ContainsUser(curr);
                    selected_users.add(curr);
                }
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.user_amount.equals("")){
                    Toast.makeText(context, "Please enter some amount", Toast.LENGTH_SHORT).show();
                }else{
                    User curr_user = data.get(position);
                    Event_User curr = new Event_User(curr_user.getName(), curr_user.getUpi(), holder.user_amount.getText().toString(),
                            "0", curr_user.getUid());
                    ContainsUser(curr);
                }
            }
        });
    }

    private boolean ContainsUser(Event_User curr) {
        for(int i = 0; i < selected_users.size(); i++){
            if(selected_users.get(i).getUser_id().equals(curr.getUser_id())){
                selected_users.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView user_name, user_amount;
        Button add, remove;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_amount = itemView.findViewById(R.id.user_amount);
            add = itemView.findViewById(R.id.btn_add);
            remove = itemView.findViewById(R.id.btn_remove);
        }
    }

    public void setData(ArrayList<User> da){
        this.data = da;
        notifyDataSetChanged();
    }

    public ArrayList<Event_User> getSelected_users(){
        return selected_users;
    }

}
