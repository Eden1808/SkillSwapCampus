package com.example.skillswapcampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestsFragment extends Fragment {

    private RecyclerView rvRequests;
    private TextView tvSectionTitle;
    private Button btnIncoming, btnMyRequests;

    private ArrayList<RequestItem> requestsList;
    private RequestAdapter adapter;

    private enum Mode { INCOMING, MY_REQUESTS }
    private Mode currentMode = Mode.INCOMING;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvRequests = view.findViewById(R.id.rvRequests);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        btnIncoming = view.findViewById(R.id.btnIncoming);
        btnMyRequests = view.findViewById(R.id.btnMyRequests);

        requestsList = new ArrayList<>();

        adapter = new RequestAdapter(requestsList, true, new RequestAdapter.OnDecisionListener() {
            @Override
            public void onApprove(RequestItem req) {
                updateRequestStatus(req, "approved");
            }

            @Override
            public void onReject(RequestItem req) {
                updateRequestStatus(req, "rejected");
            }
        });

        rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRequests.setAdapter(adapter);

        // ברירת מחדל: Incoming
        showIncoming();

        btnIncoming.setOnClickListener(v -> showIncoming());
        btnMyRequests.setOnClickListener(v -> showMyRequests());
    }

    private void showIncoming() {
        currentMode = Mode.INCOMING;
        tvSectionTitle.setText("Incoming requests");
        adapter.setShowActions(true); // כאן כן יש כפתורים
        loadRequests();
    }

    private void showMyRequests() {
        currentMode = Mode.MY_REQUESTS;
        tvSectionTitle.setText("My requests");
        adapter.setShowActions(false); // כאן אין כפתורים
        loadRequests();
    }

    private void loadRequests() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String myUid = user.getUid();

        FirebaseDatabase.getInstance().getReference("requests")
                .get()
                .addOnSuccessListener(snapshot -> {
                    requestsList.clear();

                    for (DataSnapshot reqSnap : snapshot.getChildren()) {
                        RequestItem req = reqSnap.getValue(RequestItem.class);
                        if (req == null) continue;

                        // אם חסר requestId בתוך האובייקט, ניקח מה-key
                        if (req.requestId == null || req.requestId.isEmpty()) {
                            req.requestId = reqSnap.getKey();
                        }

                        if (currentMode == Mode.INCOMING) {
                            if (myUid.equals(req.ownerUid)) {
                                requestsList.add(req);
                            }
                        } else {
                            if (myUid.equals(req.requesterUid)) {
                                requestsList.add(req);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (requestsList.isEmpty()) {
                        Toast.makeText(requireContext(), "No requests found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void updateRequestStatus(RequestItem req, String newStatus) {
        if (req == null || req.requestId == null || req.requestId.isEmpty()) {
            Toast.makeText(requireContext(), "Missing requestId", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference("requests")
                .child(req.requestId)
                .child("status")
                .setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                    // לרענן את הרשימה כדי שתראי את הסטטוס החדש
                    loadRequests();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
