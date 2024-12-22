package com.example.storyapp.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        val storyName = intent.getStringExtra(EXTRA_STORY_NAME)
        val storyDescription = intent.getStringExtra(EXTRA_STORY_DESCRIPTION)
        val storyImageUrl = intent.getStringExtra(EXTRA_STORY_IMAGE_URL)

        observeViewModel(storyId, storyName, storyDescription, storyImageUrl)
    }

    private fun observeViewModel(
        storyId: String?, storyName: String?,
        storyDescription: String?, storyImageUrl: String?
    ) {
        if (storyId != null) {
            viewModel.getDetailStory(storyId).observe(this) { storyDetail ->
                if (storyDetail != null) {
                    binding.tvDetailName.text = storyName
                    binding.tvDetailDescription.text = storyDescription
                    Glide.with(this)
                        .load(storyImageUrl)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .into(binding.ivDetailPhoto)
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
        const val EXTRA_STORY_IMAGE_URL = "extra_story_image_url"
    }
}