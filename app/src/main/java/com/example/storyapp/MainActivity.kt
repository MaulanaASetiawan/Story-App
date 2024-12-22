package com.example.storyapp

import android.animation.ObjectAnimator
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.data.pref.UserPref
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.home.HomeActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.ui.widget.ImageBannerWidget
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLoginStatus()
        updateWidget()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun checkLoginStatus() {
        userPref = UserPref.getInstance(dataStore)
        lifecycleScope.launch {
            userPref.getToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this, ImageBannerWidget::class.java))
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view)

        val intent = Intent(this, ImageBannerWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

        sendBroadcast(intent)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}