package com.example.skillswapcampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvWelcome = view.findViewById(R.id.tvWelcome);

        Button btnAddSkill = view.findViewById(R.id.btnAddSkill);
        Button btnBrowseSkills = view.findViewById(R.id.btnBrowseSkills);
        Button btnProfile = view.findViewById(R.id.btnProfile);
        Button btnMyRequests = view.findViewById(R.id.btnMyRequests);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button btnMatches = view.findViewById(R.id.btnMatches);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            FirebaseDatabase.getInstance().getReference("users")
                    .child(uid).child("name")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        String name = snapshot.getValue(String.class);
                        if (name != null && !name.isEmpty()) {
                            tvWelcome.setText("Welcome " + name);
                        } else {
                            tvWelcome.setText("Welcome!");
                        }
                    })
                    .addOnFailureListener(e -> tvWelcome.setText("Welcome!"));
        } else {
            tvWelcome.setText("Welcome!");
        }

        btnAddSkill.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_addSkillFragment)
        );

        btnBrowseSkills.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_browseSkillsFragment)
        );

        btnProfile.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_profileFragment)
        );

        btnMyRequests.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_requestsFragment)
        );

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(v)
                    .navigate(R.id.action_homeFragment_to_loginFragment);
        });

        btnMatches.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_homeFragment_to_matchesFragment)
        );


    }
}
