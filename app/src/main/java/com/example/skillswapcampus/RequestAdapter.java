package com.example.skillswapcampus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    public interface OnDecisionListener {
        void onApprove(RequestItem req);
        void onReject(RequestItem req);
    }

    private ArrayList<RequestItem> list;
    private boolean showActions; // true = Incoming, false = My Requests
    private OnDecisionListener listener;

    public RequestAdapter(ArrayList<RequestItem> list, boolean showActions, OnDecisionListener listener) {
        this.list = list;
        this.showActions = showActions;
        this.listener = listener;
    }

    public void setShowActions(boolean showActions) {
        this.showActions = showActions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestItem r = list.get(position);

        holder.tvReqTitle.setText("Skill: " + (r.skillTitle != null ? r.skillTitle : ""));
        holder.tvReqFrom.setText("From: " + (r.requesterName != null ? r.requesterName : "Unknown"));
        holder.tvReqStatus.setText("Status: " + (r.status != null ? r.status : "pending"));

        // כפתורים רק ב-Incoming
        holder.layoutActions.setVisibility(showActions ? View.VISIBLE : View.GONE);

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(r);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(r);
        });

        // טיפ קטן: אם כבר לא pending, אפשר להסתיר כפתורים
        if (r.status != null && !r.status.equals("pending")) {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvReqTitle, tvReqFrom, tvReqStatus;
        LinearLayout layoutActions;
        Button btnReject, btnApprove;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReqTitle = itemView.findViewById(R.id.tvReqTitle);
            tvReqFrom = itemView.findViewById(R.id.tvReqFrom);
            tvReqStatus = itemView.findViewById(R.id.tvReqStatus);

            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnApprove = itemView.findViewById(R.id.btnApprove);
        }
    }
}
