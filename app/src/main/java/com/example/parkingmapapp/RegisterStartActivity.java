package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stripe.model.tax.Registration;

public class RegisterStartActivity extends AppCompatActivity
{
    EditText nameET;
    EditText surnameET;
    EditText emailET;
    EditText passwordET;
    EditText password2ET;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameET = findViewById(R.id.et_name);
        surnameET = findViewById(R.id.et_surname);
        emailET = findViewById(R.id.et_email);
        passwordET = findViewById(R.id.et_password);
        password2ET = findViewById(R.id.et_password2);
        next = findViewById(R.id.btn_next);

        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameET.getText().toString();
                String surname = surnameET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                Intent intent = new Intent(getApplicationContext(), RegisterActivity2.class);
                intent.putExtra("NAME", name);
                intent.putExtra("SURNAME", surname);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);

                startActivity(intent);
            }
        });
    }
}