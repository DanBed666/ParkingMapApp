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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity2 extends AppCompatActivity
{
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        EditText nameET = findViewById(R.id.et_name);
        EditText surnameET = findViewById(R.id.et_surname);
        EditText emailET = findViewById(R.id.et_email);
        EditText passwordET = findViewById(R.id.et_password);
        EditText password2ET = findViewById(R.id.et_password2);
        Button register = findViewById(R.id.btn_next);
        TextView goToLogin = findViewById(R.id.tv_login);
        EditText nickET = findViewById(R.id.et_nick);

        EditText [] editTexts = new EditText[]{nameET, surnameET, nickET};

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                String password2 = password2ET.getText().toString();

                Log.i("PASS", email);
                Log.i("PASS", password);

                if (checkCredentials(email, password, password2))
                {
                    signUp(email, password, editTexts);
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

    public void signUp(String email, String password, EditText [] editTexts)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    String name = editTexts[0].getText().toString();
                    String surname = editTexts[1].getText().toString();
                    String nick = editTexts[2].getText().toString();
                    Log.i("REJESTRACJA", "Zarejestrowano!");
                    GetTagData get = new GetTagData();
                    DatabaseManager dbm = new DatabaseManager();

                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    //User userObj = new User(user.getUid(), name, surname, "Administrator", "0", "0", nick, getActualDate());
                    //User userObj = new User(user.getUid(), name, surname, "Moderator", "0", "0", nick, getActualDate());
                    User userObj = new User(user.getUid(), name, surname, "Użytkownik", "0", "0", nick, get.getActualDate());
                    dbm.addElement("users", user.getUid(), userObj);

                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    public boolean checkCredentials(String email, String password, String password2)
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

        if (!password.equals(password2))
        {
            Toast.makeText(getApplicationContext(), "Hasła są różne", Toast.LENGTH_SHORT).show();
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