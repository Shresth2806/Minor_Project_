
package com.example.minorproject

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvDiseases: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        btnBack = findViewById(R.id.btnBack)

        tvName = findViewById(R.id.tvProfileName)
        tvAge = findViewById(R.id.tvProfileAge)
        tvGender = findViewById(R.id.tvProfileGender)
        tvDiseases = findViewById(R.id.tvProfileDiseases)

        btnBack.setOnClickListener {
            finish()
        }

        loadProfile()
    }

    private fun loadProfile() {

        val preferences = getSharedPreferences(
            "DocRecommProfile",
            MODE_PRIVATE
        )

        val name = preferences.getString(
            "name",
            "Not provided"
        )

        val age = preferences.getInt(
            "age",
            0
        )

        val gender = preferences.getString(
            "gender",
            "Not provided"
        )

        val diseases = preferences.getStringSet(
            "diseases",
            emptySet()
        )

        tvName.text = name

        tvAge.text = if (age > 0) {
            "$age years"
        } else {
            "Not provided"
        }

        tvGender.text = gender

        tvDiseases.text = if (!diseases.isNullOrEmpty()) {
            diseases.joinToString(", ")
        } else {
            "None"
        }
    }
}

