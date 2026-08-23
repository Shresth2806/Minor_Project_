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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import kotlinx.coroutines.launch


class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        // Firebase
        auth = FirebaseAuth.getInstance()

        // Credential Manager
        credentialManager = CredentialManager.create(this)


        // ---------------------------------------------------------
        // FIND VIEWS
        // ---------------------------------------------------------

        val loginButton =
            findViewById<Button>(R.id.loginButton)

        val googleButton =
            findViewById<ImageButton>(R.id.googleButton)

        val facebookButton =
            findViewById<ImageButton>(R.id.facebookButton)

        val moreOptionsButton =
            findViewById<LinearLayout>(R.id.moreOptionsButton)

        val forgotPassword =
            findViewById<TextView>(R.id.forgotPassword)

        val signUpText =
            findViewById<TextView>(R.id.signUpText)


        // ---------------------------------------------------------
        // NORMAL LOGIN
        // ---------------------------------------------------------

        loginButton.setOnClickListener {

            Toast.makeText(
                this,
                "Login button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ---------------------------------------------------------
        // GOOGLE LOGIN
        // ---------------------------------------------------------

        googleButton.setOnClickListener {

            startGoogleSignIn()
        }


        // ---------------------------------------------------------
        // FACEBOOK LOGIN
        // ---------------------------------------------------------

        facebookButton.setOnClickListener {

            Toast.makeText(
                this,
                "Continue with Facebook",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ---------------------------------------------------------
        // FORGOT PASSWORD
        // ---------------------------------------------------------

        forgotPassword.setOnClickListener {

            Toast.makeText(
                this,
                "Forgot Password selected",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ---------------------------------------------------------
        // SIGN UP
        // ---------------------------------------------------------

        signUpText.setOnClickListener {

            Toast.makeText(
                this,
                "Sign Up selected",
                Toast.LENGTH_SHORT
            ).show()
        }


        // ---------------------------------------------------------
        // THREE DOT MORE OPTIONS
        // ---------------------------------------------------------

        moreOptionsButton.setOnClickListener {

            val popupMenu =
                PopupMenu(
                    this,
                    moreOptionsButton
                )


            popupMenu.menu.add(
                "Login with Mobile Number"
            )

            popupMenu.menu.add(
                "Other Login Options"
            )


            popupMenu.setOnMenuItemClickListener { item ->

                when (item.title.toString()) {

                    "Login with Mobile Number" -> {

                        val intent =
                            Intent(
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


    // =========================================================
    // GOOGLE SIGN-IN
    // =========================================================

    private fun startGoogleSignIn() {

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(
                getString(R.string.default_web_client_id)
            )
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {

            try {

                val result = credentialManager.getCredential(
                    context = this@Login,
                    request = request
                )

                handleGoogleSignIn(result)

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    this@Login,
                    "Google Sign-In failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // HANDLE GOOGLE CREDENTIAL
    // =========================================================

    private fun handleGoogleSignIn(
        result: GetCredentialResponse
    ) {

        val credential =
            result.credential


        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential
                .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                val googleIdTokenCredential =
                    GoogleIdTokenCredential
                        .createFrom(
                            credential.data
                        )


                val idToken =
                    googleIdTokenCredential.idToken


                firebaseAuthWithGoogle(
                    idToken
                )


            } catch (
                e: GoogleIdTokenParsingException
            ) {

                e.printStackTrace()


                Toast.makeText(
                    this,
                    "Unable to process Google account",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                "Unexpected Google credential",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    // =========================================================
    // FIREBASE AUTHENTICATION
    // =========================================================

    private fun firebaseAuthWithGoogle(
        idToken: String
    ) {

        val credential =
            GoogleAuthProvider.getCredential(
                idToken,
                null
            )


        auth.signInWithCredential(
            credential
        )

            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val user =
                        auth.currentUser


                    Toast.makeText(
                        this,
                        "Welcome ${user?.displayName ?: "User"}!",
                        Toast.LENGTH_LONG
                    ).show()


                    // Google login successful.
                    //
                    // Later we will put:
                    // startActivity(...)
                    // here to open your Home page.


                } else {

                    Toast.makeText(
                        this,
                        "Firebase Google Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}