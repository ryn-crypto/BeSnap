package com.ryan.storyapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.FragmentStoryBinding
import com.ryan.storyapp.ui.main.adapter.LoadingPagingAdapter
import com.ryan.storyapp.ui.main.adapter.StoryPagingAdapter
import com.ryan.storyapp.viewmodel.StoryViewModel
import com.ryan.storyapp.viewmodel.ViewModelFactory

class StoryFragment : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        setView()
        scrollListener()
        return view
    }


    private fun setView() {
        val adapter = StoryPagingAdapter()
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingPagingAdapter {
                adapter.retry()
            }
        )
        storyViewModel.result.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun scrollListener() {
        val bottomNav = requireActivity().findViewById<BottomAppBar>(R.id.bottomNav)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && bottomNav.visibility == View.VISIBLE) {
                    bottomNav.animate().translationY(bottomNav.height.toFloat()).setDuration(300)
                        .withEndAction {
                            bottomNav.visibility = View.GONE
                        }
                    fab.animate().alpha(0f).setDuration(300)
                        .withEndAction {
                            fab.visibility = View.GONE
                        }
                } else if (dy < 0 && bottomNav.visibility != View.VISIBLE) {
                    bottomNav.animate().translationY(0f).setDuration(300).withStartAction {
                        bottomNav.visibility = View.VISIBLE
                    }
                    fab.animate().alpha(1f).duration = 300
                    fab.visibility = View.VISIBLE
                }
            }
        })
    }
}