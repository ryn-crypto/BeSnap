package com.ryan.storyapp.ui.main.paging

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ryan.storyapp.databinding.ActivityPagingBinding

class PagingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagingBinding
    private val mainViewModel: PagingViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvQuote.layoutManager = LinearLayoutManager(this)

        getData()
    }

    private fun getData() {
        val adapter = PagingAdaper()
        binding.rvQuote.adapter = adapter.withLoadStateFooter(
            footer = LoadingPaging {
                adapter.retry()
            }
        )
        mainViewModel.quote.observe(this) {
            adapter.submitData(lifecycle, it)
        }

    }
}