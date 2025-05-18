package com.datn.zimstay;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ZimStayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
    }
}