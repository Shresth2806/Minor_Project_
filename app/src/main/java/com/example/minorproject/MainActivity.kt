
package com.example.minorproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val rootView = findViewById<android.view.View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                view.paddingBottom
            )

            insets
        }

        tvGreeting = findViewById(R.id.tvGreeting)

        loadProfileName()
        setupClickListeners()
    }

    private fun loadProfileName() {

        val preferences = getSharedPreferences(
            "DocRecommProfile",
            MODE_PRIVATE
        )

        val name = preferences.getString(
            "name",
            "there"
        )

        tvGreeting.text = "Good morning, $name 👋"
    }

    private fun setupClickListeners() {

        val btnProfile =
            findViewById<ImageButton>(R.id.btnProfile)

        val cardDoctors =
            findViewById<MaterialCardView>(R.id.cardDoctors)

        val cardAppointments =
            findViewById<MaterialCardView>(R.id.cardAppointments)

        val cardRecommendation =
            findViewById<MaterialCardView>(R.id.cardRecommendation)

        val btnViewDoctor =
            findViewById<MaterialButton>(R.id.btnViewDoctor)

        val cardSymptoms =
            findViewById<MaterialCardView>(R.id.cardSymptoms)

        val cardHealthInfo =
            findViewById<MaterialCardView>(R.id.cardHealthInfo)

        val navHome =
            findViewById<LinearLayout>(R.id.navHome)

        val navDoctors =
            findViewById<LinearLayout>(R.id.navDoctors)

        val navProfile =
            findViewById<LinearLayout>(R.id.navProfile)

        btnProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        cardDoctors.setOnClickListener {
            Toast.makeText(
                this,
                "Find a Doctor selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        cardAppointments.setOnClickListener {
            Toast.makeText(
                this,
                "Appointments selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        cardRecommendation.setOnClickListener {
            Toast.makeText(
                this,
                "Doctor recommendations selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnViewDoctor.setOnClickListener {
            Toast.makeText(
                this,
                "Opening recommended doctors",
                Toast.LENGTH_SHORT
            ).show()
        }

        cardSymptoms.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SymptomActivity::class.java
                )
            )
        }

        cardHealthInfo.setOnClickListener {
            Toast.makeText(
                this,
                "Health information selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        navHome.setOnClickListener {
            Toast.makeText(
                this,
                "You are already on Home",
                Toast.LENGTH_SHORT
            ).show()
        }

        navDoctors.setOnClickListener {
            Toast.makeText(
                this,
                "Doctors selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        navProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }
    }
}

