package com.who.calmcove

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*

class category_graph : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var barChart : BarChart
    private lateinit var barDataSet: BarDataSet
    private lateinit var barData : BarData
    private var barEntriesArrayList = ArrayList<BarEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_graph)
        barChart = findViewById(R.id.idBarChart)
        // Get a reference to the "categories_count" node in Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("categories_count")

        // Retrieve data from Firebase and show toast
        retrieveDataAndShowToast()


    }

    private fun retrieveDataAndShowToast() {
        // Add a ValueEventListener to get the data from Firebase
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Check if the "categories_count" node has the required children
                    if (snapshot.hasChild("Anxiety") && snapshot.hasChild("Depression") && snapshot.hasChild("Stress")) {
                        // Get the values of each child and show toast
                        val anxietyValue = (snapshot.child("Anxiety").value as Long).toFloat()
                        val depressionValue = (snapshot.child("Depression").value as Long).toFloat()
                        val stressValue = (snapshot.child("Stress").value as Long).toFloat()
                        barEntriesArrayList = ArrayList()
                        barEntriesArrayList.clear()
                        barEntriesArrayList.add(BarEntry(1f, anxietyValue))
                        barEntriesArrayList.add(BarEntry(4f, depressionValue))
                        barEntriesArrayList.add(BarEntry(7f, stressValue))

                        barDataSet = BarDataSet(barEntriesArrayList,"")

                        // creating a new bar data and
                        // passing our bar data set.
                        barData = BarData(barDataSet)

                        // below line is to set data
                        // to our bar chart.
                        barChart.data = barData

                        // adding color to our bar data set.
                        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

                        // setting text color.
                        barDataSet.valueTextColor = Color.BLACK
                        // Decrease the size of the chart (you can adjust the width and height as needed)
                        val legend: Legend = barChart.legend
                        legend.isEnabled = true
                        legend.textSize = 12f
                        legend.textColor = Color.BLACK
                        // setting text size
                        barDataSet.valueTextSize = 16f
                        barChart.description.isEnabled = false
                        barChart.setBackgroundColor(Color.WHITE)

                    }
                } else {
                    Toast.makeText(this@category_graph, "Database is empty", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors here
                Toast.makeText(this@category_graph, "Error retrieving data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
