package akshay.client.exam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import akshay.client.exam.R
import kotlinx.android.synthetic.main.activity_test_result.*
import akshay.client.exam.Module.Results
import akshay.client.exam.services.ServiceBuilder
import akshay.client.exam.services.UsersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TestResult : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val currentTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val cal = Calendar.getInstance()
        val current = cal.time.toString()
        Log.d("Current Datetime",current.toString())
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        val todayDate = Date()
        val thisDate: String = currentDate.format(todayDate)
        Log.d("Date : ", thisDate.toString())
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("pastScore", 0)
        var total  = sharedPreferences.getInt("total",0)
        total -= 1
        val firstname = sharedPreferences.getString("first_name","defaultname")
        val lastname = sharedPreferences.getString("last_name","defaultname")
        val phonenum = sharedPreferences.getString("phonenum","defaultname")
        val rollno = sharedPreferences.getString("usr_rollno","defaultname")
        val message = "Namaste!,\n  $firstname has done Online Exam Sucessfully, \n He/She got $highScore / $total "
        loadResult(firstname, lastname, highScore,total, phonenum, rollno, thisDate, currentTime)
        btn_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${message} \n http://akshaydigital.in")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

    }

    private fun loadResult(
        firstname: String?,
        lastname: String?,
        highScore: Int,
        total: Int,
        phonenum: String?,
        rollno: String?,
        thisdate: String,
        currentTime: String
    ) {
        val newUser = Results()
        newUser.Stname = "$firstname $lastname"
        newUser.Marks = highScore.toString()
        newUser.Phonenum = phonenum.toString()
        newUser.Rollno = rollno.toString()
        newUser.dateofexam = thisdate
        newUser.timeofexam = currentTime


        val checkoutService = ServiceBuilder.buildService(UsersService::class.java)
        val requestCall = checkoutService.sendResult(phonenum!!,newUser)

        requestCall.enqueue(object : Callback<Results> {

            override fun onResponse(call: Call<Results>, response: Response<Results>) {
                if (response.isSuccessful) {

                    var newlyCreatedDestination = response.body() // Use it or ignore it
                    val text = "Marks: " + highScore
                    val total_marks = "Total: $total"
                    tv_score.text = text
                    tv_total.text = total_marks
                     Toast.makeText(this@TestResult, "Successfully Added", Toast.LENGTH_SHORT).show()
                        if (total_marks == text){
                            /*Congratulation Greetings*/

                        }
                } else {
                     Toast.makeText(this@TestResult, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Results>, t: Throwable) {
                 Toast.makeText(this@TestResult, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
        })
    }
}