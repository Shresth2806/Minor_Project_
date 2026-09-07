package com.example.minorproject

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecommendationActivity : AppCompatActivity() {

    private lateinit var header: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_recommendation)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setupSystemBars()
        setupBackButton()
        loadSelectedSymptoms()
    }


    // ==========================================
    // SYSTEM BAR + CAMERA CUTOUT
    // ==========================================

    private fun setupSystemBars() {

        val root =
            findViewById<View>(
                R.id.recommendationRoot
            )

        header =
            findViewById(
                R.id.recommendationHeader
            )

        ViewCompat.setOnApplyWindowInsetsListener(
            root
        ) { _, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            val displayCutout =
                insets.getInsets(
                    WindowInsetsCompat.Type.displayCutout()
                )

            val topInset =
                maxOf(
                    systemBars.top,
                    displayCutout.top
                )

            header.setPadding(
                header.paddingLeft,
                topInset,
                header.paddingRight,
                header.paddingBottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)
    }


    // ==========================================
    // BACK BUTTON
    // ==========================================

    private fun setupBackButton() {

        val backButton =
            findViewById<ImageButton>(
                R.id.btnBackRecommendation
            )

        backButton.setOnClickListener {

            finish()
        }
    }


    // ==========================================
    // LOAD SELECTED SYMPTOMS
    // ==========================================

    private fun loadSelectedSymptoms() {

        val selectedSymptoms =
            intent.getStringArrayListExtra(
                "selectedSymptoms"
            ) ?: arrayListOf()

        val symptomsTextView =
            findViewById<TextView>(
                R.id.tvSelectedSymptoms
            )

        if (selectedSymptoms.isEmpty()) {

            symptomsTextView.text =
                "No symptoms selected"

            return
        }

        val formattedSymptoms =
            selectedSymptoms.joinToString(
                separator = "\n"
            ) { symptom ->

                "• " + formatSymptom(symptom)
            }

        symptomsTextView.text =
            formattedSymptoms
    }


    // ==========================================
    // FORMAT SYMPTOM
    // ==========================================

    private fun formatSymptom(
        symptom: String
    ): String {

        return symptom
            .replace("_", " ")
            .replaceFirstChar {
                it.uppercase()
            }
    }
}