package akshay.client.exam

import akshay.client.exam.Module.Userinfo
import akshay.client.exam.services.ServiceBuilder
import akshay.client.exam.services.UsersService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_userprofile.*
import java.io.IOException
import java.util.*


class UserprofileActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var imageButton: Button
    private lateinit var sendButton: Button
    private var imageData: ByteArray? = null
    private val postURL: String = "https://ptsv2.com/t/54odo-1576291398/post" // remember to use your own api

    companion object {
        private const val IMAGE_PICK_CODE = 999
    }
    private val sharedPreferencesFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_userprofile)
        if (!isNetworkAvailable()){
            startActivity(Intent(this@UserprofileActivity,NointernetActivity::class.java))
            finish()
        }
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPreferencesFile,
            Context.MODE_PRIVATE)
        val sharedIdVal = sharedPreferences.getInt("uuid",0)
        val rollnumber = sharedPreferences.getString("usr_rollno","defaultname")
        val firstname = sharedPreferences.getString("first_name","defaultname")
        val lastname = sharedPreferences.getString("last_name","defaultname")
        val studentphone = sharedPreferences.getString("usr_phone","defaultname")
        val organization = sharedPreferences.getString("usr_org","defaultname")
        val imageurl = sharedPreferences.getString("photo_url","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR48vsJaqxn01wR2eQo-p_3Z2VMgXWo5dpGrA&usqp=CAU")

        if (sharedIdVal.equals(0) && studentphone.equals("defaultname")){

        }else{
            Usrname.text = "$firstname $lastname"
            Usrphone.text = studentphone
            usrorg.text = organization
            usrsubject.text = "Roll No : $rollnumber"

            val roundedimag =
                findViewById(R.id.roundedimage) as ImageView


            // Load an image using Glide library
            Glide.with(applicationContext)
                .load(imageurl)
                .into(roundedimag)
        }

        val roundedimag =
            findViewById<ImageView>(R.id.roundedimage)

        roundedimag.setOnClickListener {

        }
        sendButton = findViewById(R.id.modifyimg)
        sendButton.setOnClickListener {
           // uploadImage()
        }

        editbtn.setOnClickListener {
            Log.d("click","change image button clicked")
           // launchGallery()
        }



        btnlogout.setOnClickListener {


            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialogTitle)
            builder.setMessage(R.string.dialogMessage)
            builder.setIcon(R.drawable.ic_baseline_warning_24)

            builder.setPositiveButton("Yes"){dialogInterface, which ->
                proceed(studentphone)
            }
            builder.setNegativeButton("No"){dialogInterface, which ->
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

    }

    private fun proceed(studentphone: String?) {
        if (btnlogout.text.equals("Logout")){
            logoutRemote(studentphone)
        }else{
            //login session call
            if (btnlogout.text.equals("Login")){
                //login process
                startActivity(Intent(this@UserprofileActivity,PhoneActivity::class.java))
                finish()
            }
        }
    }

    private fun logoutRemote(studentphone: String?) {
        if (!isNetworkAvailable()){
            startActivity(Intent(this@UserprofileActivity,NointernetActivity::class.java))
            finish()
        }else{
            //todo call remote service
            val loginstatus = false
            val updateService = ServiceBuilder.buildService(UsersService::class.java)
            val requestCall = updateService.updateLoginstatus(studentphone!!,loginstatus)
            requestCall.enqueue(object: Callback<Userinfo> {

                override fun onResponse(call: Call<Userinfo>, response: Response<Userinfo>) {
                    Log.d("remote","response comming")
                    if (response.isSuccessful) {
                        Log.d("remote","Success")
                            onRemoteLogout(studentphone)
                    } else {
                        val errorcode = response.code().toString()
                        dialogTryagian(errorcode, studentphone)
                    }
                }

                override fun onFailure(call: Call<Userinfo>, t: Throwable) {
                    if (isNetworkAvailable()){
                        Log.d("remote","not responnded")
                        val erroecode = "internal Error: 8"
                       // dialogTryagian(erroecode, studentphone)
                    }else{
                        startActivity(Intent(this@UserprofileActivity,NointernetActivity::class.java))
                        finish()
                    }
                }
            })
        }
    }

    private fun onRemoteLogout(studentphone: String) {
        /* Logout System */
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val sharedphoneValue = sharedPreferences.getString("student_phone","defaultname")

        editor.clear()
        editor.apply()
        editor.commit()
        // changes in appeareance after logged out and out from activity
        //start changing appeareance
        // freload phone Activity
        btnlogout.text = "Login"
        Usrname.visibility = View.INVISIBLE
        Usrphone.visibility = View.INVISIBLE
        usrorg.visibility = View.INVISIBLE
        usrsubject.visibility = View.INVISIBLE
        roundedimage.visibility = View.INVISIBLE
        /////////////////////////////////
        startActivity(Intent(this@UserprofileActivity,PhoneActivity::class.java))
        finish()
        /////////////////////////////////

    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun dialogTryagian(errorcodes: String, studentphone: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialogbox_roof, null)
        val errorsetter  = dialogLayout.findViewById<TextView>(R.id.errorsetter)
        val btnrefresh = dialogLayout.findViewById<Button>(R.id.btn_refresh)
        errorsetter.text = "Connection Error :$errorcodes"
        btnrefresh.setOnClickListener {
            //val i = Intent(this@UserprofileActivity,Splashnew::class.java)
            //startActivity(i)
            //finish()
            proceed(studentphone)
        }
        builder.setView(dialogLayout)
        /* builder.setPositiveButton("OK") { dialogInterface, i ->
             val examduration = editText.text.toString()
         }*/
        builder.show()
    }

    private fun launchGallery() {
        if (!isNetworkAvailable()){
            startActivity(Intent(this@UserprofileActivity,NointernetActivity::class.java))
            finish()
        }
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

   /* private fun uploadImage() {
        imageData?: return
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            postURL,
            Response.Listener {
                println("response is: $it")
            },
            Response.ErrorListener {
                println("error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["imageFile"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }*/

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            if (uri != null) {
                imageView.setImageURI(uri)
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

