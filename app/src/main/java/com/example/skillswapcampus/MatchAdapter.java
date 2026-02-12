package com.example.skillswapcampus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private ArrayList<MatchItem> list;

    public MatchAdapter(ArrayList<MatchItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchItem m = list.get(position);
        holder.tvMatchSkill.setText("Skill: " + (m.skillTitle != null ? m.skillTitle : ""));
        holder.tvMatchWith.setText("With: " + (m.withName != null ? m.withName : "Unknown"));
        holder.tvMatchRole.setText("Role: " + (m.roleText != null ? m.roleText : ""));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvMatchSkill, tvMatchWith, tvMatchRole;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatchSkill = itemView.findViewById(R.id.tvMatchSkill);
            tvMatchWith = itemView.findViewById(R.id.tvMatchWith);
            tvMatchRole = itemView.findViewById(R.id.tvMatchRole);
        }
    }
}
