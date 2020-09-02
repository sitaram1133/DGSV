package akshay.client.exam



import akshay.client.exam.Module.UserSession
import akshay.client.exam.services.ServiceBuilder
import akshay.client.exam.services.SessionService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity() : AppCompatActivity() {
    //private var progressBar: ProgressBar? = null
    private var CHANNEL_ID: String = "notification_channel"
    private val sharedPreferencesFile = "kotlinsharedpref"
    val list: ArrayList<String> = ArrayList()
    val phonumlist: ArrayList<String> = ArrayList()
    val qlimitlist: ArrayList<String> = ArrayList()
    val intervallist: ArrayList<String> = ArrayList()
    val examidcode: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setContentView(R.layout.activity_main)
        mainprogressbar.visibility = View.INVISIBLE
           // onTokenRefresh()
            createNotificationChannel()

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val pattern = longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.blackico)
            .setContentTitle("Welcome!")
            .setContentText("You will get exam alert via online notification.")
            .setDefaults(Notification.DEFAULT_SOUND)
            .setVibrate(pattern)
            .setStyle(NotificationCompat.InboxStyle())
            .setLights(Color.BLUE, 500, 500)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)



        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId = 0
            notify(notificationId, builder.build())

        }

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val sharedorgvalue = sharedPreferences.getString("usr_org","defaultname")
        //val sharedphoneval = sharedPreferences.getString("usr_phone","defaultname")
        //val shareduuid = sharedPreferences.getInt("uuid_key",0)

        //onTokenRefresh(sharedphoneval, shareduuid.toString())
        if (!isNetworkAvailable()){
            startActivity(Intent(this@MainActivity,NointernetActivity::class.java))
            finish()
        }
        mainprogressbar.visibility = View.VISIBLE
        val destinationService = ServiceBuilder.buildService(SessionService::class.java)
        val filter = HashMap<String, String>()
        val requestCall = destinationService.getSession(sharedorgvalue!!,filter)

        requestCall.enqueue(object: Callback<List<UserSession>> {

            // If you receive a HTTP Response, then this method is executed
            // Your STATUS Code will decide if your Http Response is a Success or Error
            override fun onResponse(call: Call<List<UserSession>>, response: Response<List<UserSession>>) {
                when {
                    response.isSuccessful -> {
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
                            examidcode.add(destinationList[index].examSessionid)
                            count++
                            if (count == destinationList.size){
                                Toast.makeText(this@MainActivity,
                                    "session Logged in Successfully.", Toast.LENGTH_LONG).show()
                                if (destinationList.isEmpty()){
                                    mainprogressbar.visibility = View.INVISIBLE
                                    sessioncode.text = "No Session available"
                                }else{
                                    mainprogressbar.visibility = View.INVISIBLE
                                    sessioncodena.visibility = View.INVISIBLE
                                    sessioncode.text = "${destinationList.size} Sessions live Now"
                                    spinData()
                                }
                            }else{
                                index++
                            }
                        }
                    }
                    response.code() == 401 -> {
                        mainprogressbar.visibility = View.INVISIBLE
                        Log.d("fail","Application Level failure")
                        val errorcodes = response.code().toString()
                        dialogTryagian(errorcodes)
                    }
                    else -> { // Application-level failure
                        mainprogressbar.visibility = View.INVISIBLE
                        // Your status code is in the range of 300's, 400's and 500's
                        //Toast.makeText(this@MainActivity, "Server not Found", Toast.LENGTH_LONG).show()
                        val errorcodes = response.code().toString()
                        dialogTryagian(errorcodes)
                    }
                }
            }

            // Invoked in case of Network Error or Establishing connection with Server
            // or Error Creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<UserSession>>, t: Throwable) {
                mainprogressbar.visibility = View.INVISIBLE
                //Toast.makeText(this@MainActivity, "internal error 8", Toast.LENGTH_LONG).show()
                if (!isNetworkAvailable()){

                }else{
                    val errorcodes = "internal Error: 8"
                    dialogTryagian(errorcodes)
                }
            }
        })


        btnstart.setOnClickListener {view->

            val examid = examsessionid.text.toString()
            Log.d("exam id entered",examid)
            if (examid.isEmpty()){
                Snackbar.make(view, "Please Enter Exam ID", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                if (!isNetworkAvailable()){
                    startActivity(Intent(this@MainActivity,NointernetActivity::class.java))
                    finish()
                }else{
                    mainprogressbar.visibility = View.VISIBLE
                    val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
                    val sharedExamid = sharedPreferences.getString("exams_id","")
                    if (sharedExamid!!.isNotEmpty()) {
                        if (sharedExamid == examid){
                            val i = Intent(this@MainActivity,ExamPortal::class.java)
                            mainprogressbar.visibility = View.INVISIBLE
                            startActivity(i)
                            // finish()
                            Log.d("btn_status","start")
                        }else{
                            Log.d("btn_status","stop")
                            mainprogressbar.visibility = View.INVISIBLE
                            Snackbar.make(view, "Please enter Correct exam ID", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    }
                }

            }
        }

    }

    private fun selectedData(
        interval: Int?,
        phonenum: String,
        totalqtn: Int?,
        examids: String
    ){

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPreferencesFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putInt("countdown",interval!!.toInt())
        editor.putString("phonenum",phonenum)
        editor.putString("exams_id",examids)
        editor.putInt("total",totalqtn!!)
        editor.apply()
        editor.commit()
        Log.d("Status",phonenum)
    }


    private fun spinData(): Int {
        var current_position = 0
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
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
                val examids = examidcode[current_position]
                Log.d("Cureent Data","$phone \n $qlimit \n $interval ")
                selectedData(interval, phone, qlimit, examids)
            }

        }
        return current_position
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId

        if (id == R.id.actionupportid){
            datahelpline(this)
        }
        if (id == R.id.action_share_app){
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=akshay.client.exam")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        return if (id == R.id.userprofileid) {
            Log.d("Log","profile_btn_presssed")
            val i = Intent(this@MainActivity,UserprofileActivity::class.java)
            startActivity(i)
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun datahelpline(view: MainActivity) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        builder.setView(dialogLayout)
       /* builder.setPositiveButton("OK") { dialogInterface, i ->
            val examduration = editText.text.toString()
        }*/
        builder.show()
    }


    private fun dialogTryagian(errorcodes: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialogbox_roof, null)
        val errorsetter  = dialogLayout.findViewById<TextView>(R.id.errorsetter)
        val btnrefresh = dialogLayout.findViewById<Button>(R.id.btn_refresh)
        errorsetter.text = "Connection Error :$errorcodes"
        btnrefresh.setOnClickListener {
            val i = Intent(this@MainActivity,Splashnew::class.java)
            startActivity(i)
            finish()
        }
        builder.setView(dialogLayout)
        /* builder.setPositiveButton("OK") { dialogInterface, i ->
             val examduration = editText.text.toString()
         }*/
        builder.show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

   /* private fun onTokenRefresh(sharedphoneval: String?, shareduuid: String?) {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("token", "Refreshed token: $refreshedToken")
        val newtoken = Security()
        newtoken.token = refreshedToken
        newtoken.stdphone = sharedphoneval
        newtoken.uuid = shareduuid
        Log.d("Notfication Data","phone: $sharedphoneval uuid: $shareduuid")

        //post data to api ->

        val checkoutService = ServiceBuilder.buildService(DataService::class.java)
        val requestCall = checkoutService.addToken(newtoken)

        requestCall.enqueue(object : Callback<Security> {

            override fun onResponse(call: Call<Security>, response: Response<Security>) {
                if (response.isSuccessful) {
                    Log.d("Log","Token Successfully Sent!")

                } else {
                     Toast.makeText(this@MainActivity, "Failed to send tcn", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Security>, t: Throwable) {
                 Toast.makeText(this@MainActivity, "Failed to send tcn", Toast.LENGTH_SHORT).show()
            }
        })
    }*/
}