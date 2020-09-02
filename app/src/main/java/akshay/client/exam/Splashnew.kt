package akshay.client.exam
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager


class Splashnew : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splashnew)
        Handler().postDelayed({
                    if (!isNetworkAvailable()){
                        val intent = Intent(this@Splashnew,NointernetActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
                            Context.MODE_PRIVATE)
                        val sharedname = sharedPreferences.getString("first_name","defaultname")
                        val sharedusr = sharedPreferences.getString("usr_rollno","defaultname")

                        if (sharedname!!.equals("defaultname") && sharedusr.equals("defaultname")){
                            val intent = Intent(this@Splashnew,PhoneActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                            finish()
                        }else{
                            val intent = Intent(this@Splashnew,MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                            finish()
                        }
                    }



        },2000)
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}