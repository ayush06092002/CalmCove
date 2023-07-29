package com.who.calmcove

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.who.calmcove.databinding.ActivityHelpBinding


class HelpActivity : AppCompatActivity() {

    private lateinit var output: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val bundle = intent.extras
        val stuff: String? = bundle?.getString("op")
        output = findViewById(R.id.editHelp)
        output.setText(stuff)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
