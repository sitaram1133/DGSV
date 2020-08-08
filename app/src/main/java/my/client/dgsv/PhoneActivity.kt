package my.client.dgsv

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.client.dgsv.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_phone.*
import java.util.*

class PhoneActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_phone)

        fun Random.nextInt(range: IntRange): Int {
            return range.start + nextInt(range.last - range.start)
        }


        btnSubmit.setOnClickListener { view ->
            val phone = phoneid!!.text.toString().trim()
            if (phone.length != 10){
                Snackbar.make(view, "Please Enter 10 Digit Number", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                val intent = Intent(this@PhoneActivity, OtpActivity::class.java)
                intent.putExtra("phone",phone)
                startActivity(intent)
                finish()
            }
        }


    }
}