package com.who.calmcove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class QuoteData(val quote: String?, val author: String?, val category: String?)

class category_selection : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var quote: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        database = FirebaseDatabase.getInstance().reference

        val checkBoxAnxiety = findViewById<CheckBox>(R.id.checkBoxAnxiety)
        val checkBoxDepression = findViewById<CheckBox>(R.id.checkBoxDepression)
        val checkBoxStress = findViewById<CheckBox>(R.id.checkBoxStress)
        val buttonSubmitCategories = findViewById<Button>(R.id.buttonSubmitCategories)

        quote = findViewById<EditText>(R.id.categ)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://andruxnet-random-famous-quotes.p.rapidapi.com/?cat=famous&count=1")
            .get()
            .addHeader("X-RapidAPI-Key", "c12575f727msh5fd9c9f1d25998dp1916e9jsnefa93e286d17")
            .addHeader("X-RapidAPI-Host", "andruxnet-random-famous-quotes.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                // Parse JSON response as an array
                val jsonArray = JSONArray(responseBody)
                if (jsonArray.length() > 0) {
                    val quoteObject = jsonArray.getJSONObject(0) // Assuming you want the first quote

                    val quoteText = quoteObject.getString("quote")

                    // Now you have the quote text, you can update the UI
                    runOnUiThread {
                        quote.setText(quoteText)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }
        })


        buttonSubmitCategories.setOnClickListener {
            val selectedCategories = mutableListOf<String>()

            // Add the selected categories to the list (e.g., "Anxiety", "Depression", "Stress")
            if (checkBoxAnxiety.isChecked) {
                selectedCategories.add("Anxiety")
            }
            if (checkBoxDepression.isChecked) {
                selectedCategories.add("Depression")
            }
            if (checkBoxStress.isChecked) {
                selectedCategories.add("Stress")
            }


            // Update the category counts in the "categories_count" node
            for (category in selectedCategories) {
                val categoryCountRef = database.child("categories_count").child(category)
                categoryCountRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val currentCount = dataSnapshot.getValue(Int::class.java) ?: 0
                        categoryCountRef.setValue(currentCount + 1)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error if necessary
                    }
                })
            }

            // Start a new activity or perform any other actions as needed
            val intent = Intent(this@category_selection, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
