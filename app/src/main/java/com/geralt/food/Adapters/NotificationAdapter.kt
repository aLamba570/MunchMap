package com.geralt.food.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geralt.food.databinding.NotificatinItemBinding

class NotificationAdapter (val nName : ArrayList<String>,val nImage : ArrayList<Int>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    inner class ViewHolder (val binding : NotificatinItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                notificationText.text=nName[position]
                notificationImage.setImageResource(nImage[position])
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {
        val binding = NotificatinItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = nName.size
}