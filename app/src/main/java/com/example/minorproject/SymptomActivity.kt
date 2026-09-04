package com.example.minorproject

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import android.content.Intent

class SymptomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_symptom)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setupSystemBars()
        setupButtons()
    }

    private fun setupSystemBars() {

        val rootView = findViewById<View>(R.id.symptomRoot)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(rootView)
    }

    private fun setupButtons() {

        // BACK BUTTON
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }


        // CHATBOT
        findViewById<MaterialCardView>(R.id.cardChatbot).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ChatbotActivity::class.java
                )
            )
        }


        // MANUAL SELECTION
        val cardManual =
            findViewById<MaterialCardView>(R.id.cardManual)

        cardManual.setOnClickListener {

            Toast.makeText(
                this,
                "Manual Selection selected",
                Toast.LENGTH_SHORT
            ).show()
        }


        // NO SPECIFIC SYMPTOMS
        val cardNoSymptoms =
            findViewById<MaterialCardView>(R.id.cardNoSymptoms)

        cardNoSymptoms.setOnClickListener {

            Toast.makeText(
                this,
                "Showing General Physician",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}