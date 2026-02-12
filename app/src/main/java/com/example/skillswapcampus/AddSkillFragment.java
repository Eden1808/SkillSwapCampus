package com.example.skillswapcampus;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddSkillFragment extends Fragment {

    private EditText etSkillTitle, etSkillDescription, etSkillCategory;
    private Button btnSaveSkill;

    private FirebaseAuth auth;
    private DatabaseReference skillsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_skill, container, false);

        // UI
        etSkillTitle = view.findViewById(R.id.etTitle);
        etSkillCategory = view.findViewById(R.id.etCategory);
        etSkillDescription = view.findViewById(R.id.etDescription);

        btnSaveSkill = view.findViewById(R.id.btnSaveSkill);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // כפתור שמירה
        btnSaveSkill.setOnClickListener(v -> saveSkill(v));

        return view;
    }

    private void saveSkill(View v) {
        String title = etSkillTitle.getText().toString().trim();
        String description = etSkillDescription.getText().toString().trim();
        String category = etSkillCategory.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(category)) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "You must login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        // נשמור תחת: users/{uid}/skills/{skillId}
        skillsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("skills");

        String skillId = skillsRef.push().getKey();
        if (skillId == null) {
            Toast.makeText(requireContext(), "Error creating skill id", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> skillMap = new HashMap<>();
        skillMap.put("title", title);
        skillMap.put("description", description);
        skillMap.put("category", category);

        skillsRef.child(skillId).setValue(skillMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Skill saved ✅", Toast.LENGTH_SHORT).show();

                    // ניקוי שדות
                    etSkillTitle.setText("");
                    etSkillDescription.setText("");
                    etSkillCategory.setText("");

                    // חזרה ל-Home
                    Navigation.findNavController(v)
                            .navigate(R.id.action_addSkillFragment_to_homeFragment);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
