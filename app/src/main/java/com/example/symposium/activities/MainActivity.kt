package com.example.symposium.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.symposium.R
import com.example.symposium.databinding.ActivityMainBinding
import com.example.symposium.utils.BaseActivity

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen(window)

        setToolbar()

        binding.navView.setNavigationItemSelectedListener {
            navigationView(it)
            true
        }
    }

    //TODO Set log out functionality
    //TODO Set night mode functionality
    //TODO Set Users data and avatar show functionality
    //TODO Set change name functionality

    override fun onClick(v: View?) {

    }

    private fun setToolbar() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout,
        binding.toolbarMain, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle.syncState()
    }

    private fun navigationView(it: MenuItem) {
        when (it.itemId) {
            R.id.menuSettings -> {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
            R.id.menuAccount -> {
                startActivity(Intent(applicationContext, AccountActivity::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}