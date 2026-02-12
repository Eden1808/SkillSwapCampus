package com.example.skillswapcampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MatchesFragment extends Fragment {

    private RecyclerView rvMatches;
    private ArrayList<MatchItem> matchesList;
    private MatchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMatches = view.findViewById(R.id.rvMatches);

        matchesList = new ArrayList<>();
        adapter = new MatchAdapter(matchesList);

        rvMatches.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMatches.setAdapter(adapter);

        loadMatches();
    }

    private void loadMatches() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String myUid = user.getUid();

        FirebaseDatabase.getInstance().getReference("requests")
                .get()
                .addOnSuccessListener(snapshot -> {
                    matchesList.clear();

                    for (DataSnapshot reqSnap : snapshot.getChildren()) {
                        RequestItem r = reqSnap.getValue(RequestItem.class);
                        if (r == null) continue;

                        if (r.status == null || !r.status.equals("approved")) continue;

                        boolean iAmOwner = myUid.equals(r.ownerUid);
                        boolean iAmRequester = myUid.equals(r.requesterUid);

                        if (!iAmOwner && !iAmRequester) continue;

                        String withName;
                        String roleText;

                        if (iAmOwner) {
                            withName = (r.requesterName != null ? r.requesterName : "Unknown");
                            roleText = "They requested";
                        } else {
                            withName = (r.ownerName != null ? r.ownerName : "Unknown");
                            roleText = "You requested";
                        }

                        matchesList.add(new MatchItem(
                                r.skillTitle != null ? r.skillTitle : "",
                                withName,
                                roleText
                        ));
                    }

                    adapter.notifyDataSetChanged();

                    if (matchesList.isEmpty()) {
                        Toast.makeText(requireContext(), "No matches yet", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
