
package com.example.storyapp.ui.home

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.ViewModelFactory
import com.example.storyapp.databinding.ActivityHomeBinding
import com.example.storyapp.ui.adapter.StoriesAdapter
import com.example.storyapp.data.pref.UserPref
import com.example.storyapp.dataStore
import com.example.storyapp.ui.addstory.AddStoryActivity
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.widget.ImageBannerWidget
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var storyAdapter: StoriesAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_local -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                lifecycleScope.launch {
                    val userPref = UserPref.getInstance(dataStore)
                    userPref.clearToken()

                    Snackbar.make(binding.root, R.string.logout_info, Snackbar.LENGTH_SHORT).show()
                    updateWidget()
                    delay(1000)
                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyAdapter = StoriesAdapter()
        binding.rvStory.adapter = storyAdapter
        binding.rvStory.layoutManager = LinearLayoutManager(this)

        observeViewModel()
        updateWidget()

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { pagingData ->
            if (pagingData != null) {
                storyAdapter.submitData(lifecycle, pagingData)
            } else {
                Log.e("HomeActivity", "Error: Received null data from viewModel.stories")
                Toast.makeText(this, "Error: Received null data", Toast.LENGTH_SHORT).show()
            }
        }

        storyAdapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility = if (loadState.refresh is LoadState.Loading && storyAdapter.itemCount == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }

            if (loadState.refresh is LoadState.Error) {
                val error = (loadState.refresh as LoadState.Error).error
                Log.e("HomeActivity", "Error: ${error.message}")
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this, ImageBannerWidget::class.java))
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view)

        val intent = Intent(this, ImageBannerWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        Log.d("Loginctivity", "Sending broadcast for widget update")

        sendBroadcast(intent)
    }
}