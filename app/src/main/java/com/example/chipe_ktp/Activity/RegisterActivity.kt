package com.example.chipe_ktp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chipe_ktp.Model.Akun
import com.example.chipe_ktp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var akun:Akun

    private var auth=FirebaseAuth.getInstance()
    private val database=FirebaseDatabase.getInstance().reference
    private var akunHashMap=HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email=binding.txtemail.text
        val password=binding.txtpassword.text
        val konfirmasiPassword=binding.txtkonfirmasipassword.text

        binding.btnlogin.setOnClickListener {
            if(email.isNullOrEmpty() || password.isNullOrEmpty() || konfirmasiPassword.isNullOrEmpty())
            {
                Toast.makeText(this@RegisterActivity, "Silahkan Masukkan Email, Password, Dan KOnfirmasi Password Untuk DIdaftarkan", Toast.LENGTH_SHORT).show()
            }

            else if(!password.toString().equals(konfirmasiPassword.toString(), ignoreCase = true))
            {
                Toast.makeText(this@RegisterActivity, "Password Dan Konfirmasi Pasword Tidak Sama", Toast.LENGTH_SHORT).show()
            }

            else if(password.toString().length<8)
            {
                Toast.makeText(this@RegisterActivity, "Panjang Password Harus 8 atau lebih", Toast.LENGTH_SHORT).show()
            }

            else
            {
                auth.fetchSignInMethodsForEmail(email.toString()).addOnCompleteListener {task->

                    val belumTerdaftar=task.result.signInMethods?.isEmpty()

                    if(belumTerdaftar == true)
                    {
                        register(email.toString(), password.toString())
                    }

                    else
                    {
                        Toast.makeText(this@RegisterActivity, "Email Sudah Digunakan", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    Log.d("auth errrr",it.toString())
                }
            }

        }

    }


    private fun register(email:String, password:String)
    {

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            auth.currentUser?.sendEmailVerification()
            Toast.makeText(this@RegisterActivity, "Pendaftaran Akun Berhasil, Silahkan Cek E-Mail Untuk Verifikasi", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            akun=Akun(id=auth.currentUser?.uid.toString(), email=email, role = "user", status="Active")
            database.child("akun").child(auth.currentUser?.uid.toString()).setValue(akun)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@RegisterActivity, "Pendaftaran Akun Gagal", Toast.LENGTH_SHORT).show()
        }
    }
}