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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BrowseSkillsFragment extends Fragment {

    private RecyclerView rvSkills;
    private ArrayList<SkillItem> skillsList;
    private SkillAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse_skills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSkills = view.findViewById(R.id.rvSkills);

        skillsList = new ArrayList<>();
        adapter = new SkillAdapter(skillsList, skill -> {
            Bundle bundle = new Bundle();
            bundle.putString("skillId", skill.skillId);
            bundle.putString("title", skill.title);
            bundle.putString("category", skill.category);
            bundle.putString("description", skill.description);
            bundle.putString("ownerName", skill.ownerName);
            bundle.putString("ownerUid", skill.ownerUid);

            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_browseSkillsFragment_to_skillDetailsFragment, bundle);
        });


        rvSkills.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSkills.setAdapter(adapter);

        loadAllSkillsFromFirebase();
    }

    private void loadAllSkillsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("users")
                .get()
                .addOnSuccessListener(snapshot -> {
                    skillsList.clear();

                    for (DataSnapshot userSnap : snapshot.getChildren()) {

                        String ownerUid = userSnap.getKey();
                        String ownerName = userSnap.child("name").getValue(String.class);

                        DataSnapshot skillsSnap = userSnap.child("skills");

                        for (DataSnapshot skillSnap : skillsSnap.getChildren()) {
                            String title = skillSnap.child("title").getValue(String.class);
                            String description = skillSnap.child("description").getValue(String.class);
                            String category = skillSnap.child("category").getValue(String.class);

                            String skillId = skillSnap.getKey();

                            SkillItem item = new SkillItem(
                                    skillId != null ? skillId : "",
                                    title != null ? title : "",
                                    description != null ? description : "",
                                    category != null ? category : "",
                                    ownerName != null ? ownerName : "Unknown",
                                    ownerUid != null ? ownerUid : ""
                            );


                            skillsList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (skillsList.isEmpty()) {
                        Toast.makeText(requireContext(), "No skills found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
