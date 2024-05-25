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

public class PasswordChangeActivity extends AppCompatActivity
{
    EditText email;
    EditText oldPassword;
    EditText newPassword;
    Button changePassword;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_change);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.et_email);
        oldPassword = findViewById(R.id.old_pass);
        newPassword = findViewById(R.id.new_pass);
        changePassword = findViewById(R.id.btn_change);
        FirebaseUser user = mAuth.getCurrentUser();

        changePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert user != null;
                String emailAdr = email.getText().toString();
                String passwordOld = oldPassword.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(emailAdr, passwordOld);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    final String passwordNew = newPassword.getText().toString();
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            user.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(), "Zmieniono hasło pomyślnie", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Błąd w trakcie zmiany hasła", Toast.LENGTH_SHORT).show();
                                        Log.e("ERROR", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Błąd w trakcie zmiany hasła", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                        }
                    }
                });
            }
        });
    }
}