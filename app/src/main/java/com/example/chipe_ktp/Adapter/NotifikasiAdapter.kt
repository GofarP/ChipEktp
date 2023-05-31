package com.example.chipe_ktp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chipe_ktp.Model.Notifikasi
import com.example.chipe_ktp.Model.Pengguna
import com.example.chipe_ktp.databinding.LayoutNotifikasiBinding
import com.example.chipe_ktp.databinding.LayoutPenggunaBinding

class NotifikasiAdapter(val notifikasiArrayList: ArrayList<Notifikasi>)
    :RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>()
{

        class ViewHolder(layoutNotifikasiBinding: LayoutNotifikasiBinding)
            :RecyclerView.ViewHolder(layoutNotifikasiBinding.root)
        {
            val binding=layoutNotifikasiBinding

            fun bind(notifikasi: Notifikasi)
            {
                itemView.apply {
                    binding.lblnotifikasi.text=notifikasi.notifikasi
                    binding.lblwaktu.text=notifikasi.time
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding= LayoutNotifikasiBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return notifikasiArrayList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(notifikasiArrayList[position])
        }
}