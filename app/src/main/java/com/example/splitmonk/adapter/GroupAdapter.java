package com.example.splitmonk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitmonk.Activities.GroupDashboardActivity;
import com.example.splitmonk.R;
import com.example.splitmonk.models.Group;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{

    Context context;
    Activity activity;
    ArrayList<Group> data = new ArrayList<>();

    public GroupAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.name.setText(data.get(position).getGroup_name());
        holder.active.setText(String.valueOf(data.get(position).getUsers().size()) + " Active Members");
        holder.events.setText(String.valueOf(data.get(position).getEvents().size()) + " Running Events");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, GroupDashboardActivity.class);
                i.putExtra("group_id", data.get(position).getGroup_id());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, active, events;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.group_name);
            active = itemView.findViewById(R.id.group_members);
            events = itemView.findViewById(R.id.group_events);
        }
    }

    public void setData(ArrayList<Group> da){
        this.data = da;
        notifyDataSetChanged();
    }
}
