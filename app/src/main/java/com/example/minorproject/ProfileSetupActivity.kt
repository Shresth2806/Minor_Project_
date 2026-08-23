package com.example.minorproject

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText

    private lateinit var genderGroup: ChipGroup
    private lateinit var diseaseGroup: ChipGroup

    private lateinit var btnSaveContinue: MaterialButton
    private lateinit var tvSkip: TextView
    private lateinit var tvAvatar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_setup)

        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)

        genderGroup = findViewById(R.id.genderGroup)
        diseaseGroup = findViewById(R.id.diseaseGroup)

        btnSaveContinue = findViewById(R.id.btnSaveContinue)
        tvSkip = findViewById(R.id.tvSkip)
        tvAvatar = findViewById(R.id.tvAvatar)

        setupDiseaseSelection()

        etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateAvatar(etName.text.toString())
            }
        }

        btnSaveContinue.setOnClickListener {
            saveProfile()
        }

        tvSkip.setOnClickListener {
            openHome()
        }
    }

    private fun setupDiseaseSelection() {

        diseaseGroup.setOnCheckedStateChangeListener { group, checkedIds ->

            val noneChip = findViewById<Chip>(R.id.chipNone)

            if (noneChip.isChecked) {

                for (i in 0 until group.childCount) {

                    val view = group.getChildAt(i)

                    if (view is Chip && view.id != R.id.chipNone) {
                        view.isChecked = false
                    }
                }
            }
        }

        val noneChip = findViewById<Chip>(R.id.chipNone)

        noneChip.setOnClickListener {

            if (noneChip.isChecked) {

                for (i in 0 until diseaseGroup.childCount) {

                    val view = diseaseGroup.getChildAt(i)

                    if (view is Chip && view.id != R.id.chipNone) {
                        view.isChecked = false
                    }
                }
            }
        }
    }

    private fun saveProfile() {

        val name = etName.text.toString().trim()
        val ageText = etAge.text.toString().trim()

        if (name.isEmpty()) {

            etName.error = "Enter your name"
            etName.requestFocus()
            return
        }

        if (ageText.isEmpty()) {

            etAge.error = "Enter your age"
            etAge.requestFocus()
            return
        }

        val age = ageText.toIntOrNull()

        if (age == null || age < 1 || age > 120) {

            etAge.error = "Enter a valid age"
            etAge.requestFocus()
            return
        }

        val genderId = genderGroup.checkedChipId

        if (genderId == -1) {

            Toast.makeText(
                this,
                "Please select your gender",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val genderChip = findViewById<Chip>(genderId)

        val gender = genderChip.text.toString()

        val diseases = mutableListOf<String>()

        for (id in diseaseGroup.checkedChipIds) {

            val chip = findViewById<Chip>(id)

            diseases.add(chip.text.toString())
        }

        if (diseases.isEmpty()) {

            Toast.makeText(
                this,
                "Please select your health history",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val preferences = getSharedPreferences(
            "DocRecommProfile",
            MODE_PRIVATE
        )

        preferences.edit()
            .putString("name", name)
            .putInt("age", age)
            .putString("gender", gender)
            .putStringSet(
                "diseases",
                diseases.toSet()
            )
            .putBoolean(
                "profileCompleted",
                true
            )
            .apply()

        Toast.makeText(
            this,
            "Profile saved successfully",
            Toast.LENGTH_SHORT
        ).show()

        openHome()
    }

    private fun updateAvatar(name: String) {

        if (name.isEmpty()) {

            tvAvatar.text = "👤"
            return
        }

        val words = name
            .trim()
            .split("\\s+".toRegex())

        val initials = if (words.size >= 2) {

            "${words[0].first()}${words[1].first()}"

        } else {

            words[0].take(2)
        }

        tvAvatar.text = initials.uppercase()
    }

    private fun openHome() {

        val intent = Intent(
            this@ProfileSetupActivity,
            MainActivity::class.java
        )

        startActivity(intent)

        finish()
    }
}