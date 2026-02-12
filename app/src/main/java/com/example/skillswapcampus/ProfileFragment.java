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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvProfileBio;
    private Button btnEditProfile, btnLogoutProfile;

    private RecyclerView rvMySkills;
    private ArrayList<SkillItem> mySkillsList;
    private SkillAdapter mySkillsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileBio = view.findViewById(R.id.tvProfileBio);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogoutProfile = view.findViewById(R.id.btnLogoutProfile);

        rvMySkills = view.findViewById(R.id.rvMySkills);

        mySkillsList = new ArrayList<>();
        mySkillsAdapter = new SkillAdapter(mySkillsList, skill -> {
            Bundle b = new Bundle();
            b.putString("skillId", skill.skillId);
            b.putString("title", skill.title);
            b.putString("category", skill.category);
            b.putString("description", skill.description);
            b.putString("ownerName", skill.ownerName);
            b.putString("ownerUid", skill.ownerUid);

            Navigation.findNavController(view)
                    .navigate(R.id.action_profileFragment_to_skillDetailsFragment, b);
        });

        rvMySkills.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMySkills.setAdapter(mySkillsAdapter);

        loadProfileAndMySkills();

        btnEditProfile.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_profileFragment_to_editProfileFragment)
        );

        btnLogoutProfile.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(v)
                    .navigate(R.id.action_profileFragment_to_loginFragment);
        });
    }

    private void loadProfileAndMySkills() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();

        tvProfileEmail.setText("Email: " + (email != null ? email : ""));

        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String name = snapshot.child("name").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);

                    tvProfileName.setText("Name: " + (name != null ? name : ""));
                    tvProfileBio.setText("Bio: " + (bio != null && !bio.isEmpty() ? bio : "(empty)"));

                    // My Skills
                    mySkillsList.clear();
                    DataSnapshot skillsSnap = snapshot.child("skills");

                    for (DataSnapshot skillSnap : skillsSnap.getChildren()) {
                        String skillId = skillSnap.getKey();
                        String title = skillSnap.child("title").getValue(String.class);
                        String description = skillSnap.child("description").getValue(String.class);
                        String category = skillSnap.child("category").getValue(String.class);

                        SkillItem item = new SkillItem(
                                skillId != null ? skillId : "",
                                title != null ? title : "",
                                description != null ? description : "",
                                category != null ? category : "",
                                name != null ? name : "Me",
                                uid
                        );

                        mySkillsList.add(item);
                    }

                    mySkillsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
