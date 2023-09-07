package com.example.firebasekotlin

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.firebasekotlin.fragments.HomeFragment
import com.example.firebasekotlin.fragments.NotificationsFragment
import com.example.firebasekotlin.fragments.ProfileFragment
import com.example.firebasekotlin.fragments.SearchFragment
import com.example.firebasekotlin.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isCheckable=false
                startActivity(Intent(this@HomeActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }


    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

       moveToFragment(HomeFragment())




    }

    override fun onBackPressed() {
        val navView: BottomNavigationView = binding.navView

        if (currentFragment is HomeFragment) {
            // If current fragment is HomeFragment, close the app
            super.onBackPressed()
        } else {
            // Otherwise, navigate to HomeFragment
            moveToFragment(HomeFragment())

            // Highlight the Home icon in the BottomNavigationView
            navView.selectedItemId = R.id.nav_home
        }
    }

    private fun moveToFragment(fragment:Fragment){
        currentFragment = fragment
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container,fragment)
        fragmentTrans.commit()
    }

}