package akshay.client.exam

import akshay.client.exam.Module.Userinfo
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import akshay.client.exam.services.ServiceBuilder
import akshay.client.exam.services.UsersService
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_phone.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PhoneActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_phone)
        indeterminateBar.visibility = View.INVISIBLE
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        fun Random.nextInt(range: IntRange): Int {
            return range.start + nextInt(range.last - range.start)
        }


        btnSubmit.setOnClickListener { view ->
            val phone = phoneid!!.text.toString().trim()
            if (phone.length != 10){
                Snackbar.make(view, "Please Enter 10 Digit Number", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{

                /*check and get User*/
                if (!isNetworkAvailable()){
                    startActivity(Intent(this@PhoneActivity,NointernetActivity::class.java))
                    finish()
                }
                indeterminateBar.visibility = View.VISIBLE
                btnSubmit.isEnabled = false
                val destinationService = ServiceBuilder.buildService(UsersService::class.java)
                val filter = HashMap<String, String>()
                val requestCall = destinationService.getUser(phone,filter)

                requestCall.enqueue(object: Callback<List<Userinfo>> {
                    override fun onResponse(call: Call<List<Userinfo>>, response: Response<List<Userinfo>>) {
                        if (response.isSuccessful) {
                            val destinationList = response.body()!!
                            val index = 0
                            Log.d("Phone",phone)
                            if (destinationList.isNotEmpty()){
                                val remotephone = destinationList[index].stdphone
                                if (remotephone == phone){
                                    loginUser(phone)
                                }else{
                                    signupNewuser(phone)
                                }
                            }else{
                                signupNewuser(phone)
                            }


                        } else if(response.code() == 401) {
                            indeterminateBar.visibility = View.INVISIBLE
                            Log.d("fail","Application Level failure")
                            val errorcodes = response.code().toString()
                            //dialogTryagian(errorcodes)
                        } else { // Application-level failure
                            val errorcodes = response.code().toString()
                            //dialogTryagian(errorcodes)
                        }
                    }

                    // Invoked in case of Network Error or Establishing connection with Server
                    // or Error Creating Http Request or Error Processing Http Response
                    override fun onFailure(call: Call<List<Userinfo>>, t: Throwable) {
                        indeterminateBar.visibility = View.INVISIBLE
                        if (!isNetworkAvailable()){
                                startActivity(Intent(this@PhoneActivity,NointernetActivity::class.java))
                                finish()
                        }else{
                            val errorcodes = "internal Error: 8"
                            dialogTryagian(errorcodes)
                        }
                    }
                })



            }
        }


    }

    private fun loginUser(phone: String) {

        //fill the data to
            Log.d("Status","Log in Process.....")
        val destinationService = ServiceBuilder.buildService(UsersService::class.java)
        val filter = HashMap<String, String>()
        val requestCall = destinationService.getuserPreff(phone,filter)

        requestCall.enqueue(object: Callback<List<Userinfo>> {
            override fun onResponse(call: Call<List<Userinfo>>, response: Response<List<Userinfo>>) {
                if (response.isSuccessful) {
                    Log.d("remote","Remote Resp. Successfull")
                    var index = 0
                    val destinationList = response.body()
                        val uuid = destinationList!![index].uuid.toInt()
                        val firstname = destinationList[index].firstname
                        val middlename = destinationList[index].middlename
                        val lastname = destinationList[index].lastname
                        val address = destinationList[index].address
                        val fullname = destinationList[index].fullname
                        val organization = destinationList[index].organization
                        val birthdate = destinationList[index].birthdate
                        val standerd = destinationList[index].standerd
                        val rollnumber = destinationList[index].rollnumber
                        val loginstatus = destinationList[index].loginstatus
                        val stdphone = destinationList[index].stdphone



                    onLoginSuccess(uuid, firstname, middlename, lastname, address, fullname,
                        organization, birthdate, standerd, rollnumber, loginstatus, stdphone)
                } else if(response.code() == 401) {
                    indeterminateBar.visibility = View.INVISIBLE
                    btnSubmit.isEnabled = true
                    Log.d("fail","Application Level failure")
                    val errorcodes = response.code().toString()
                    dialogTryagian(errorcodes)
                } else { // Application-level failure
                    // Your status code is in the range of 300's, 400's and 500's
                    //Toast.makeText(this@MainActivity, "Server not Found", Toast.LENGTH_LONG).show()
                    val errorcodes = response.code().toString()
                    dialogTryagian(errorcodes)
                }
            }

            // Invoked in case of Network Error or Establishing connection with Server
            // or Error Creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Userinfo>>, t: Throwable) {
                indeterminateBar.visibility = View.INVISIBLE
                btnSubmit.isEnabled = true
                //Toast.makeText(this@MainActivity, "internal error 8", Toast.LENGTH_LONG).show()
                if (!isNetworkAvailable()){
                        startActivity(Intent(this@PhoneActivity,NointernetActivity::class.java))
                    finish()
                }else{
                    val errorcodes = "internal Error: 8"
                    dialogTryagian(errorcodes)
                }
            }
        })

    }

    private fun onLoginSuccess(
        uuid: Int,
        firstname: String,
        middlename: String,
        lastname: String,
        address: String,
        fullname: String,
        organization: String,
        birthdate: String,
        standerd: String,
        rollnumber: String,
        loginstatus: Boolean,
        stdphone: String
    ) {
        indeterminateBar.visibility = View.INVISIBLE
        btnSubmit.isEnabled = false
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("uuid_key",uuid)
        editor.putString("first_name",firstname)
        editor.putString("middle_name",middlename)
        editor.putString("last_name",lastname)

        editor.putString("usr_phone",stdphone)
        editor.putString("usr_org",organization)
        editor.putString("usr_rollno",rollnumber)
        editor.putString("usr_standerd",standerd)
        editor.clear()
        editor.apply()
        editor.commit()
        startActivity(Intent(this@PhoneActivity,MainActivity::class.java))
        finish()
    }

    private fun signupNewuser(phone: String) {
        if (!isNetworkAvailable()){
            startActivity(Intent(this@PhoneActivity,NointernetActivity::class.java))
            finish()
        }
        indeterminateBar.visibility = View.INVISIBLE
        btnSubmit.isEnabled = true
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val sharedphoneValue = sharedPreferences.getString("student_phone","defaultname")
        Log.d("Phone", sharedphoneValue)
        val random = Random().nextInt()
        val id = random
        editor.putInt("uuid",id)
        editor.putString("student_phone",phone)
        editor.clear()
        editor.apply()
        editor.commit()
        val intent = Intent(this@PhoneActivity, RegisterActivity::class.java)
        intent.putExtra("phone",phone)
        startActivity(intent)
        finish()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun dialogTryagian(errorcodes: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialogbox_roof, null)
        val errorsetter  = dialogLayout.findViewById<TextView>(R.id.errorsetter)
        val btnrefresh = dialogLayout.findViewById<Button>(R.id.btn_refresh)
        errorsetter.text = "Connection Error :$errorcodes"
        btnrefresh.setOnClickListener {
            val i = Intent(this@PhoneActivity,Splashnew::class.java)
            startActivity(i)
            finish()
        }
        builder.setView(dialogLayout)
        /* builder.setPositiveButton("OK") { dialogInterface, i ->
             val examduration = editText.text.toString()
         }*/
        builder.show()
    }
}