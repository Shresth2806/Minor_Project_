package com.example.minorproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GeneralPhysicianActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_general_physician)

        val btnBack = findViewById<TextView>(R.id.btnBackGeneral)

        btnBack.setOnClickListener {
            finish()
        }

        val btnFindDoctor = findViewById<Button>(R.id.btnFindDoctor)

        btnFindDoctor.setOnClickListener {
            // Doctor recommendation screen will be connected here later.
        }
    }
}