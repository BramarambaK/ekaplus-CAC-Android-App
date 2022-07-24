package com.eka.cacapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.eka.cacapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       if(intent.hasExtra("logout")) {
           if (intent.getBooleanExtra("logout", false)) {
               findNavController(R.id.fragment).navigate(R.id.singInOptionsFrag)
           }
       }
    }
}