package com.example.chipe_ktp.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chipe_ktp.Activity.MainActivity
import com.example.chipe_ktp.Adapter.NotifikasiAdapter
import com.example.chipe_ktp.Model.Notifikasi
import com.example.chipe_ktp.R
import com.example.chipe_ktp.databinding.FragmentHomeBinding
import com.example.chipe_ktp.databinding.FragmentNotifikasiBinding
import com.example.chipe_ktp.databinding.LayoutDeviceIdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotifikasiFragment : Fragment() {

    private lateinit var binding: FragmentNotifikasiBinding
    private lateinit var notifikasi: Notifikasi
    private lateinit var layoutManager:LinearLayoutManager
    private lateinit var notifikasiAdapter:NotifikasiAdapter
    private lateinit var deviceId:String


    private var database=FirebaseDatabase.getInstance().reference

    private var notifikasiArrayList=ArrayList<Notifikasi>()

    private var auth=FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNotifikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

    }

    private fun getData()
    {
        database.child("deviceid")
            .child(auth.currentUser?.uid.toString())
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val snapDeviceId=snapshot.child("deviceid").value.toString()

                    database.child("notifikasi")
                        .child(snapDeviceId)
                        .addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach {snap->
                                    val snapNotifikasi=snap.child("notifikasi").value.toString()
                                    val snapTime=snap.child("time").value.toString()
                                    notifikasi=Notifikasi(snapNotifikasi, snapTime)
                                    notifikasiArrayList.add(notifikasi)
                                }

                                layoutManager=LinearLayoutManager(requireActivity())
                                notifikasiAdapter=NotifikasiAdapter(notifikasiArrayList)
                                binding.rvnotifikasi.layoutManager=layoutManager
                                binding.rvnotifikasi.adapter=notifikasiAdapter
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("snap",error.message)
                            }

                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("db error",error.message)
                }

            })
    }
//    private fun getData()
//    {
//        notifikasiArrayList.clear()
//        database.child("notifikasi")
//            .child(deviceId)
//            .addValueEventListener(object :ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    snapshot.children.forEach { snap->
//                        val snapNotifikasi=snap.child("notifikasi").value.toString()
//                        val snapTime=snap.child("time").value.toString()
//                        notifikasi= Notifikasi(snapNotifikasi,snapTime)
//                        notifikasiArrayList.add(notifikasi)
//                    }
//
//                    layoutManager=LinearLayoutManager(requireContext())
//                    notifikasiAdapter=NotifikasiAdapter(notifikasiArrayList)
//                    binding.rvnotifikasi.layoutManager=layoutManager
//                    binding.rvnotifikasi.adapter=notifikasiAdapter
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.d("db error",error.message)
//                }
//
//            })
//    }
}