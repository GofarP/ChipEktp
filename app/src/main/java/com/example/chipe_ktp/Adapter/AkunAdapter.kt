package com.example.chipe_ktp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chipe_ktp.Model.Akun
import com.example.chipe_ktp.R
import com.example.chipe_ktp.databinding.LayoutAkunBinding

class AkunAdapter(val arrayListAkun: ArrayList<Akun>, val itemOnClickListener: OnClickListener)
    :RecyclerView.Adapter<AkunAdapter.ViewHolder>()
{

    class ViewHolder(layoutAkun: LayoutAkunBinding)
        :RecyclerView.ViewHolder(layoutAkun.root){
            private val binding=layoutAkun

        fun bind(akun: Akun, itemOnClick: OnClickListener)
        {
            itemView.apply {
                binding.lblakun.text=akun.email

                if(akun.status=="Active")
                {
                    binding.btndeleteaccount.setBackgroundColor(ContextCompat.getColor(context,R.color.green))
                    binding.btndeleteaccount.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.unlock))
                }

                else if(akun.status=="Banned")
                {
                    binding.btndeleteaccount.setBackgroundColor(ContextCompat.getColor(context,R.color.red))
                    binding.btndeleteaccount.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.lock))
                }

                binding.btndeleteaccount.setOnClickListener { view->
                    itemOnClick.akunAction(view,akun)
                }

            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=LayoutAkunBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayListAkun[position], itemOnClickListener)
    }


    override fun getItemCount(): Int {
        return arrayListAkun.size
    }


    interface OnClickListener
    {
        fun akunAction(view: View, akun: Akun)
    }

}
