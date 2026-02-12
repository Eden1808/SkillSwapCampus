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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SkillDetailsFragment extends Fragment {

    private TextView tvDetailsTitle, tvDetailsOwner, tvDetailsCategory, tvDetailsDescription;
    private Button btnDeleteSkill, btnRequestSkill;

    private String skillId, title, category, description, ownerName, ownerUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skill_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDetailsTitle = view.findViewById(R.id.tvDetailsTitle);
        tvDetailsOwner = view.findViewById(R.id.tvDetailsOwner);
        tvDetailsCategory = view.findViewById(R.id.tvDetailsCategory);
        tvDetailsDescription = view.findViewById(R.id.tvDetailsDescription);

        btnDeleteSkill = view.findViewById(R.id.btnDeleteSkill);
        btnRequestSkill = view.findViewById(R.id.btnRequestSkill);

        // 1) למשוך נתונים מה-Bundle
        Bundle args = getArguments();
        if (args != null) {
            skillId = args.getString("skillId");
            title = args.getString("title");
            category = args.getString("category");
            description = args.getString("description");
            ownerName = args.getString("ownerName");
            ownerUid = args.getString("ownerUid");
        }

        // 2) להציג במסך
        tvDetailsTitle.setText(title != null ? title : "");
        tvDetailsOwner.setText("By: " + (ownerName != null ? ownerName : ""));
        tvDetailsCategory.setText("Category: " + (category != null ? category : ""));
        tvDetailsDescription.setText("Description: " + (description != null ? description : ""));

        // ---- פה תשאירי את הקוד שלך למחיקה אם יש לך ----
        // btnDeleteSkill.setOnClickListener(...)

        // 3) Request Skill -> שמירה ל-Firebase
        btnRequestSkill.setOnClickListener(v -> createRequest());
    }

    private void createRequest() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String requesterUid = currentUser.getUid();

        // אם מישהו לוחץ על ה-skill של עצמו - לא הגיוני
        if (ownerUid != null && ownerUid.equals(requesterUid)) {
            Toast.makeText(requireContext(), "You cannot request your own skill", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("requests");

        // נשלוף את השם של המבקש מתוך users/{uid}/name
        usersRef.child(requesterUid).child("name")
                .get()
                .addOnSuccessListener(snapshot -> {
                    String requesterName = snapshot.getValue(String.class);
                    if (requesterName == null) requesterName = "Unknown";

                    String requestId = requestsRef.push().getKey();
                    if (requestId == null) {
                        Toast.makeText(requireContext(), "Failed to create requestId", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HashMap<String, Object> requestMap = new HashMap<>();
                    requestMap.put("requestId", requestId);
                    requestMap.put("skillId", skillId != null ? skillId : "");
                    requestMap.put("skillTitle", title != null ? title : "");
                    requestMap.put("ownerUid", ownerUid != null ? ownerUid : "");
                    requestMap.put("ownerName", ownerName != null ? ownerName : "Unknown");
                    requestMap.put("requesterUid", requesterUid);
                    requestMap.put("requesterName", requesterName);
                    requestMap.put("status", "pending");
                    requestMap.put("timestamp", System.currentTimeMillis());

                    requestsRef.child(requestId).setValue(requestMap)
                            .addOnSuccessListener(v -> {
                                Toast.makeText(requireContext(), "Request sent ✅", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load your name: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
