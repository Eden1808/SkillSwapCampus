package com.example.skillswapcampus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    public interface OnSkillClickListener {
        void onSkillClick(SkillItem skill);
    }

    private ArrayList<SkillItem> skillsList;
    private OnSkillClickListener listener;

    public SkillAdapter(ArrayList<SkillItem> skillsList, OnSkillClickListener listener) {
        this.skillsList = skillsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skill, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        SkillItem skill = skillsList.get(position);

        holder.tvTitle.setText(skill.title);
        holder.tvOwner.setText("By: " + skill.ownerName);
        holder.tvCategory.setText("Category: " + skill.category);
        holder.tvDescription.setText("Description: " + skill.description);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSkillClick(skill);
            }
        });
    }

    @Override
    public int getItemCount() {
        return skillsList.size();
    }

    static class SkillViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOwner, tvCategory, tvDescription;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
