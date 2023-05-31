package com.example.chipe_ktp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chipe_ktp.R
import com.example.chipe_ktp.databinding.ActivityLupaPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLupaPasswordBinding
    private lateinit var email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLupaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnresetpassword.setOnClickListener {
            email=binding.txtemail.text.toString().trim()
            if(email.contains("@gmail.com"))
            {
                FirebaseAuth.getInstance().setLanguageCode("en")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this@LupaPasswordActivity, "Cek email anda untuk mereset password anda", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@LupaPasswordActivity, "Gagal Mengirim Reset Password", Toast.LENGTH_SHORT).show()
                    }
            }

            else
            {
                Toast.makeText(this@LupaPasswordActivity, "Format Gmail tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}