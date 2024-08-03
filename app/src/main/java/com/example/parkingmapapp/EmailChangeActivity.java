package com.example.parkingmapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class EmailChangeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_change);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText email;
        EditText password;
        EditText newEmail;
        Button changeEmail;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.old_pass);
        newEmail = findViewById(R.id.et_newemail);
        changeEmail = findViewById(R.id.btn_change);
        FirebaseUser user = mAuth.getCurrentUser();

        changeEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert user != null;
                String emailAdr = email.getText().toString();
                String passwordOld = password.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(emailAdr, passwordOld);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            assert user != null;
                            String email = newEmail.getText().toString();
                            user.verifyBeforeUpdateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(), "Zmieniono adres email pomyślnie", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Błąd w trakcie zmiany maila", Toast.LENGTH_SHORT).show();
                                        Log.e("ERROR", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Niepoprawne passy", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                        }
                    }
                });
            }
        });
    }
}