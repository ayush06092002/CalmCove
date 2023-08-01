package com.who.calmcove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User

data class CategoryCount(
    var anxietyCount: Int = 0,
    var depressionCount: Int = 0,
    var stressCount: Int = 0
)

class category_selection : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var categoryCount: CategoryCount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        database = FirebaseDatabase.getInstance().reference
        categoryCount = CategoryCount()

        val checkBoxAnxiety = findViewById<CheckBox>(R.id.checkBoxAnxiety)
        val checkBoxDepression = findViewById<CheckBox>(R.id.checkBoxDepression)
        val checkBoxStress = findViewById<CheckBox>(R.id.checkBoxStress)
        val buttonSubmitCategories = findViewById<Button>(R.id.buttonSubmitCategories)

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
