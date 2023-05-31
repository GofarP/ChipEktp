package com.example.chipe_ktp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chipe_ktp.Adapter.AkunAdapter
import com.example.chipe_ktp.Adapter.AkunAdapter.OnClickListener
import com.example.chipe_ktp.Adapter.PenggunaAdapter
import com.example.chipe_ktp.Model.Akun
import com.example.chipe_ktp.PreferenceManager.PreferenceManager
import com.example.chipe_ktp.R
import com.example.chipe_ktp.databinding.ActivitySuperAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SuperAdminActivity : AppCompatActivity(), OnClickListener{
    private lateinit var binding:ActivitySuperAdminBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var akunAdapter: AkunAdapter
    private lateinit var akun: Akun
    private lateinit var preferenceManager: PreferenceManager

    private val database=FirebaseDatabase.getInstance().reference
    private var arrayListAkun=ArrayList<Akun>()
    private val auth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySuperAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(this@SuperAdminActivity)
        if(preferenceManager.getString("ROLE").isNullOrEmpty())
        {
            startActivity(Intent(this@SuperAdminActivity,LoginActivity::class.java))
            finish()
        }
        getAkunData()
    }


    private fun getAkunData()
    {
        database.child("akun").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrayListAkun.clear()

                snapshot.children.forEach { snap->
                    val id=snap.child("id").value.toString()
                    val role=snap.child("role").value.toString()
                    val email=snap.child("email").value.toString()
                    val status=snap.child("status").value.toString()

                    akun=Akun(
                        id=id,
                        role=role,
                        email =email,
                        status=status
                    )

                    if(role!="superadmin")
                    {
                        arrayListAkun.add(akun)
                    }

                    Log.d("snap",snap.value.toString())

                }

                layoutManager=LinearLayoutManager(this@SuperAdminActivity)
                akunAdapter= AkunAdapter(arrayListAkun,this@SuperAdminActivity)
                binding.rvakun.layoutManager=layoutManager
                binding.rvakun.adapter=akunAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SuperAdminActivity, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    override fun akunAction(view: View, akun: Akun) {
        database.child("akun").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {snap->

                    val snapEmail=snap.child("email").value.toString()
                    val snapStatus=snap.child("status").value.toString()
                    val snapId=snap.child("id").value.toString()

                    if(snapEmail==akun.email && snapStatus=="Active")
                    {
                        banned(snapId)
                    }

                    else if(snapEmail==akun.email && snapStatus=="Banned")
                    {
                        unbanned(snapId)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("snap",error.message)
            }

        })
//        database.child("akun").addListenerForSingleValueEvent(object:ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach {snap->
//                    val snapEmail=snap.child("email").value.toString()
//                    val snapId=snap.child("id").value.toString()
//                    if(snapEmail==akun.email)
//                    {
//                        database.child("akun")
//                            .child(snapId)
//                            .child("status")
//                            .setValue("Banned")
//                            .addOnSuccessListener {
//                                Toast.makeText(this@SuperAdminActivity, "Sukses Banned Akun", Toast.LENGTH_SHORT).show()
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(this@SuperAdminActivity, "Gagal Banned Akun", Toast.LENGTH_SHORT).show()
//                            }
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("snap",error.message)
//            }
//
//        })
    }

    private fun banned(id:String)
    {
        val builder=AlertDialog.Builder(this@SuperAdminActivity)
        builder.setMessage("Apakah Anda Yakin Ingin Banned Akun Ini?")
            .setCancelable(true)
            .setPositiveButton("Ya"){dialog, _->
                database.child("akun")
                    .child(id)
                    .child("status")
                    .setValue("Banned")
                    .addOnSuccessListener {
                        Toast.makeText(this@SuperAdminActivity, "Sukses Banned Akun", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@SuperAdminActivity, "Gagal Banned Akun", Toast.LENGTH_SHORT).show()
                    }

                dialog.dismiss()
            }
            .setNegativeButton("Tidak"){dialog,_->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()

    }


    private fun unbanned(id: String)
    {
        val builder=AlertDialog.Builder(this@SuperAdminActivity)
        builder.setMessage("Apakah Anda Yakin Ingin Unbanned Akun Ini?")
            .setCancelable(true)
            .setPositiveButton("Ya"){dialog,_->
                database.child("akun")
                    .child(id)
                    .child("status")
                    .setValue("Active")
                    .addOnSuccessListener {
                        Toast.makeText(this@SuperAdminActivity, "Sukses Unbanned Akun", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@SuperAdminActivity, "Gagal Unbanned Akun", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Tidak"){dialog,_->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.logout->{
                auth.signOut()
                startActivity(Intent(this@SuperAdminActivity, LoginActivity::class.java))
                Toast.makeText(this@SuperAdminActivity, "Sukses Logout", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        return super.onOptionsItemSelected(item)

    }


}