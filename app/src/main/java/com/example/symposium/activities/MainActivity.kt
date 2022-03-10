package com.example.symposium.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.example.symposium.R
import com.example.symposium.databinding.ActivityMainBinding
import com.example.symposium.databinding.NavigationHeaderBinding
import com.example.symposium.firebase.FirestoreHandler
import com.example.symposium.models.User
import com.example.symposium.utils.BaseActivity

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerBinding : NavigationHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        val headerView = binding.navView.getHeaderView(0)
        headerBinding = NavigationHeaderBinding.bind(headerView)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar()
        setNightMode()
        headerBinding.ivNightMode.setOnClickListener(this)
        setNightModeButtonImage()
        FirestoreHandler().getUserData(this)

        binding.navView.setNavigationItemSelectedListener {
            navigationView(it)
            true
        }
    }

    //TODO Set change name functionality

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivNightMode -> {
                changeNightMode()
                setNightModeButtonImage()
            }
        }
    }

    fun updateNavigationUserDetails(user: User) {
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(headerBinding.ivNavigationHeaderImage)
        headerBinding.tvHeaderUserName.text = user.name
    }

    private fun setToolbar() {
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            binding.toolbarMain, R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle.syncState()
    }



    private fun setNightModeButtonImage() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isDayModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)
        Log.i("fdf", isDayModeOn.toString())
        if (isDayModeOn) {
            headerBinding.ivNightMode.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.ic_night_mode_on)
            )
        } else {
            headerBinding.ivNightMode.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.ic_day_mode_on)
            )
        }
    }

    private fun changeNightMode() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isDayModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isDayModeOn) {
            turnOnDarkMode()
        } else {
            turnOffDarkMode()
        }
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