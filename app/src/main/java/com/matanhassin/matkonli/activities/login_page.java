package com.matanhassin.matkonli.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.ModelFirebase;

public class login_page extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginBtn;
    Button registerBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        firebaseAuth = firebaseAuth.getInstance();

        emailInput = findViewById(R.id.login_page_Email_Edittext);
        passwordInput = findViewById(R.id.login_page_Password_Edittext);
        loginBtn = findViewById(R.id.login_Page_Login_btn);

        if(firebaseAuth.getCurrentUser()!=null)
        {
            ModelFirebase.setUserData(firebaseAuth.getCurrentUser().getEmail());
            startActivity(new Intent(login_page.this,MainActivity.class));
            finish();
        }
        this.setTitle("Login");

        registerBtn = findViewById(R.id.login_Page_Register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegisterPage();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelFirebase.loginUser(emailInput.getText().toString(), passwordInput.getText().toString(), new ModelFirebase.Listener<Boolean>() {
                    @Override
                    public void onComplete()
                    {
                        startActivity(new Intent(login_page.this, MainActivity.class));
                        login_page.this.finish();
                    }

                    @Override
                    public void onFail()
                    {

                    }
                });
            }
        });

    }

    private void toRegisterPage() {
        Intent intent = new Intent(this, register_page.class);
        startActivity(intent);
    }
}