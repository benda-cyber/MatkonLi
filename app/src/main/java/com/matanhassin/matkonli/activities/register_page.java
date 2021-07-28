package com.matanhassin.matkonli.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.model.ModelFirebase;
import com.matanhassin.matkonli.model.Utils;

public class register_page extends AppCompatActivity {

    EditText userName;
    EditText email;
    EditText password;
    ImageView photoUpload;
    Button loginBtn;
    Button registerBtn;
    Uri profileImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        userName = findViewById(R.id.register_Page_Name_EditText);
        email = findViewById(R.id.register_Page_Email_EditText);
        password = findViewById(R.id.register_Page_Password_EditText);
        photoUpload = findViewById(R.id.register_Page_Camera_imageButton);
        photoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.imageFromGallery(register_page.this);
            }
        });
        registerBtn = findViewById(R.id.register_Page_Register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelFirebase.registerUser(userName.getText().toString(),password.getText().toString(),email.getText().toString(),profileImageUri,new ModelFirebase.Listener<Boolean>(){
                    @Override
                    public void onComplete() {
                        register_page.this.finish();
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }
        });
        loginBtn = findViewById(R.id.register_Page_Login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLoginPage();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && resultCode == RESULT_OK){
            profileImageUri = data.getData();
            photoUpload.setImageURI(profileImageUri);
        }
        else {
            Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void toLoginPage() {
        Intent intent = new Intent(this, login_page.class);
        startActivity(intent);
    }
}