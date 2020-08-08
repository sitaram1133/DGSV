package my.client.dgsv

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.client.dgsv.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_register.*
import my.client.dgsv.Module.Userinfo
import my.client.dgsv.services.ServiceBuilder
import my.client.dgsv.services.UsersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val spinner: Spinner = findViewById(R.id.planets_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.subjects,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }




        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val sharedIdVal = sharedPreferences.getInt("uuid",0)
        val sharedphoneValue = sharedPreferences.getString("student_phone","defaultname")
        Log.d("Phone", sharedphoneValue)

        btnsubmit.setOnClickListener {view ->
            val uuid = sharedIdVal
            val firstname = firstname.text.toString()
            val middlename = middlename.text.toString()
            val sirname = lastname.text.toString()
            val stdphone = sharedphoneValue
            val address = address.text.toString()
            val rollnumber = rollno.text.toString()
            val standerd = classid.text.toString()
            if (firstname.isEmpty() && middlename.isEmpty() && sirname.isEmpty() && address.isEmpty() && rollnumber.isEmpty() && standerd.isEmpty())
            {
                Snackbar.make(view, "Please Enter all fields", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                registerUser(firstname, middlename, sirname, uuid.toString(), stdphone.toString(), address, rollnumber, standerd)
            }
        }
    }


    private fun registerUser(
        firstname: String,
        middlename: String,
        sirname: String,
        uuid: String,
        organization: String,
        stdphone: String,
        rollnumber: String,
        standerd: String
    ) {
        val newUser = Userinfo()
        newUser.firstname = firstname
        newUser.middlename = middlename
        newUser.lastname = sirname
        newUser.fullname = "$firstname $middlename $sirname"
        newUser.organization = organization
        newUser.stdphone = stdphone
        newUser.uuid = uuid
        newUser.organization = organization
        newUser.standerd = standerd
        newUser.rollnumber = rollnumber




        val checkoutService = ServiceBuilder.buildService(UsersService::class.java)
        val requestCall = checkoutService.addUser(newUser)

        requestCall.enqueue(object : Callback<Userinfo> {

            override fun onResponse(call: Call<Userinfo>, response: Response<Userinfo>) {
                if (response.isSuccessful) {

                    onRemoteUserRegistered(firstname, middlename, sirname, organization, stdphone, uuid, rollnumber, standerd)

                    var newlyCreatedDestination = response.body() // Use it or ignore it
                    // Toast.makeText(context, "Successfully Added", Toast.LENGTH_SHORT).show()


                } else {
                    // Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Userinfo>, t: Throwable) {
                // Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onRemoteUserRegistered(
        firstname: String,
        middlename: String,
        sirname: String,
        organization: String,
        stdphone: String,
        uuid: String,
        rollnumber: String,
        standerd: String
    ) {
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("uuid_key",uuid.toInt())
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
        if (!uuid.isEmpty() && !stdphone.isEmpty()){
            val intent = Intent(this@RegisterActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Log.d("Login","Not any credential")
        }
    }


}