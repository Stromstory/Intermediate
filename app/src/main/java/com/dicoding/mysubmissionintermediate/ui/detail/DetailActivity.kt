package com.dicoding.mysubmissionintermediate.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.data.response.Story
import com.dicoding.mysubmissionintermediate.data.result.ResultCode
import com.dicoding.mysubmissionintermediate.databinding.ActivityDetailBinding
import com.dicoding.mysubmissionintermediate.ui.FactoryViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModelDetail: ViewModelDetail by viewModels {
        FactoryViewModel.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivPhotoDetail.transitionName = "shared_image"
        binding.tvNameDetail.transitionName = "shared_name"
        binding.tvDescriptionDetail.transitionName = "shared_description"

        supportActionBar?.apply {
            title = getString(R.string.story_detail)
            setDisplayHomeAsUpEnabled(true)
        }

        val storyId = intent.getStringExtra("EXTRA_STORY_ID")
        if (storyId != null) {
            viewModelDetail.getStoryById(storyId)
            viewModelDetail.detailStory.observe(this) { result ->
                when (result) {
                    is ResultCode.Loading -> showLoading(true)
                    is ResultCode.Success -> {
                        showLoading(false)
                        populateStoryDetail(result.data.story) // Update to access the story from DetailResponse
                    }
                    is ResultCode.Error -> {
                        showLoading(false)
                        showError(result.error)
                    }
                }
            }
        } else {
            showError("StoryRepository ID not found")
        }
    }
    private fun populateStoryDetail(story: Story) {
        binding.tvNameDetail.text = story.name
        binding.tvDescriptionDetail.text = story.description
        Glide.with(this).load(story.photoUrl).into(binding.ivPhotoDetail)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.tvNameDetail.text = getString(R.string.detail_nothing)
        binding.tvDescriptionDetail.text = message
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}