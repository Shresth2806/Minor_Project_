package com.example.minorproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProcessingActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_processing)

        tvStatus = findViewById(R.id.tvProcessingStatus)
        progressBar = findViewById(R.id.processingProgress)

        val selectedSymptoms =
            intent.getStringArrayListExtra("selectedSymptoms")
                ?: arrayListOf()

        if (selectedSymptoms.isEmpty()) {
            finish()
            return
        }

        progressBar.max = 100
        progressBar.progress = 0

        processSymptoms(selectedSymptoms)
    }

    private fun processSymptoms(
        symptoms: ArrayList<String>
    ) {

        tvStatus.text = "Analyzing your symptoms..."
        progressBar.progress = 20

        Handler(Looper.getMainLooper()).postDelayed({

            tvStatus.text = "Comparing with our symptom database..."
            progressBar.progress = 50

        }, 700)

        Handler(Looper.getMainLooper()).postDelayed({

            tvStatus.text = "Finding possible conditions..."
            progressBar.progress = 80

        }, 1400)

        Handler(Looper.getMainLooper()).postDelayed({

            tvStatus.text = "Preparing recommendations..."
            progressBar.progress = 100

        }, 2100)

        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(
                this,
                RecommendationActivity::class.java
            )

            intent.putStringArrayListExtra(
                "selectedSymptoms",
                symptoms
            )

            startActivity(intent)

            finish()

        }, 2800)
    }
}