package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText emailET;
    EditText passwordET;
    Button register;
    TextView goToRegister;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailET = findViewById(R.id.et_email);
        passwordET = findViewById(R.id.et_password);
        register = findViewById(R.id.btn_register);
        goToRegister = findViewById(R.id.tv_register);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailET.getText().toString();
                String password = emailET.getText().toString();

                signUp(email, password);
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    public void signUp(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Log.i("LOGIN!", "Zalogowano pomyślnie!");
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                }
                else
                {
                    Log.i("BŁĄD", "Błąd w trakcie logowania!");
                    Log.i("BŁĄD2", Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }
}