package com.dicoding.mysubmissionintermediate.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mysubmissionintermediate.R
import com.dicoding.mysubmissionintermediate.databinding.ActivityMainBinding
import com.dicoding.mysubmissionintermediate.ui.FactoryViewModel
import com.dicoding.mysubmissionintermediate.ui.adapter.AdapterStory
import com.dicoding.mysubmissionintermediate.ui.adapter.LoadingState
import com.dicoding.mysubmissionintermediate.ui.detail.DetailActivity
import com.dicoding.mysubmissionintermediate.ui.login.LoginActivity
import com.dicoding.mysubmissionintermediate.ui.maps.MapsActivity
import com.dicoding.mysubmissionintermediate.ui.upl.UploadActivity


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<ViewModelMain> {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterStory: AdapterStory
    private lateinit var mainViewModel: ViewModelMain



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> startActivity(Intent(this, MapsActivity::class.java))
            R.id.logout -> showLogoutConfirmationDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story Apps"

        setupView()
        setupRecyclerView()
        setupObservers()

        onBackPressedDispatcher.addCallback(this) {
            showExitConfirmationDialog()
        }

        mainViewModel = ViewModelProvider(this)[ViewModelMain::class.java]

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            supportActionBar?.show()
        }
    }

    private fun setupRecyclerView() {
        adapterStory = AdapterStory { story, ivItemPhoto, tvItemName, tvItemDescription ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_STORY_ID", story.id)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(ivItemPhoto, "shared_image"),
                Pair(tvItemName, "shared_name"),
                Pair(tvItemDescription, "shared_description")
            )
            startActivity(intent, options.toBundle())
        }

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = adapterStory.withLoadStateHeaderAndFooter(
                header = LoadingState { adapterStory.retry() },
                footer = LoadingState { adapterStory.retry() }
            )
        }
    }

    private fun setupObservers() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                loadStories(user.token)
            }
        }
    }

    private fun loadStories(token: String) {
        viewModel.getStoryList(token).observe(this) { pagingData ->
            adapterStory.submitData(lifecycle, pagingData)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout)
            .setMessage(R.string.confirmation_logout)
            .setPositiveButton(R.string.done) { _, _ ->
                viewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_apl)
            .setMessage(R.string.confirmation_logout)
            .setPositiveButton(R.string.done) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}