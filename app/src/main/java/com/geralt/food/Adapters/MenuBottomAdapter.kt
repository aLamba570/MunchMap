package com.geralt.food.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geralt.food.DetailsActivity
import com.geralt.food.Model.MenuItems
import com.geralt.food.databinding.FragmentMenuBottomSheetBinding
import com.geralt.food.databinding.MenuItemBinding

class MenuBottomAdapter (
    private val menuItem: List<MenuItems>,
    private val requireContext: Context
): RecyclerView.Adapter<MenuBottomAdapter.MenuViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuBottomAdapter.MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuBottomAdapter.MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItem.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItem[position]

            // a intent to open details activity and pass data
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItem", menuItem.foodName)
                putExtra("MenuImage", menuItem.foodImage)
                putExtra("MenuPrice", menuItem.foodPrice)
                putExtra("MenuDescription", menuItem.foodDescription)
                putExtra("MenuIngredients", menuItem.foodIngredient)
            }
            // start the details activity
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItem[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuFoodPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)


            }
        }

    }

}




