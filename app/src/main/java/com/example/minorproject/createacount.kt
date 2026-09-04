package com.example.minorproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth


class createacount : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_createacount
        )


        // Firebase
        auth = FirebaseAuth.getInstance()


        val fullName =
            findViewById<EditText>(
                R.id.etFullName
            )

        val email =
            findViewById<EditText>(
                R.id.etEmail
            )

        val password =
            findViewById<EditText>(
                R.id.etPassword
            )

        val confirmPassword =
            findViewById<EditText>(
                R.id.etConfirmPassword
            )

        val signUpButton =
            findViewById<Button>(
                R.id.btnSignUp
            )

        val loginText =
            findViewById<TextView>(
                R.id.tvLogin
            )


        // ------------------------------------------
        // CREATE ACCOUNT
        // ------------------------------------------

        signUpButton.setOnClickListener {

            val name =
                fullName.text.toString().trim()

            val userEmail =
                email.text.toString().trim()

            val userPassword =
                password.text.toString()

            val confirm =
                confirmPassword.text.toString()


            if (name.isEmpty()) {

                fullName.error =
                    "Enter your full name"

                fullName.requestFocus()

                return@setOnClickListener
            }


            if (userEmail.isEmpty()) {

                email.error =
                    "Enter your email"

                email.requestFocus()

                return@setOnClickListener
            }


            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(userEmail)
                    .matches()
            ) {

                email.error =
                    "Enter a valid email"

                email.requestFocus()

                return@setOnClickListener
            }


            if (userPassword.length < 6) {

                password.error =
                    "Password must contain at least 6 characters"

                password.requestFocus()

                return@setOnClickListener
            }


            if (userPassword != confirm) {

                confirmPassword.error =
                    "Passwords do not match"

                confirmPassword.requestFocus()

                return@setOnClickListener
            }


            createFirebaseAccount(
                name,
                userEmail,
                userPassword
            )
        }


        // ------------------------------------------
        // LOGIN
        // ------------------------------------------

        loginText.setOnClickListener {

            finish()
        }
    }


    // ==================================================
    // FIREBASE ACCOUNT CREATION
    // ==================================================

    private fun createFirebaseAccount(
        name: String,
        email: String,
        password: String
    ) {

        auth.createUserWithEmailAndPassword(
            email,
            password
        )
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val user =
                        auth.currentUser


                    // Save user's name
                    val profile =
                        getSharedPreferences(
                            "DocRecommProfile",
                            MODE_PRIVATE
                        )


                    profile.edit()
                        .putString(
                            "name",
                            name
                        )
                        .putString(
                            "email",
                            email
                        )
                        .putBoolean(
                            "profileCompleted",
                            false
                        )
                        .apply()


                    Toast.makeText(
                        this,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()


                    // ----------------------------------
                    // OPEN PROFILE SETUP
                    // ----------------------------------

                    val intent =
                        Intent(
                            this,
                            ProfileSetupActivity::class.java
                        )


                    startActivity(intent)

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        task.exception?.message
                            ?: "Account creation failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}