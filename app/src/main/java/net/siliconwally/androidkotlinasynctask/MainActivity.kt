package net.siliconwally.androidkotlinasynctask

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    val tag: String? = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            AsyncTaskExample(this).execute()
        }
    }
}

class AsyncTaskExample(private var activity: MainActivity?) : AsyncTask<String, Int, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        activity?.progressBar?.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: String?): String {

        var result = ""
        try {
            val url = URL("https://api.chucknorris.io/jokes/random")
            val httpURLConnection = url.openConnection() as HttpURLConnection

            httpURLConnection.readTimeout = 8000
            httpURLConnection.connectTimeout = 8000
            httpURLConnection.connect()

            val responseCode: Int = httpURLConnection.responseCode
            Log.d(activity?.tag, "responseCode - " + responseCode)

            if (responseCode == 200) {
                val inStream: InputStream = httpURLConnection.inputStream
                val isReader = InputStreamReader(inStream)
                val bReader = BufferedReader(isReader)
                var tempStr: String?

                try {
                    while (true) {
                        tempStr = bReader.readLine()
                        if (tempStr == null) {
                            break
                        }
                        result += tempStr
                    }
                } catch (Ex: Exception) {
                    Log.e(activity?.tag, "Error in convertToString " + Ex.printStackTrace())
                }
            }
        } catch (ex: Exception) {
            Log.d("", "Error in doInBackground " + ex.message)
        }
        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        activity?.progressBar?.visibility = View.INVISIBLE
        if (result == "") {
            activity?.mensaje?.text = activity?.getString(R.string.network_error)
        } else {
            var parsedResult = ""
            var jsonObject: JSONObject? = JSONObject(result)
            parsedResult += "Value: : " + (jsonObject?.get("value")) + "\n"
            parsedResult += "Created at: " + (jsonObject?.get("created_at")) + "\n"
            activity?.mensaje?.text = parsedResult
        }
    }
}
