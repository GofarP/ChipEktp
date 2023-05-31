package com.example.chipe_ktp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chipe_ktp.PreferenceManager.PreferenceManager
import com.example.chipe_ktp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var preferenceManager:PreferenceManager


    private var currentUser= FirebaseAuth.getInstance().currentUser

    private var auth=FirebaseAuth.getInstance()

    private var database=FirebaseDatabase.getInstance().reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(this@LoginActivity)

        if(currentUser!=null && auth.currentUser?.isEmailVerified==true && preferenceManager.getString("ROLE")=="superadmin")
        {
            startActivity(Intent(this@LoginActivity, SuperAdminActivity::class.java))
            finish()
        }

        else if(currentUser!=null && auth.currentUser?.isEmailVerified==true && preferenceManager.getString("ROLE")=="user")
        {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        binding.btnlogin.setOnClickListener {

            email=binding.txtemail.text.toString()
            password=binding.txtpassword.text.toString()
            login(email,password)
        }

        binding.lblregister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        binding.lbllupassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, LupaPasswordActivity::class.java))
        }

    }

    private fun login(email:String, password:String)
    {
        database.child("akun")
            .addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var emailDitemukan=false
                    var statusAkun=""
                    var role=""

                    snapshot.children.forEach {snap->
                        val snapEmail=snap.child("email").value.toString()
                        val snapStatus=snap.child("status").value.toString()
                        val snapRole=snap.child("role").value.toString()

                        if(email==snapEmail)
                        {
                            emailDitemukan=true
                            statusAkun=snapStatus
                            role=snapRole
                            Log.d("snap","$email sama dengan $snapEmail")
                        }
                    }

                    if(emailDitemukan && statusAkun=="Active")
                    {
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { result ->

                            if(auth.currentUser?.isEmailVerified == true)
                            {
                                if(role=="superadmin")
                                {
                                    preferenceManager.putString("ROLE","superadmin")
                                    startActivity(Intent(this@LoginActivity, SuperAdminActivity::class.java))
                                }

                                else if(role=="user")
                                {
                                    preferenceManager.putString("ROLE","user")
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                }
                            }

                            else
                            {
                                Toast.makeText(this@LoginActivity, "Silahkan Verifikasi Akun Ini Lewat E-Mail Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                            }

                        }.addOnFailureListener {error->
                            when(error.toString())
                            {
                                "There is no user record corresponding to this identifier. The user may have been deleted."->{
                                    Toast.makeText(this@LoginActivity, "Email Yang Dimasukkan Tidak Valid", Toast.LENGTH_SHORT).show()
                                }

                                "The password is invalid or the user does not have a password."->{
                                    Toast.makeText(this@LoginActivity, "Password yang Dimasukkan Tidak Valid", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    else if(emailDitemukan && statusAkun=="Banned")
                    {
                        Toast.makeText(this@LoginActivity, "Akun Anda Telah Di Banned", Toast.LENGTH_SHORT).show()
                    }

                    else
                    {
                        Toast.makeText(this@LoginActivity, "Akun Tidak Ditemukan", Toast.LENGTH_SHORT).show()
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB error",error.message)
                }

            })

    }
//    private fun login()
//    {
//        if(!email.contains("@gmail.com"))
//        {
//            Toast.makeText(this@LoginActivity, "Format Email Tidak Valid", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { result->
//
//            if(auth.currentUser?.isEmailVerified == true)
//            {
//                database.child("akun")
//                    .child(auth.currentUser!!.uid)
//                    .child("email")
//                    .equalTo(email)
//                    .addListenerForSingleValueEvent(object:ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//
//                        if(snapshot.exists())
//                        {
//                            Log.d("snap",snapshot.value.toString())
//                        }
//
//                        else
//                        {
//                            Log.d("snap","tidak ditemukan")
//                        }
////                        if(snapshot.exists())
////                        {
////                            Toast.makeText(this@LoginActivity, "Data Ketemu", Toast.LENGTH_SHORT).show()
////                        }
////
////                        else{
////                            Toast.makeText(this@LoginActivity, "Data Tidak Ketemu", Toast.LENGTH_SHORT).show()
////                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Log.d("snap error",error.message)
//                    }
//
//                })
//
//            }
//
//            else
//            {
//                Toast.makeText(this@LoginActivity, "Silahkan Verifikasi Akun Ini Lewat E-Mail Terlebih Dahulu", Toast.LENGTH_SHORT).show()
//            }
//
//        }.addOnFailureListener {error->
//            when(error.toString())
//            {
//                "There is no user record corresponding to this identifier. The user may have been deleted."->{
//                    Toast.makeText(this@LoginActivity, "Email Yang Dimasukkan Tidak Valid", Toast.LENGTH_SHORT).show()
//                }
//
//                "The password is invalid or the user does not have a password."->{
//                    Toast.makeText(this@LoginActivity, "Password yang Dimasukkan Tidak Valid", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }
}

//1.Login
//2.Cek Jika AKun ditemukan Atau tidak
//3.Jika akun ditemukan maka dicek bahwa akun tersebut akun superadmin atau user
//4.Jika superadmin maka akan diarahkan ke superadmin activity
//5.JIka user maka diarahkan ke MainActivity
