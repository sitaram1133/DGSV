package my.client.dgsv


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.client.dgsv.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import my.client.dgsv.Module.UserSession
import my.client.dgsv.services.ServiceBuilder
import my.client.dgsv.services.SessionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    var selectionsub: String = ""
    var interval: Int? = 0
    val list: ArrayList<String> = ArrayList()
    val phonumlist: ArrayList<String> = ArrayList()
    val qlimitlist: ArrayList<String> = ArrayList()
    val intervallist: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val sharedorgvalue = sharedPreferences.getString("usr_org","defaultname")
        val destinationService = ServiceBuilder.buildService(SessionService::class.java)
        val filter = HashMap<String, String>()
        val requestCall = destinationService.getSession(sharedorgvalue!!,filter)

        requestCall.enqueue(object: Callback<List<UserSession>> {

            // If you receive a HTTP Response, then this method is executed
            // Your STATUS Code will decide if your Http Response is a Success or Error
            override fun onResponse(call: Call<List<UserSession>>, response: Response<List<UserSession>>) {
                if (response.isSuccessful) {
                    // Your status code is in the range of 200's
                    var count = 0
                    var index = 0
                    val destinationList = response.body()!!
                    Log.d("Total",destinationList.size.toString())
                    while (count < destinationList.size){
                        list.add(destinationList[index].Subject)
                        phonumlist.add(destinationList[index].phonenum)
                        qlimitlist.add(destinationList[index].qlimit.toString())
                        intervallist.add(destinationList[index].examinterval.toString())

                        count++
                        if (count == destinationList.size){
                                SpinData()
                           // Log.d("Spindata",SpinData().toString())
                           // SelectedData(interval, phonenum, totalqtn)
                        }else{
                            index++
                        }
                    }
                } else if(response.code() == 401) {
                    Log.d("fail","Application Level failure")
                    // Toast.makeText(this@HomeFragment,
                    //  "Your session has expired. Please Login again.", Toast.LENGTH_LONG).show()
                } else { // Application-level failure
                    // Your status code is in the range of 300's, 400's and 500's
                    Toast.makeText(this@MainActivity, "Failed to retrieve items", Toast.LENGTH_LONG).show()

                }
            }

            // Invoked in case of Network Error or Establishing connection with Server
            // or Error Creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<UserSession>>, t: Throwable) {


            }
        })

        textview.text = "Namaste!"


        btnstart.setOnClickListener { view ->
                    val intent = Intent(this@MainActivity,ExamPortal::class.java)
                    startActivity(intent)
                   // finish()
                     Log.d("btnstatus","sstart")
                 }

    }

    private fun SelectedData(interval: Int?, phonenum: String, totalqtn: Int?){

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("countdown",interval!!.toInt())
        editor.putString("phonenum",phonenum)
        editor.putInt("total",totalqtn!!)
        editor.apply()
        editor.commit()
        Log.d("Status",phonenum)
    }


    private fun SpinData(): Int {
        val spinningdata = list
        var current_position = 0
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, spinningdata
            )
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("Info","Item selected")
                current_position = position
                val phone = phonumlist[current_position]
                val qlimit = qlimitlist[current_position].toInt()
                val interval = intervallist[current_position].toInt()
                Log.d("Cureent Data","$phone \n $qlimit \n $interval ")
                SelectedData(interval, phone, qlimit)
            }

        }
        val text = spinner.selectedItem.toString()
        return current_position
    }
}