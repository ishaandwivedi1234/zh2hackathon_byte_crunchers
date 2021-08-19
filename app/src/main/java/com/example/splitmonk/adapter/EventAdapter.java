package com.example.splitmonk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitmonk.Activities.EventDetailsActivity;
import com.example.splitmonk.R;
import com.example.splitmonk.models.Event;
import com.example.splitmonk.models.Event_User;
import com.example.splitmonk.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.paperdb.Paper;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    Context context;
    Activity activity;
    ArrayList<Event> data = new ArrayList<>();

    public EventAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.name.setText(data.get(position).getEvent_name());
        holder.amount.setText("Total: " + data.get(position).getEvent_total_amount() + "/-");
        holder.members.setText(data.get(position).getEvent_users().size() + " members participated");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, EventDetailsActivity.class);
                Paper.init(context);
                Paper.book().write("event", data.get(position));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, members, amount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_name);
            members = itemView.findViewById(R.id.event_members);
            amount = itemView.findViewById(R.id.event_amount);
        }
    }

    public void setData(ArrayList<Event> da){
        this.data = da;
        notifyDataSetChanged();
    }
}
