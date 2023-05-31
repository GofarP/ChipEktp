package com.example.chipe_ktp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chipe_ktp.Activity.LoginActivity
import com.example.chipe_ktp.Activity.TambahEditDataActivity
import com.example.chipe_ktp.Adapter.PenggunaAdapter
import com.example.chipe_ktp.Model.Notifikasi
import com.example.chipe_ktp.Model.Pengguna
import com.example.chipe_ktp.PreferenceManager.PreferenceManager
import com.example.chipe_ktp.R
import com.example.chipe_ktp.Service.sharedPreference
import com.example.chipe_ktp.databinding.FragmentHomeBinding
import com.example.chipe_ktp.databinding.LayoutDeviceIdBinding
import com.example.chipe_ktp.databinding.LayoutKonfirmasiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), PenggunaAdapter.OnClickListener {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapter: PenggunaAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var pengguna: Pengguna
    private lateinit var dialogView: View
    private lateinit var customDialog: AlertDialog
    private lateinit var layoutKonfirmasiDialog: LayoutKonfirmasiBinding
    private lateinit var layoutDeviceIdDialog: LayoutDeviceIdBinding
    private lateinit var notifikasi:Notifikasi
    private lateinit var tanggalHariIni:String
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var deviceId:String


    private val database= FirebaseDatabase.getInstance().reference
    private val auth= FirebaseAuth.getInstance()
    private val email=FirebaseAuth.getInstance().currentUser?.email.toString()
    private val calendar=Calendar.getInstance()

    private var penggunaArrayList = ArrayList<Pengguna>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getData()

        checkStatusAkun()



        preferenceManager= PreferenceManager()
        preferenceManager.preferenceManager(requireActivity())

        if(preferenceManager.getString("ROLE").isNullOrEmpty())
        {
            startActivity(Intent(requireActivity(),LoginActivity::class.java))
            activity?.supportFragmentManager?.popBackStack()
        }



        tanggalHariIni= SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)


        binding.fabadd.setOnClickListener {
            val intent=Intent(requireContext(), TambahEditDataActivity::class.java)
            intent.putExtra("data","tambah")
            intent.putExtra("deviceId",deviceId)
            startActivity(intent)
        }

    }


    private fun checkStatusAkun()
    {
        database.child("akun")
            .child(auth.currentUser?.uid.toString())
            .child("status")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value.toString()=="Banned")
                    {
                        startActivity(Intent(requireActivity(),LoginActivity::class.java))
                        activity?.supportFragmentManager?.popBackStack()
                        Toast.makeText(requireActivity(), "Akun Anda Telah Di Banned", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("snap err",error.message.toString())
                }

            })
    }

    private fun bukaPintu(pengguna: Pengguna)
    {

        dialogView=layoutInflater.inflate(R.layout.layout_konfirmasi, null)
        customDialog=AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()

        layoutKonfirmasiDialog=LayoutKonfirmasiBinding.inflate(layoutInflater)
        customDialog.setContentView(layoutKonfirmasiDialog.root)

        val password=layoutKonfirmasiDialog.txtkonfirmasipassword.text

        layoutKonfirmasiDialog.btnkonfirmasipassword.setOnClickListener {

            val random = (0..1000000).random()

            if(password.isNullOrEmpty())
            {
                Toast.makeText(requireContext(), "Silahkan Isi Konfirmasi Password Anda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            notifikasi= Notifikasi(
                    notifikasi="Admin Telah Membukakan Pintu Untuk ${pengguna.nama} Lewat aplikasi",
                    time = tanggalHariIni
                )

            database.child("bukapintu")
                .child(deviceId)
                .child("status")
                .setValue("Buka")

            auth.signInWithEmailAndPassword(email, password.toString()).addOnSuccessListener {
                database.child("notifikasi")
                    .child(deviceId)
                    .child(random.toString())
                    .setValue(notifikasi)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Berhasil Membuka Pintu", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Password Salah", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getData()
    {
        database.child("deviceid")
            .child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val snapDeviceId=snapshot.child("deviceid").value.toString()

                    deviceId=snapDeviceId

                    if(snapshot.exists())
                    {
                        database.child("pengguna")
                            .child(snapDeviceId)
                            .addValueEventListener(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    penggunaArrayList.clear()
                                    snapshot.children.forEach { snap->
                                        pengguna=Pengguna(
                                            nama=snap.child("username").value.toString(),
                                            id=snap.child("idEktp").value.toString()
                                            )
                                        penggunaArrayList.add(pengguna)
                                    }

                                    layoutManager=LinearLayoutManager(requireActivity())
                                    adapter=PenggunaAdapter(penggunaArrayList, this@HomeFragment)
                                    binding.rvdata.layoutManager=layoutManager
                                    binding.rvdata.adapter=adapter

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d("DB Error",error.message)
                                }

                            })
                    }

                    else
                    {
                        dialogView=layoutInflater.inflate(R.layout.layout_device_id, null)
                        customDialog=AlertDialog.Builder(requireActivity())
                            .setView(dialogView)
                            .setCancelable(false)
                            .show()

                        layoutDeviceIdDialog= LayoutDeviceIdBinding.inflate(layoutInflater)
                        customDialog.setContentView(layoutDeviceIdDialog.root)

                        val txtDeviceId=layoutDeviceIdDialog.txtdeviceid.text

                        deviceId=txtDeviceId.toString()

                        layoutDeviceIdDialog.btntambahdeviceid.setOnClickListener {


                            database.child("deviceid")
                                .child(auth.currentUser?.uid.toString())
                                .child("deviceid")
                                .setValue(txtDeviceId.toString())

                            database.child("bukapintu")
                                .child(txtDeviceId.toString())
                                .child("status")
                                .setValue("Tutup")

                            database.child("fcmtoken")
                                .child(txtDeviceId.toString())
                                .child("fcmtoken")
                                .setValue(sharedPreference.getString("FCM_TOKEN"))

                            customDialog.dismiss()
                            Toast.makeText(requireActivity(), "Sukses Menambah Device ID", Toast.LENGTH_SHORT).show()

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
    }

    private fun hapusData(pengguna: Pengguna)
    {
        dialogView=layoutInflater.inflate(R.layout.layout_konfirmasi, null)
        customDialog=AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()

        layoutKonfirmasiDialog=LayoutKonfirmasiBinding.inflate(layoutInflater)
        customDialog.setContentView(layoutKonfirmasiDialog.root)

        val password=layoutKonfirmasiDialog.txtkonfirmasipassword.text

        layoutKonfirmasiDialog.btnkonfirmasipassword.setOnClickListener {
            if(password.isNullOrEmpty())
            {
                Toast.makeText(requireContext(), "Silahkan Isi Konfirmasi Password Anda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password.toString()).addOnSuccessListener {
                database.child("pengguna")
                    .child(deviceId)
                    .child(pengguna.id)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Sukses Menghapus Data Pengguna", Toast.LENGTH_SHORT).show()
                        val indexPengguna=penggunaArrayList.indexOf(pengguna)
                        adapter.notifyItemRemoved(indexPengguna)
                    }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Password Salah", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun clickBukaPintu(view: View, pengguna: Pengguna) {
        bukaPintu(pengguna)
    }

    override fun clickHapusData(view: View, pengguna: Pengguna) {
        hapusData(pengguna)
    }

    override fun clickEditData(view: View, pengguna: Pengguna) {

        val intent=Intent(requireContext(), TambahEditDataActivity::class.java)
        intent.putExtra("data","edit")
        intent.putExtra("deviceId",deviceId)
        intent.putExtra("nama",pengguna.nama)
        intent.putExtra("id",pengguna.id)

        startActivity(intent)
    }


}