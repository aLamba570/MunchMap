package com.geralt.food.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.geralt.food.Adapters.MenuBottomAdapter
import com.geralt.food.Model.MenuItems
import com.geralt.food.R
import com.geralt.food.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItems>
    private lateinit var binding: FragmentMenuBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.backBtnMenu.setOnClickListener {
            dismiss()
        }
        retrieveMenuItem()


        return binding.root
    }

    private fun retrieveMenuItem(){
        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("Menu")
        menuItems = mutableListOf()
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                // ones data receive , set to adapter
                setAdapter()
            }

            private fun setAdapter() {
                val adapter = MenuBottomAdapter(menuItems, requireContext())
                binding.mRv.layoutManager = LinearLayoutManager(requireContext())
                binding.mRv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }


        })
    }

    companion object {

    }
}