package com.example.splitmonk.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitmonk.R;
import com.example.splitmonk.models.Event;
import com.example.splitmonk.models.Event_User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EventDetailsAdapter extends RecyclerView.Adapter<EventDetailsAdapter.ViewHolder>{

    Context context;
    Activity activity;
    ArrayList<Event_User> data = new ArrayList<>();

    public EventDetailsAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_details_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.usertopay.setText("Need to pay Rs " + String.valueOf(data.get(position).getUser_to_pay()));
        holder.username.setText(data.get(position).getUser_name());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, usertopay;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_name);
            usertopay = itemView.findViewById(R.id.user_to_pay);

        }
    }

    public void setData(ArrayList<Event_User> da){
        this.data = da;
        notifyDataSetChanged();
    }
}
