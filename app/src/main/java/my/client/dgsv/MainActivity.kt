package my.client.dgsv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.client.dgsv.R
import com.google.android.material.snackbar.Snackbar
import my.client.dgsv.Module.Subjects
import my.client.dgsv.services.CheckoutService
import my.client.dgsv.services.ServiceBuilder
import my.client.dgsv.services.UsersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var selectionsub: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // access the spinner
        //alertled.visibility = View.INVISIBLE
        val spinningdata = resources.getStringArray(R.array.subjects)
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, spinningdata
            )
            spinner.adapter = adapter
        }
        var sub = spinner.selectedItem.toString()
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

             when(position){
                 1 -> selectionsub = "Sanskrit"
                 2 -> selectionsub = "Marathi"
                 3 -> selectionsub = "History"
             }


            }
        }





        val checkoutService = ServiceBuilder.buildService(UsersService::class.java)
        val filter = HashMap<String, String>()
        val requestCall = checkoutService.getUsersList(filter)

        requestCall.enqueue(object: Callback<List<Subjects>> {

            // If you receive a HTTP Response, then this method is executed
            // Your STATUS Code will decide if your Http Response is a Success or Error
            override fun onResponse(call: Call<List<Subjects>>, response: Response<List<Subjects>>) {
                if (response.isSuccessful) {
                    var index = 0
                    // Your status code is in the range of 200's
                    val destinationList = response.body()!!

                    val tosub = destinationList[index].Subject
                    val examtime = destinationList[index].examtime
                    val examstatus = destinationList[index].examstatus
                    Log.d("Subject",tosub)
                    Log.d("exam Status",examstatus.toString())

                    val startexam = findViewById<Button>(R.id.btnstart)

                    startexam.setOnClickListener { view ->
                       if(selectionsub == tosub && examstatus == true){
                           val intent = Intent(this@MainActivity, ExamPortal::class.java)
                           startActivity(intent)
                           finish()
                       }
                        else{
                           Snackbar.make(view, "Please select proper details", Snackbar.LENGTH_LONG)
                               .setAction("Action", null).show()
                       }
                    }


                    // destiny_recycler_view.adapter = DestinationAdapter(destinationList)
                } else if(response.code() == 401) {
                    Log.d("fail","Application Level failure")
                    // Toast.makeText(this@HomeFragment,
                    //  "Your session has expired. Please Login again.", Toast.LENGTH_LONG).show()
                } else { // Application-level failure
                    // Your status code is in the range of 300's, 400's and 500's
                    // Toast.makeText(this@HomeFragment, "Failed to retrieve items", Toast.LENGTH_LONG).show()

                }
            }

            // Invoked in case of Network Error or Establishing connection with Server
            // or Error Creating Http Request or Error Processing Http Response
            override fun onFailure(call: Call<List<Subjects>>, t: Throwable) {


            }
        })





    }
}