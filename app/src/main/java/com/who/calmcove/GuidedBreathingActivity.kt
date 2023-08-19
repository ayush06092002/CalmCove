package com.who.calmcove

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class GuidedBreathingActivity : AppCompatActivity() {

    private var isBreathing = false
    private lateinit var breathingTimer: CountDownTimer
    private lateinit var videoView: VideoView
    private lateinit var startTimerButton: Button
    private lateinit var timerTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guided_breathing)
        auth = FirebaseAuth.getInstance()

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val imgMenu = findViewById<ImageView>(R.id.imgMenu)

        val navView = findViewById<NavigationView>(R.id.navDrawer)
        val navText = navView.getHeaderView(0).findViewById<TextView>(R.id.navTextName)
        navView.itemIconTintList = null
        imgMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_breathing -> {
                    intent = Intent(this, GuidedBreathingActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_journaling -> {
                    // Handle Journaling item click
                    // Open the Journaling activity here
                    true
                }
                R.id.menu_graph -> {
                    // Handle Graph item click
                    val intent = Intent(this, category_graph::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_signout -> {
                    intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    auth.signOut()
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    setLoginState(false)
                    startActivity(intent)
                    true
                }
                R.id.menu_notifications -> {
                    // Handle Notifications item click
                    // You can open the Notifications activity here
                    true
                }
                else -> false
            }
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isLoggedIn = sharedPreferences.getBoolean("login_state", false)
        if (!isLoggedIn) {
            // Redirect the user to the login activity if not logged in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        timerTextView = findViewById(R.id.timerTextView)
        startTimerButton = findViewById(R.id.startTimerButton)

        videoView = findViewById(R.id.videoView)

        startTimerButton.setOnClickListener {
            if (!isBreathing) {
                startBreathing()
            } else {
                stopBreathing()
            }
        }
    }
    private fun setLoginState(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("login_state", isLoggedIn)
        editor.apply()
    }
    private fun startBreathing() {
        isBreathing = true
        startTimerButton.text = "Stop Breathing"
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.breathing}")
        videoView.setVideoURI(videoUri)
        val totalDurationMillis = 60000L // Total duration of the breathing session (in milliseconds)
        val intervalMillis = 1000L // Interval between phases (in milliseconds)

        // Configure and start the breathing timer
        breathingTimer = object : CountDownTimer(totalDurationMillis, intervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = secondsRemaining.toString()

                // TODO: Change video content based on the current phase of breathing.
                // You'll need to calculate the phase based on the time left in the timer.
            }

            override fun onFinish() {
                isBreathing = false
                startTimerButton.text = "Start Breathing"
                timerTextView.text = "Done!"
//                videoView.stopPlayback() // Stop video playback at the end
            }
        }

        breathingTimer.start()
        videoView.start()
        // Loop the video
        videoView.setOnCompletionListener {
            videoView.start() // Restart video when it completes
        }

    }

    private fun stopBreathing() {
        isBreathing = false
        startTimerButton.text = "Start Breathing"
        breathingTimer.cancel()
        timerTextView.text = "Stopped"
        videoView.stopPlayback()
    }
}
