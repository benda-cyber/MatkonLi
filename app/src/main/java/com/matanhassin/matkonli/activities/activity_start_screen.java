package com.matanhassin.matkonli.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.matanhassin.matkonli.R;
import com.matanhassin.matkonli.fragments.HomeFragment;

public class activity_start_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        new Thread(){
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                } finally {
                    toLoginPage();
                }
            }
        }.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }

    private void toLoginPage() {
        Intent intent = new Intent(this, login_page.class);
        startActivity(intent);
    }
}