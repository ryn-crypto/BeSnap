package com.ryan.storyapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

//        setAppbar()
        setView()

        return view
    }

//    private fun setAppbar() {
//        (requireActivity() as AppCompatActivity).supportActionBar?.title =
//            getString(R.string.app_name)
//    }


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


//        storyViewModel.result.observe(viewLifecycleOwner) { result ->

//            when (result) {
//                is ResultViewModel.Loading -> {
//                    adapter.showShimmer = true
//                }
//
//                is ResultViewModel.Success -> {
//                    val data = result.data
//                    adapter.showShimmer = false
//                    adapter.setData(data)
//                }
//
//                is ResultViewModel.Error -> {
//                    val errorMessage = result.message
//                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
//                }
//
//                else -> {
//                    val errorMessage = getString(R.string.error)
//                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
    }
}