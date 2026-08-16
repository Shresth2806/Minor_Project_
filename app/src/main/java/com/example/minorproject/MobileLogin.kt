package com.example.minorproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MobileLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mobile_login)

        val mobileNumberEditText =
            findViewById<EditText>(R.id.mobileNumberEditText)

        val sendOtpButton =
            findViewById<Button>(R.id.sendOtpButton)

        sendOtpButton.setOnClickListener {

            val mobileNumber =
                mobileNumberEditText.text.toString().trim()

            if (mobileNumber.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter your mobile number",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (mobileNumber.length != 10) {

                Toast.makeText(
                    this,
                    "Please enter a valid 10-digit mobile number",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "OTP will be sent to $mobileNumber",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}