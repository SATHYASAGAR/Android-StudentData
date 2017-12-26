package com.example.sathy.studentdata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstScreenActivity extends AppCompatActivity{

    Button uploadDataButton;
    Button retrieveDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        uploadDataButton = (Button)findViewById(R.id.uploadDataButton);
        retrieveDataButton = (Button)findViewById(R.id.retrieveDataButton);

        uploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData(v);
            }
        });

        retrieveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData(v);
            }
        });
    }
        public void uploadData(View v){
        Intent go = new Intent(this,FormActivity.class);
        startActivity(go);
    }

    public void retrieveData(View v){
        Intent go = new Intent(this,FilterActivity.class);
        startActivity(go);
    }
}
