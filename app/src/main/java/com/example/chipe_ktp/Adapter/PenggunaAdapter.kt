package com.example.chipe_ktp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chipe_ktp.Model.Pengguna
import com.example.chipe_ktp.databinding.LayoutPenggunaBinding

class PenggunaAdapter(val arrayListPengguna:ArrayList<Pengguna>, val itemOnClick: OnClickListener)
    :RecyclerView.Adapter<PenggunaAdapter.ViewHolder>()

{

    class ViewHolder(layoutPenggunaBinding: LayoutPenggunaBinding)
        :RecyclerView.ViewHolder(layoutPenggunaBinding.root){

            private val binding=layoutPenggunaBinding

            fun bind(pengguna: Pengguna, itemOnClick: OnClickListener){
                itemView.apply {
                    binding.lblnamapengguna.text=pengguna.nama

                    binding.btnbukapintu.setOnClickListener { view->
                        itemOnClick.clickBukaPintu(view, pengguna)
                    }
                    binding.btnhapusdata.setOnClickListener { view->
                        itemOnClick.clickHapusData(view, pengguna)
                    }

                    binding.cvpengguna.setOnClickListener { view->
                        itemOnClick.clickEditData(view, pengguna)
                    }
                }
            }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=LayoutPenggunaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return arrayListPengguna.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayListPengguna[position],itemOnClick)
    }

    interface OnClickListener
    {
        fun clickBukaPintu(view: View, pengguna: Pengguna)

        fun clickHapusData(view: View, pengguna: Pengguna)

        fun clickEditData(view: View, pengguna: Pengguna)

    }

}