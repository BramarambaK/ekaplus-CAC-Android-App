package com.eka.cacapp.ui.notidtl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.eka.cacapp.R;

public class FcmNotificationDtlScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm_notification_dtl_screen);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
            }
        }
    }
}