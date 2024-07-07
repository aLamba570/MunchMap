package com.geralt.food.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.geralt.food.Adapters.MenuBottomAdapter
import com.geralt.food.Model.MenuItems
import com.geralt.food.R
import com.geralt.food.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set up View Menu button click
        binding.viewMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve and display menu popular items
        retrieveAndDisplayMenuPopularItems()

        // Set up image slider
        setupImageSlider()
    }

    private fun setupImageSlider() {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                // Handle double click
            }

            override fun onItemSelected(position: Int) {
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun retrieveAndDisplayMenuPopularItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("Menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (foodSnapshot in snapshot.children) {
                        val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                        menuItem?.let { menuItems.add(it) }
                    }
                    // Display random popular items
                    randomPopularItems()
                } else {
                    Log.e("Home", "No data found in /Menu node")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Home", "Database error: ${error.message}")
            }
        })
    }

    private fun randomPopularItems() {
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 10
        val subsetMenuItems = index.take(numItemToShow).map { menuItems[it] }
        setPopularItemsAdapter(subsetMenuItems)
    }

    private fun setPopularItemsAdapter(subsetMenuItems: List<MenuItems>) {
        if (subsetMenuItems.isNotEmpty()) {
            val adapter = MenuBottomAdapter(subsetMenuItems, requireContext())
            binding.rv.layoutManager = LinearLayoutManager(requireContext())
            binding.rv.adapter = adapter
        } else {
            Log.e("Home", "No items to display in RecyclerView")
        }
    }
}
