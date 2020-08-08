package my.client.dgsv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.client.dgsv.R
import kotlinx.android.synthetic.main.activity_nointernet.*

class NointernetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nointernet)
        btnreload.setOnClickListener {
            val intent = Intent(this@NointernetActivity,
                Splashnew::class.java)
            startActivity(intent)
            finish()
        }
    }
}