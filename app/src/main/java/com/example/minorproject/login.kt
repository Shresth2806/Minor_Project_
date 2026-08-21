package com.example.minorproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val googleButton = findViewById<ImageButton>(R.id.googleButton)
        val facebookButton = findViewById<ImageButton>(R.id.facebookButton)
        val moreOptionsButton =
            findViewById<LinearLayout>(R.id.moreOptionsButton)
        val forgotPassword =
            findViewById<TextView>(R.id.forgotPassword)
        val signUpText =
            findViewById<TextView>(R.id.signUpText)

        // Login
        loginButton.setOnClickListener {
            Toast.makeText(
                this,
                "Login button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Google
        googleButton.setOnClickListener {
            Toast.makeText(
                this,
                "Continue with Google",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Facebook
        facebookButton.setOnClickListener {
            Toast.makeText(
                this,
                "Continue with Facebook",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Forgot Password
        forgotPassword.setOnClickListener {
            Toast.makeText(
                this,
                "Forgot Password selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Sign Up → Create Account
        signUpText.setOnClickListener {

            val intent = Intent(
                this,
                createacount::class.java
            )

            startActivity(intent)
        }

        // More Options
        moreOptionsButton.setOnClickListener {

            val popupMenu = PopupMenu(
                this,
                moreOptionsButton
            )

            popupMenu.menu.add("Login with Mobile Number")
            popupMenu.menu.add("Other Login Options")

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.title.toString()) {

                    "Login with Mobile Number" -> {

                        val intent = Intent(
                            this,
                            MobileLogin::class.java
                        )

                        startActivity(intent)
                    }

                    "Other Login Options" -> {

                        Toast.makeText(
                            this,
                            "Other login options selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                true
            }

            popupMenu.show()
        }
    }
}