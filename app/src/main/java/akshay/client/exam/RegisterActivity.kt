package akshay.client.exam

import akshay.client.exam.Module.Organization
import akshay.client.exam.Module.Userinfo
import akshay.client.exam.services.ServiceBuilder
import akshay.client.exam.services.UsersService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_phone.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    val list: ArrayList<String> = ArrayList()
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_register)
        regprogressbar.visibility = View.INVISIBLE
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        /*Call Organization Data from User field*/
        if (!isNetworkAvailable()){
            startActivity(Intent(this@RegisterActivity,NointernetActivity::class.java))
            finish()
        }
        regprogressbar.visibility = View.VISIBLE
        val destinationService = ServiceBuilder.buildService(UsersService::class.java)
        val filter = HashMap<String, String>()
        val requestCall = destinationService.getOrgList(filter)

        requestCall.enqueue(object: Callback<List<Organization>> {
            // If you receive a HTTP Response, then this method is executed
            // Your STATUS Code will decide if your Http Response is a Success or Error
            override fun onResponse(call: Call<List<Organization>>, response: Response<List<Organization>>) {
                if (response.isSuccessful) {
                    // Your status code is in the range of 200's
                    var index = 0
                    var count = 0
                    val orglist  = response.body()!!
                    if (orglist.isEmpty()){
                        Toast.makeText(this@RegisterActivity, "Organizations not Available", Toast.LENGTH_LONG).show()
                    }else{
                        while (count < orglist.size){
                            list.add(orglist[index].organization)
                            count++
                            if (count == orglist.size){
                                regprogressbar.visibility = View.INVISIBLE
                                spinData()

                            }else{
                                index++
                            }
                        }
                    }

                } else if(response.code() == 401) {
                    regprogressbar.visibility = View.INVISIBLE
                    Log.d("fail","Application Level failure")
                    Toast.makeText(this@RegisterActivity,
                        "Server not Available.", Toast.LENGTH_LONG).show()
                } else { // Application-level failure
                    // Your status code is in the range of 300's, 400's and 500's
                    Toast.makeText(this@RegisterActivity, "Server not Found", Toast.LENGTH_LONG).show()
                }
            }
            // Invoked in case of Network Error or Establishing connection with Server
            // or Error Creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Organization>>, t: Throwable) {
                regprogressbar.visibility = View.INVISIBLE
                /*check internet*/
                if (!isNetworkAvailable()){
                    val i = Intent(this@RegisterActivity,NointernetActivity::class.java)
                    startActivity(i)
                    finish()
                }else{
                    Toast.makeText(this@RegisterActivity, "internal error 8", Toast.LENGTH_LONG).show()
                }
            }
        })







        btnsubmit.setOnClickListener {view ->
            val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
                Context.MODE_PRIVATE)
            val sharedIdVal = sharedPreferences.getInt("uuid",0)
            //val sharedphoneValue = sharedPreferences.getString("student_phone","defaultname")
            val orgstatus = sharedPreferences.getString("organization","defaultname")

            val intent = intent
            val usrphone = intent.getStringExtra("phone")

            /*Log.d("Phone", sharedphoneValue)
            Log.d("Phone", orgstatus)*/



            val uuid = sharedIdVal
            val firstname = firstname.text.toString()
            val middlename = middlename.text.toString()
            val sirname = lastname.text.toString()
            val stdphone = usrphone
            val address = address.text.toString()
            val rollnumber = rollno.text.toString()
            val standerd = classid.text.toString()
            val organization = orgstatus


            /*check whether any feild is null or not */
            if (firstname.isEmpty() && middlename.isEmpty() && sirname.isEmpty() && address.isEmpty() && rollnumber.isEmpty() && standerd.isEmpty())
            {
                if (organization!!.isEmpty()){
                    Snackbar.make(view, "Please Enter all fields", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }else{
                    Snackbar.make(view, "Please Enter all fields", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                Snackbar.make(view, "Please Enter all fields", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                if (organization != null) {
                    registerUser(firstname, middlename, sirname, uuid, stdphone, address, rollnumber, standerd, organization)
                }
            }
        }
    }

    private fun onSelected(selected: String) {
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("organization",selected)
        editor.apply()
        editor.commit()
        Log.d("Organization Status",selected)
    }


    private fun registerUser(
        firstname: String,
        middlename: String,
        sirname: String,
        uuid: Int,
        stdphone: String?,
        address: String,
        rollnumber: String,
        standerd: String,
        interesting: String
    ) {
        val newUser = Userinfo()
        newUser.firstname = firstname
        newUser.middlename = middlename
        newUser.lastname = sirname
        newUser.fullname = "$firstname $middlename $sirname"
        newUser.organization = interesting
        newUser.stdphone = stdphone.toString()
        newUser.address = address
        newUser.uuid = uuid.toString()
        newUser.standerd = standerd
        newUser.rollnumber = rollnumber
        val organization = interesting

        regprogressbar.visibility = View.VISIBLE
        val checkoutService = ServiceBuilder.buildService(UsersService::class.java)
        val requestCall = checkoutService.addUser(newUser)

        requestCall.enqueue(object : Callback<Userinfo> {

            override fun onResponse(call: Call<Userinfo>, response: Response<Userinfo>) {
                if (response.isSuccessful) {
                    Log.d("Log","Registration Succcess!")
                    onRemoteUserRegistered(uuid, firstname, middlename, sirname, organization, stdphone!!,rollnumber,standerd)


                } else {
                    // Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Userinfo>, t: Throwable) {
                regprogressbar.visibility = View.INVISIBLE
                // Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onRemoteUserRegistered(
        uuid: Int,
        firstname: String,
        middlename: String,
        sirname: String,
        organization: String,
        stdphone: String,
        rollnumber: String,
        standerd: String
    ) {
        regprogressbar.visibility = View.INVISIBLE
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("uuid_key",uuid)
        editor.putString("first_name",firstname)
        editor.putString("middle_name",middlename)
        editor.putString("last_name",sirname)

        editor.putString("usr_phone",stdphone)
        editor.putString("usr_org",organization)
        editor.putString("usr_rollno",rollnumber)
        editor.putString("usr_standerd",standerd)
        editor.clear()
        editor.apply()
        editor.commit()
        if (uuid != null && stdphone != "defaultname"){
            val intent = Intent(this@RegisterActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Log.d("Login","Not any credential")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    private fun spinData(): Int {
        val spinningdata = list
        var current_position = 0
        val spinner = findViewById<Spinner>(R.id.spinnerid)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                R.layout.layoutspin, spinningdata)
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
                val selection = spinningdata[current_position]
                Log.d("Cureent Data",selection)
                onSelected(selection)
            }

        }
        return current_position
    }


}