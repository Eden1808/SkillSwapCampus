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
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfileFragment extends Fragment {

    private EditText etEditName, etEditBio;
    private Button btnSaveProfile, btnCancelEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEditName = view.findViewById(R.id.etEditName);
        etEditBio = view.findViewById(R.id.etEditBio);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.loginFragment);
            return;
        }

        String uid = user.getUid();

        // להביא ערכים קיימים מה-DB כדי למלא את השדות
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String name = snapshot.child("name").getValue(String.class);
                    String bio = snapshot.child("bio").getValue(String.class);

                    if (name != null) etEditName.setText(name);
                    if (bio != null) etEditBio.setText(bio);
                });

        btnSaveProfile.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            String newBio = etEditBio.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("bio", newBio);

            FirebaseDatabase.getInstance().getReference("users")
                    .child(uid)
                    .updateChildren(updates)
                    .addOnSuccessListener(done -> {
                        Toast.makeText(requireContext(), "Profile updated ", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).popBackStack(); // חזרה לפרופיל
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        btnCancelEdit.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack()
        );
    }
}
