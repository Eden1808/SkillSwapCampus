package com.example.skillswapcampus;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvGoRegister = view.findViewById(R.id.tvGoRegister);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> doLogin());

        tvGoRegister.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_loginFragment_to_registerFragment)
        );

        return view;
    }

    // 🔹 Auto Login
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            NavController navController = Navigation.findNavController(view);

            // מוחק את Login מההיסטוריה
            navController.popBackStack(R.id.loginFragment, true);

            // עובר ל-Home
            navController.navigate(R.id.homeFragment);
        }
    }

    private void doLogin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(),
                    "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {

                    Toast.makeText(requireContext(),
                            "Login successful",
                            Toast.LENGTH_SHORT).show();

                    NavController navController =
                            Navigation.findNavController(requireView());

                    // מוחק את Login מההיסטוריה
                    navController.popBackStack(R.id.loginFragment, true);

                    // עובר ל-Home
                    navController.navigate(R.id.homeFragment);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Login failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}
