package com.who.calmcove

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.who.calmcove.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.buttonGenerateHelp.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.scale_button)
            binding.buttonGenerateHelp.startAnimation(animation)
            val name = binding.editTextName.text.toString()
            val mood = binding.editTextMood.text.toString()
            val prob = binding.editTextProb.text.toString()
            val jsonData = "{\"model\": \"text-davinci-003\", " +
                    "\"prompt\": \"My name is " + name + " . I am feeling really " + mood + " . If I am at this state help me. My problems are " +
                    prob + " Provide support telling me how to deal. HOW to Deal. call me by my name. don't forget to mention " +
                    "the problems, make it fun if possible.\", " +
                    "\"max_tokens\": 500, " +
                    "\"temperature\": 0}"
            callAzureFunctionAsync(jsonData) { response ->
                if (response?.status == 200) {
                    val outputText = response.output
//                    binding.editTextName.setText(prob)
                    val intent = Intent(this, HelpActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("op", outputText)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    // Handle the error or no response scenario here
                    showToast("error")
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
data class AzureFunctionResponse(val output: String, val status: Int)
fun makeRequestToAzureFunction(jsonData: String): String? {
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = jsonData.toRequestBody(mediaType)
    val request = Request.Builder()
        .url("https://dotnetfirst.azurewebsites.net/api/completionAPI?") // Replace with your Azure Function URL
        .post(requestBody)
        .build()
    try {
        val response = client.newCall(request).execute()
        return response.body?.string()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}
fun callAzureFunctionAsync(jsonData: String, callback: (AzureFunctionResponse?) -> Unit) {
    object : AsyncTask<Void, Void, AzureFunctionResponse?>() {
        override fun doInBackground(vararg params: Void?): AzureFunctionResponse? {
            val responseText = makeRequestToAzureFunction(jsonData)
            return if (responseText != null) {
                AzureFunctionResponse(responseText, 200) // Assuming success with 200 status code
            } else {
                AzureFunctionResponse("nahin hua bsdk", -1) // Error occurred, return an empty response
            }
        }

        override fun onPostExecute(result: AzureFunctionResponse?) {
            callback(result)
        }
    }.execute()
}

