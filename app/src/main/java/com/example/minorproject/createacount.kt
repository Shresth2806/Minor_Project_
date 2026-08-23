package com.example.minorproject

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class createacount : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_createacount)

        window.statusBarColor = Color.rgb(248, 249, 252)
        window.navigationBarColor = Color.rgb(248, 249, 252)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)

        setupPasswordToggle(etPassword)
        setupPasswordToggle(etConfirmPassword)

        // Sign Up button
        btnSignUp.setOnClickListener {
            createAccount()
        }

        // Login
        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun createAccount() {

        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword =
            etConfirmPassword.text.toString()

        // Name
        if (name.isEmpty()) {
            etFullName.error = "Enter your full name"
            etFullName.requestFocus()
            return
        }

        // Email
        if (email.isEmpty()) {
            etEmail.error = "Enter your email"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            etEmail.error = "Enter a valid email"
            etEmail.requestFocus()
            return
        }

        // Password
        if (password.isEmpty()) {
            etPassword.error = "Create a password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error =
                "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        // Confirm Password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error =
                "Confirm your password"
            etConfirmPassword.requestFocus()
            return
        }

        // Password match
        if (password != confirmPassword) {
            etConfirmPassword.error =
                "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        Toast.makeText(
            this,
            "Account created successfully!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupPasswordToggle(editText: EditText) {

        var passwordVisible = false

        editText.setOnTouchListener { view, event ->

            if (event.action == MotionEvent.ACTION_UP) {

                val drawable =
                    editText.compoundDrawables[2]

                if (drawable != null &&
                    event.x >= editText.width -
                    editText.paddingEnd -
                    drawable.bounds.width()
                ) {

                    val position =
                        editText.selectionStart

                    passwordVisible =
                        !passwordVisible

                    if (passwordVisible) {

                        editText.transformationMethod =
                            HideReturnsTransformationMethod
                                .getInstance()

                    } else {

                        editText.transformationMethod =
                            PasswordTransformationMethod
                                .getInstance()
                    }

                    editText.setSelection(
                        position.coerceAtLeast(0)
                    )

                    view.performClick()

                    true

                } else {
                    false
                }

            } else {
                false
            }
        }
    }
}