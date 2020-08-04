package my.client.dgsv

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.client.dgsv.R
import kotlinx.android.synthetic.main.activity_exam_portal.*
import my.client.dgsv.services.CheckoutService
import my.client.dgsv.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExamPortal : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    lateinit var context: Context



    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_portal)
        context = this
        loadDestination()

    }

    private fun loadDestination() {
        lateinit var destinationList: List<Question>
        val checkoutService = ServiceBuilder.buildService(
            CheckoutService::class.java)

        val filter = HashMap<String, String>()
//        filter["country"] = "India"
//        filter["count"] = "1"

        val requestCall = checkoutService.getCheckoutList(filter)

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
                     Log.d("Data","Program has been initialized")
                    var score = 0

                   /* Log.d("data", destinationList[index].Question)
                    Log.d("data", destinationList[index].Option1)
                    Log.d("data", destinationList[index].Option2)
                    Log.d("data", destinationList[index].Option3)
                    Log.d("data", destinationList[index].Option4)
                    Log.d("data", destinationList[index].ans)*/
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

                        if (btn_next.text == "Finish"){
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




    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Are you sure !")
        dialog.setMessage("Do you want to quit the application ?")
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