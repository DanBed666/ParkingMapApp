package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity
{
    EditText nameET;
    EditText surnameET;
    EditText emailET;
    EditText passwordET;
    Button register;
    TextView goToLogin;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        nameET = findViewById(R.id.et_name);
        surnameET = findViewById(R.id.et_surname);
        emailET = findViewById(R.id.et_email);
        passwordET = findViewById(R.id.et_password);
        register = findViewById(R.id.btn_register);
        goToLogin = findViewById(R.id.tv_login);

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameET.getText().toString();
                String surname = surnameET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                Log.i("PASS", email);
                Log.i("PASS", password);

                if (checkCredentials(email, password))
                {
                    User user = new User(name, surname);
                    signUp(email, password, user);
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    public void signUp(String email, String password, User user)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Log.i("REJESTRACJA", "Zarejestrowano!");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    sendConfirmMail();
                    finish();
                }
                else
                {
                    Log.i("BŁĄD", "Błąd w trakcie rejestracji!");
                }
            }
        });
    }

    public boolean checkCredentials(String email, String password)
    {
        if (email.isEmpty() && password.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Wypełnij puste pola!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length() < 8)
        {
            Toast.makeText(getApplicationContext(), "Hasło powinno mieć min 8 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void sendConfirmMail()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "WYsłano link do potwierdzenia maila!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.i("BŁĄD", "Błąd potwierdzenie!");
                }
            }
        });
    }
}