package my.client.dgsv

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.client.dgsv.R
import kotlinx.android.synthetic.main.activity_test_result.*
import my.client.dgsv.Module.Results
import my.client.dgsv.services.ServiceBuilder
import my.client.dgsv.services.UsersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestResult : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_result)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val highScore = sharedPreferences.getInt("pastScore", 0)
        val total  = sharedPreferences.getInt("total",0)
        val firstname = sharedPreferences.getString("first_name","defaultname")
        val lastname = sharedPreferences.getString("last_name","defaultname")
        val phonenum = sharedPreferences.getString("phonenum","defaultname")
        val rollno = sharedPreferences.getString("usr_rollno","defaultname")
        val message = "Namaste!,\n  $firstname have done Online Exam Sucessfully, \n He/She got $highScore / $total "
        loadResult(firstname, lastname, highScore,total, phonenum, rollno)
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
        rollno: String?
    ) {
        val newUser = Results()
        newUser.Stname = "$firstname $lastname"
        newUser.Marks = highScore.toString()
        newUser.Phonenum = phonenum.toString()
        newUser.Rollno = rollno.toString()


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