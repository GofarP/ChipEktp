package com.example.chipe_ktp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.chipe_ktp.Model.Notifikasi
import com.example.chipe_ktp.R
import com.example.chipe_ktp.databinding.ActivityTambahEditDataBinding
import com.example.chipe_ktp.databinding.LayoutDeviceIdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TambahEditDataActivity : AppCompatActivity(){

    private lateinit var binding: ActivityTambahEditDataBinding
    private lateinit var idEktp:String
    private lateinit var username: String
    private lateinit var notifikasi:Notifikasi
    private lateinit var tanggalHariIni:String
    private lateinit var oldId:String
    private lateinit var oldName:String
    private lateinit var dialogView: View
    private lateinit var customDialog: AlertDialog
    private lateinit var customDialogBinding: LayoutDeviceIdBinding
    private lateinit var deviceId:String


    private var database= FirebaseDatabase.getInstance().reference
    private var hashmapPengguna=HashMap<String,String>()
    private var extras:Bundle? = null
    private var status="Tambah"
    private  var calendar = Calendar.getInstance()
    private var auth=FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTambahEditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extras=intent?.extras

        tanggalHariIni=SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)

        if(extras?.getString("data")=="tambah")
        {
            deviceId=extras?.getString("deviceId").toString()
        }

        else if(extras?.getString("data")=="edit")
        {
            binding.txtid.setText(extras?.getString("id"))
            binding.txtusername.setText(extras?.getString("nama"))
            binding.btntambahdata.text="Edit Data"
            status="Edit"
            oldId=extras!!.getString("id").toString()
            oldName=extras!!.getString("nama").toString()
            deviceId=extras?.getString("deviceId").toString()

        }


        binding.btntambahdata.setOnClickListener {

            idEktp=binding.txtid.toString()
            username=binding.txtusername.toString()

            if(idEktp.isNullOrEmpty()||username.isNullOrEmpty())
            {
                Toast.makeText(this@TambahEditDataActivity, "Silahkan Scan ID E-KTP Anda Kemudian Masukkan Username E-KTP", Toast.LENGTH_SHORT).show()
            }

            else if(status=="Tambah")
            {
                tambahData()
            }

            else if(status=="Edit")
            {
                editData()
            }
        }

    }

    private fun tambahData()
    {
        val text=binding.txtid.text.toString()
        username=binding.txtusername.text.toString()

        idEktp=text.subSequence(2, text.length).toString()

        database.child("pengguna").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists())
                {
                    snapshot.children.forEach{snap->

                        val snapUsername=snap.child("username").value.toString()
                        val snapId=snap.child("id").value.toString()

                        if(snapId==idEktp)
                        {
                            Toast.makeText(this@TambahEditDataActivity, "ID KTP Sudah Dipakai", Toast.LENGTH_SHORT).show()
                        }

                        else if(snapUsername.equals(username, ignoreCase = true))
                        {
                            Toast.makeText(this@TambahEditDataActivity, "Username Sudah Dipakai", Toast.LENGTH_SHORT).show()
                        }

                        else
                        {
                            hashmapPengguna["id"]=idEktp.trim()
                            hashmapPengguna["username"]=username

                            database.child("pengguna")
                                .child(deviceId)
                                .child(idEktp).setValue(hashmapPengguna).addOnSuccessListener {
                                    binding.txtid.setText("")
                                    binding.txtusername.setText("")
                                    Toast.makeText(this@TambahEditDataActivity, "Sukses menambahkan data pengguna", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Log.d("db error",it.message.toString())
                                }
                        }
                    }

                }

                else
                {
                    hashmapPengguna["idEktp"]=idEktp.replace("."," ")
                    hashmapPengguna["username"]=username

                    database.child("pengguna")
                        .child(deviceId)
                        .child(idEktp).setValue(hashmapPengguna).addOnSuccessListener {
                            binding.txtid.setText("")
                            binding.txtusername.setText("")
                            Toast.makeText(this@TambahEditDataActivity, "Sukses menambahkan data pengguna", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Log.d("db error",it.message.toString())
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("db error",error.message)
            }

        })
    }


    private fun editData()
    {
        var text=binding.txtid.text.toString()
        username=binding.txtusername.text.toString()

        idEktp=text.subSequence(2, text.length).toString().replace("\n","")

        hashmapPengguna["id"]=idEktp
        hashmapPengguna["username"]=username

        database.child("pengguna")
            .child(deviceId)
            .child(idEktp)
            .setValue(hashmapPengguna)
            .addOnSuccessListener {
                Toast.makeText(this@TambahEditDataActivity, "Edit Data Berhasil", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@TambahEditDataActivity, "Edit Data Gagal", Toast.LENGTH_SHORT).show()
            }
    }


}