package com.example.minorproject

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.checkbox.MaterialCheckBox

class ManualSymptomActivity : AppCompatActivity() {

    private lateinit var symptomContainer: LinearLayout
    private lateinit var selectedSymptoms: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_manual_symptom)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        selectedSymptoms = linkedSetOf()

        setupSystemBars()
        setupButtons()
        loadSymptoms()
    }


    // ==========================================
    // SYSTEM BAR + CAMERA CUTOUT
    // ==========================================

    private fun setupSystemBars() {

        val root =
            findViewById<View>(R.id.manualRoot)

        val header =
            findViewById<View>(R.id.manualHeader)

        val bottomAction =
            findViewById<View>(R.id.bottomAction)

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

            val bottomInset =
                maxOf(
                    systemBars.bottom,
                    displayCutout.bottom
                )

            header.setPadding(
                header.paddingLeft,
                topInset,
                header.paddingRight,
                header.paddingBottom
            )

            bottomAction.setPadding(
                bottomAction.paddingLeft,
                bottomAction.paddingTop,
                bottomAction.paddingRight,
                bottomInset + 10
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)
    }


    // ==========================================
    // BUTTONS
    // ==========================================

    private fun setupButtons() {

        val backButton =
            findViewById<ImageButton>(
                R.id.btnBackManual
            )

        backButton.setOnClickListener {

            finish()
        }


        val findButton =
            findViewById<Button>(
                R.id.btnFindCondition
            )

        findButton.setOnClickListener {

            if (selectedSymptoms.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please select at least one symptom",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent =
                android.content.Intent(
                    this,
                    ProcessingActivity::class.java
                )

            intent.putStringArrayListExtra(
                "selectedSymptoms",
                ArrayList(selectedSymptoms)
            )

            startActivity(intent)
        }
    }


    // ==========================================
    // LOAD SYMPTOMS FROM CSV
    // ==========================================

    private fun loadSymptoms() {

        symptomContainer =
            findViewById(R.id.symptomContainer)

        val symptoms =
            linkedSetOf<String>()

        try {

            val inputStream =
                assets.open("DiseaseAndSymptoms.csv")

            val reader =
                inputStream.bufferedReader()

            // Skip CSV header
            reader.readLine()

            reader.forEachLine { line ->

                val columns =
                    parseCsvLine(line)

                for (
                i in 1 until
                        minOf(columns.size, 18)
                ) {

                    val symptom =
                        columns[i].trim()

                    if (symptom.isNotEmpty()) {

                        symptoms.add(symptom)
                    }
                }
            }

            reader.close()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to load symptom database",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        symptoms
            .sorted()
            .forEach { symptom ->

                addSymptomCheckBox(symptom)
            }
    }


    // ==========================================
    // ADD CHECKBOX
    // ==========================================

    private fun addSymptomCheckBox(
        symptom: String
    ) {

        val checkBox =
            MaterialCheckBox(this)

        checkBox.text =
            symptom
                .replace("_", " ")
                .replaceFirstChar {
                    it.uppercase()
                }

        checkBox.textSize = 14f

        checkBox.minHeight = 52

        checkBox.setPadding(
            8,
            4,
            8,
            4
        )

        checkBox.setOnCheckedChangeListener {
                _,
                checked ->

            if (checked) {

                selectedSymptoms.add(
                    symptom
                )

            } else {

                selectedSymptoms.remove(
                    symptom
                )
            }
        }

        symptomContainer.addView(
            checkBox
        )
    }


    // ==========================================
    // CSV PARSER
    // ==========================================

    private fun parseCsvLine(
        line: String
    ): List<String> {

        val result =
            mutableListOf<String>()

        val current =
            StringBuilder()

        var insideQuotes =
            false

        for (character in line) {

            when {

                character == '"' -> {

                    insideQuotes =
                        !insideQuotes
                }

                character == ',' &&
                        !insideQuotes -> {

                    result.add(
                        current.toString()
                    )

                    current.clear()
                }

                else -> {

                    current.append(
                        character
                    )
                }
            }
        }

        result.add(
            current.toString()
        )

        return result
    }
}