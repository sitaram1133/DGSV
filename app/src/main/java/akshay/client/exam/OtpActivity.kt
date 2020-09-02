package akshay.client.exam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import akshay.client.exam.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    lateinit var context: Context
    var START_MILLI_SECONDS = 60000L
    lateinit var countdown_timer: CountDownTimer
    var isRunning: Boolean = false;
    var time_in_milli_seconds = 0L
    var timeoutlog: Boolean = false
    var validationCall:Boolean = false

    //It is the verification id that will be sent to the user
    private var mVerificationId: String? = null
    private  var phone_trans: String? = null
    //firebase auth object
    private var mAuth: FirebaseAuth? = null
    private val sharedPrefFile = "kotlinsharedpref"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val intent = intent
        val phone = intent.getStringExtra("phone")

        if (phone.isEmpty() || phone.length < 10) {
           // phone_num!!.setError("Enter a valid mobile")
          //  phone_num!!.requestFocus()
        }else{
            /*var countdown = 1
            time_in_milli_seconds = countdown.toLong() *60000L
            val counttime = findViewById<TextView>(R.id.counttime)
            startTimer(time_in_milli_seconds)
            Log.d("Phone",phone)
            sendVerificationCode(phone)*/
             val intent = Intent(this@OtpActivity, RegisterActivity::class.java)
             intent.putExtra("mobile", phone)
              startActivity(intent)
        }


        btnpushcode.setOnClickListener {
            if (validationCall){
                val intent = Intent(this@OtpActivity,RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }else
            {
          /*      val intent = Intent(this@OtpActivity,RegisterActivity::class.java)
                startActivity(intent)
                finish()*/
            }
        }

    }


    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                timeoutlog = true
               // startActivity(intent)
               // finish()
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

        counttime.text = seconds.toString()
    }

    private fun sendVerificationCode(phone: String?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91" + phone,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks);
    }
    //the callback to detect the verification status
    private val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // editTextCode = findViewById(R.id.realotp)
                //  otp_num = editTextCode!!.text.toString().trim()
                //Getting the code sent by SMS
                val code = phoneAuthCredential.smsCode
                Log.d("data","Here We Go!")
                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                    editTextCode!!.setText(code)
                    Log.d("code : ",code)
                    //verifying the code
                    verifyVerificationCode(code)
                }
            }


            private fun verifyVerificationCode(code: String) {
                //creating the credential
                val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)

                //signing the user
                signInWithPhoneAuthCredential(credential)
            }



            private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
                mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener(
                        this@OtpActivity,
                        OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                //verification successful we will start the profile activity
                                onResponsesuccess(phone_trans)
                                // loadUserRepo(username, phone_trans, address)

                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {

                                //verification unsuccessful.. display an error message
                                var message =
                                    "Somthing is wrong, we will fix it soon..."
                                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                    message = "Invalid code entered..."
                                }


                            }
                        })
            }



            override fun onVerificationFailed(e: FirebaseException) {
                //Toast.makeText(this@VerifyPhoneActivity, e.message, Toast.LENGTH_LONG).show()
                Log.d("tagline","Verification Failed")
                onResponsesuccess(phone_trans)
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)

                //storing the verification id that is sent to the user
                mVerificationId = s
            }
        }

    private fun onResponsesuccess(phoneTrans: String?): Boolean {
        val sharedPreferences : SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val random = Random()
        val id = random
        editor.putString("uuid",id.toString())
        editor.putString("student_phone",phoneTrans)
        editor.clear()
        editor.apply()
        editor.commit()
        validationCall = true
        Log.d("Validation","Success")
        return validationCall
    }




}