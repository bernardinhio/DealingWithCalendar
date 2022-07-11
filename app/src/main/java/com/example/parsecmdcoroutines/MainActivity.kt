package com.example.parsecmdcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parsecmdcoroutines.databinding.ActivityMainBinding

/**
 * Create dropdown menu
 * https://developer.android.com/guide/topics/ui/controls/spinner
 *
 * Populate dynamically
 * https://stackoverflow.com/questions/22479597/populate-android-spinner-dynamically
 *
 */
class MainActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}