package akshay.client.exam

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import akshay.client.exam.R
import kotlinx.android.synthetic.main.activity_exam_portal.*
import akshay.client.exam.services.CheckoutService
import akshay.client.exam.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExamPortal : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    lateinit var context: Context
    var START_MILLI_SECONDS = 60000L
    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L
    var timeoutlog: Boolean = false


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_exam_portal)
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val sharedphonevalue = sharedPreferences.getString("phonenum","defaultname")
        var countdown = sharedPreferences.getInt("countdown",0)
        if (sharedphonevalue.equals("defaultname")){
            val i = Intent(this@ExamPortal,MainActivity::class.java)
            startActivity(i)
            finish()
        }
        var START_MILLI_SECONDS = 60000L
        lateinit var countdown_timer: CountDownTimer
        var isRunning: Boolean = false;
        var time_in_milli_seconds = 0L
        context = this
        time_in_milli_seconds = countdown.toLong() *60000L
        if (!isNetworkAvailable()){
            val intent = Intent(this@ExamPortal,NointernetActivity::class.java)
            startActivity(intent)
            finish()
        }
        startTimer(time_in_milli_seconds)
        loadDestination(sharedphonevalue)

    }

    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                timeoutlog = true
                btn_next.background = resources.getDrawable(R.drawable.redzone)
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI(time_in_seconds)
            }
        }
        countdown_timer.start()

        isRunning = true
    }


    private fun updateTextUI(timeInSeconds: Long) {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60
        var halfval = timeInSeconds * 0.5
        var onepot =  timeInSeconds * 0.25
        var pot = timeInSeconds * 0.15
        //Log.d("time in seconds",timeInSeconds.toString())
        // Log.d("time_in_milli_seconds",time_in_milli_seconds.toString())
        if (halfval > time_in_milli_seconds){
            countid.background = resources.getDrawable(R.drawable.yellow_warning)
        }
        if (onepot > time_in_milli_seconds){
            countid.background = resources.getDrawable(R.drawable.orangezone)
        }
        if (pot > time_in_milli_seconds){
            countid.background = resources.getDrawable(R.drawable.redzone)
        }
        countid.text = "$minute:$seconds"
    }





    private fun loadDestination(sharedphonevalue: String?) {
        lateinit var destinationList: List<Question>
        val checkoutService = ServiceBuilder.buildService(
            CheckoutService::class.java)

        val filter = HashMap<String, String>()
//        filter["country"] = "India"
//        filter["count"] = "1"

        val requestCall = checkoutService.getCheckoutList(sharedphonevalue,filter)

        requestCall.enqueue(object : Callback<List<Question>> {

            // If you receive a HTTP Response, then this method is executed
            // Your STATUS Code will decide if your Http Response is a Success or Error
            override fun onResponse(
                call: Call<List<Question>>,
                response: Response<List<Question>>
            ) {
                if (response.isSuccessful) {
                    // Your status code is in the range of 200's
                    var index = 0
                    destinationList = response.body()!!
                    val totalQtn = destinationList.size
                    sendQty(totalQtn)
                     Log.d("Data","Program has been initialized")
                    var score = 0
                    //startTimer(time_in_milli_seconds)
                    Log.d("total Quations:", destinationList.size.toString())
                    DataLoader().getQuations(destinationList, index)

                    var ansText = ""
                    Log.d("Data","Program has been initialized with DataLoader")
                    val selection = findViewById<RadioGroup>(R.id.rg_choice)
                    btn_next.setOnClickListener {
                        selection.setOnCheckedChangeListener { group, checkedId ->
                            if(rg_choice.checkedRadioButtonId ==-1){
                               // Toast.makeText(getApplicationContext(), "Please select one choice", Toast.LENGTH_SHORT).show()
                            }else{
                                val uans = findViewById<RadioButton>(rg_choice.checkedRadioButtonId)
                                 ansText = uans.text.toString()
                                Log.d("Ans no",ansText.toString())
                                //Log.d("lib ans", destinationList[--index].ans)
                               // Log.d("Real data", destinationList[index].Answer.toString())

                                if (ansText == destinationList[--index].ans){
                                    score++
                                    ++index
                                    Log.d("score", score.toString())
                                }else{
                                    ++index
                                    Log.d("score", score.toString())
                                }

                            }
                        }

                        if (btn_next.text == "Finish" || timeoutlog){
                            Log.d("Status","Exam Finished")
                            selection.setOnCheckedChangeListener { group, checkedId ->
                                if(rg_choice.checkedRadioButtonId ==-1){
                                     Toast.makeText(getApplicationContext(), "Please select one choice", Toast.LENGTH_SHORT).show()
                                }else{
                                    val uans = findViewById<RadioButton>(rg_choice.checkedRadioButtonId)
                                    ansText = uans.text.toString()
                                    Log.d("Ans no",ansText.toString())
                                    //Log.d("lib ans", destinationList[--index].ans)
                                    // Log.d("Real data", destinationList[index].Answer.toString())

                                    if (ansText == destinationList[--index].ans){
                                        val marks = destinationList[index].marks
                                        score++
                                        ++index
                                        Log.d("score", score.toString())
                                    }else{
                                        ++index
                                        Log.d("score is npt", score.toString())
                                    }

                                }
                            }
                            val sharedPreferences : SharedPreferences = getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putInt("pastScore", score)
                            editor.apply()
                            val intent = Intent(this@ExamPortal,
                                TestResult::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            index++
                            rg_choice.clearCheck()
                            DataLoader().getQuations(destinationList, index)
                        }

                    }

                } else if (response.code() == 401) {
                    Log.d("fail", "Application Level failure")
                    // Toast.makeText(this@HomeFragment,
                    //  "Your session has expired. Please Login again.", Toast.LENGTH_LONG).show()
                } else { // Application-level failure
                    // Your status code is in the range of 300's, 400's and 500's
                    // Toast.makeText(this@HomeFragment, "Failed to retrieve items", Toast.LENGTH_LONG).show()

                }
            }

            // Invoked in case of Network Error or Establishing connection with Server
            // or Error Creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Question>>, t: Throwable) {


                Log.d("Stack","No Data Server Available error code 500 ")
                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle("You are in Offline Mode")
                //set message for alert dialog
                builder.setMessage("Shall procced test in offline")


                //performing positive action
                builder.setPositiveButton("Yes"){dialogInterface, which ->
                    Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()

                /*Still no code alive*/

                }
                //performing cancel action
                builder.setNeutralButton("Cancel"){dialogInterface , which ->
                    Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
                }
                //performing negative action
                builder.setNegativeButton("No"){dialogInterface, which ->
                    Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()

            }
        })
    }

    private fun sendQty(totalQtn: Int) {
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("total",totalQtn)
        editor.apply()
        editor.commit()
    }


    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Are you sure !")
        dialog.setMessage("Do you want to quit the Exam ?")
        dialog.setPositiveButton("Yes") { dialog, which ->
            dialog?.dismiss()
            super.onBackPressed()
        }
        dialog.setNegativeButton("No") { dialog, which -> dialog?.dismiss() }
        dialog.show()
    }

    inner class DataLoader {
        @SuppressLint("SetTextI18n")
        fun getQuations(
            destinationList: List<Question>,
            index: Int
        ) {


            if (index < destinationList.size) {

                if (index < destinationList.size) {
                    tv_marks.text = "Marks:"+destinationList[index].marks
                    tv_qustion.text = destinationList[index].Question
                    rd_choice1.text = destinationList[index].Option1
                    rd_choice2.text = destinationList[index].Option2
                    rd_choice3.text = destinationList[index].Option3
                    rd_choice4.text = destinationList[index].Option4


                }

                if ((index + 1) == destinationList.size){
                    btn_next.text = "Finish"
                    //  val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    //   val editor = preferences.edit()
                    // editor.putInt("pastScore", score)
                    // editor.apply()
                }

            }
        }

    }
}