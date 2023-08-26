package com.example.firebasekotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasekotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using Data Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Now you can access the views using Data Binding, e.g., binding.textView.text = "Hello!"
    }
}
