package my.client.dgsv

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.client.dgsv.R
import kotlinx.android.synthetic.main.activity_test_result.*

class TestResult : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("pastScore", 0)
        val text = "Past Score : " + highScore
        tv_score.text = text
        ratingBar.rating = (highScore.toFloat() / 2.0).toFloat()
        ratingBar.rating = highScore.toFloat()

    }
}