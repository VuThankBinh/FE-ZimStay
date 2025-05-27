package com.datn.zimstay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DatLichThanhCongActivity extends AppCompatActivity {

    private String name,appointmentDate;
    private int apartmentId,ownerId,userId;
    TextView UserNameDLC,SdtDLC,dateDLC;
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dat_lich_thanh_cong);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        name=intent.getStringExtra("userName");
        apartmentId=intent.getIntExtra("apartmentId", 0);
        ownerId=intent.getIntExtra("ownerId", 0);
        userId=intent.getIntExtra("userId", 0);
        appointmentDate=intent.getStringExtra("appointmentDate");

        UserNameDLC=findViewById(R.id.UserNameDLC);
        SdtDLC=findViewById(R.id.SdtDLC);
        dateDLC=findViewById(R.id.dateDLC);
        btnClose=findViewById(R.id.btnClose);
        UserNameDLC.setText(name);
        SdtDLC.setText("0987654321");
        dateDLC.setText(appointmentDate);
        btnClose.setOnClickListener(v -> {
            Intent intent1 = new Intent(DatLichThanhCongActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });


    }
    private void sendNotification() {

    }
}