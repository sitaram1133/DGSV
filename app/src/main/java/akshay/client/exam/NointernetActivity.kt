package akshay.client.exam

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import akshay.client.exam.R
import kotlinx.android.synthetic.main.activity_nointernet.*

class NointernetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nointernet)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnreload.setOnClickListener {
            val intent = Intent(this@NointernetActivity,
                Splashnew::class.java)
            startActivity(intent)
            finish()
        }
    }
}