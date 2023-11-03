package com.ryan.storyapp.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.databinding.FragmentStoryBinding
import com.ryan.storyapp.repository.StoryRepository
import com.ryan.storyapp.ui.createstory.CreateStoryActivity
import com.ryan.storyapp.viewmodel.StoryViewModel
import com.ryan.storyapp.viewmodel.StoryViewModelFactory

class StoryFragment : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryAdapter
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        val view = binding.root

        vieModelInitialisation()
        setAppbar()
        setAdapter()
        setView()

        return view
    }

    private fun setAppbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setAdapter() {
        recyclerView = binding.recyclerView
        adapter = StoryAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun vieModelInitialisation() {
        val repository = StoryRepository(requireContext())
        val viewModelFactory = StoryViewModelFactory(repository)
        storyViewModel = ViewModelProvider(this, viewModelFactory)[StoryViewModel::class.java]
//        storyViewModel.fetchStories()
    }

    private fun setView() {
        storyViewModel.result.observe(viewLifecycleOwner) { result ->

            when (result) {
                is ResultViewModel.Loading -> {
                    adapter.showShimmer = true
                }

                is ResultViewModel.Success -> {
                    val data = result.data
                    adapter.showShimmer = false
                    adapter.setData(data)
                }

                is ResultViewModel.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val errorMessage = getString(R.string.error)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}