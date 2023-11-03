package com.ryan.storyapp.ui.detail

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.data.model.Story
import com.ryan.storyapp.databinding.ActivityDetailStoryBinding
import com.ryan.storyapp.repository.DetailStoryRepository
import com.ryan.storyapp.repository.RegisterRepository
import com.ryan.storyapp.utils.DateUtils
import com.ryan.storyapp.utils.SharedPreferencesManager
import com.ryan.storyapp.utils.SharedPreferencesManagerFactory
import com.ryan.storyapp.utils.loadImage
import com.ryan.storyapp.viewmodel.DetailStoryViewModel
import com.ryan.storyapp.viewmodel.DetailStoryViewModelFactory
import com.ryan.storyapp.viewmodel.RegisterViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var detailStoryViewModel: DetailStoryViewModel
    private lateinit var binding: ActivityDetailStoryBinding
    private var isDescriptionBoxVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiationViewModel()
        fetchStoryDetails()
        setupUI()

        binding.container.setOnClickListener {
            if (isDescriptionBoxVisible) {
                hideDescriptionBox()
            } else {
                showDescriptionBox()
            }
            isDescriptionBoxVisible = !isDescriptionBoxVisible
        }
    }

    private fun showDescriptionBox() {
        val descriptionBox = binding.descriptionBox
        descriptionBox.animate().translationY(0f).setDuration(300).withStartAction {
            descriptionBox.visibility = View.VISIBLE
        }
    }

    private fun hideDescriptionBox() {
        val descriptionBox = binding.descriptionBox

        descriptionBox.animate().translationY(descriptionBox.height.toFloat()).setDuration(300)
            .withEndAction {
                descriptionBox.visibility = View.GONE
            }
    }

    private fun setupUI() {

        val loadingProgressBar = binding.progressBar
        val shimmerDescriptionBox = binding.descriptionBoxShimmer
        val shimmerDescriptionBox2 = binding.descriptionBoxShimmer2
        val shimmerDescriptionBox3 = binding.descriptionBoxShimmer3
        val username = binding.tvDetailName
        val description = binding.tvDetailDescription
        val uploadTime = binding.uploadTime

        loadingProgressBar.visibility = View.VISIBLE
        username.visibility = View.INVISIBLE
        description.visibility = View.INVISIBLE
        uploadTime.visibility = View.INVISIBLE

        shimmerDescriptionBox.startShimmer()
        shimmerDescriptionBox2.startShimmer()
        shimmerDescriptionBox3.startShimmer()
    }

    private fun fetchStoryDetails() {
        val storyId = intent.getStringExtra("story_id")

        detailStoryViewModel.fetchDetailStory(storyId.toString())

        detailStoryViewModel.detailStory.observe(this@DetailStoryActivity) { result ->
            when (result) {
                is ResultViewModel.DetailSuccess -> {
                    handleDetailStorySuccess(result.data)
                }

                is ResultViewModel.Error -> {
                    handleDetailStoryError(result.message)
                }

                else -> {
                    handleUnknownError()
                }
            }
        }
    }

    private fun handleDetailStorySuccess(story: Story) {
        val imageUrl = story.photoUrl
        binding.apply {
            ivDetailPhoto.loadImage(url = imageUrl.toString())
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvDetailDescription.setOnClickListener {
                if (tvDetailDescription.maxLines == Int.MAX_VALUE) {
                    tvDetailDescription.maxLines = 2
                    tvDetailDescription.ellipsize = TextUtils.TruncateAt.END
                } else {
                    tvDetailDescription.maxLines = Int.MAX_VALUE
                    tvDetailDescription.ellipsize = null
                }
            }
        }
        binding.uploadTime.text = DateUtils.calculateTimeAgo(this, story.createdAt)

        hideShimmerAnimations()
    }

    private fun handleDetailStoryError(errorMessage: String) {
        Toast.makeText(this, "${getString(R.string.error)}: $errorMessage", Toast.LENGTH_SHORT)
            .show()
        hideShimmerAnimations()
    }

    private fun handleUnknownError() {
        Toast.makeText(this, getString(R.string.invalid_type), Toast.LENGTH_SHORT).show()
    }

    private fun hideShimmerAnimations() {
        binding.apply {
            progressBar.visibility = View.GONE
            descriptionBoxShimmer.stopShimmer()
            descriptionBoxShimmer2.stopShimmer()
            descriptionBoxShimmer3.stopShimmer()
            descriptionBoxShimmer.visibility = View.GONE
            descriptionBoxShimmer2.visibility = View.GONE
            descriptionBoxShimmer3.visibility = View.GONE
            tvDetailName.visibility = View.VISIBLE
            tvDetailDescription.visibility = View.VISIBLE
            uploadTime.visibility = View.VISIBLE
        }
    }

    private fun initiationViewModel() {
        val repository = DetailStoryRepository(this)
        val viewModelFactory = DetailStoryViewModelFactory(repository)
        detailStoryViewModel =
            ViewModelProvider(this, viewModelFactory)[DetailStoryViewModel::class.java]
    }
}