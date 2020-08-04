package my.client.dgsv


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.client.dgsv.R
import kotlinx.android.synthetic.main.activity_main.*
import my.client.dgsv.Module.Subjects
import my.client.dgsv.services.ServiceBuilder
import my.client.dgsv.services.UsersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var selectionsub: String = ""
    val list: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // access the spinner
        //alertled.visibility = View.INVISIBLE
       // val spinningdata = resources.getStringArray(R.array.subjects)
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
                 4 -> selectionsub = "Hindi"
                 5 -> selectionsub = "Geography"
                 6 -> selectionsub = "Economics"
                 7 -> selectionsub = "Vyavasay Shikshan"
                 8 -> selectionsub = "Science"
             }


            }
        }



        btnstart.setOnClickListener { view ->

            if (selectionsub.isEmpty()){

            }else{
                val checkoutService = ServiceBuilder.buildService(UsersService::class.java)
                val filter = HashMap<String, String>()
                val requestCall = checkoutService.getUsersList(filter)

                requestCall.enqueue(object: Callback<List<Subjects>> {

                    // If you receive a HTTP Response, then this method is executed
                    // Your STATUS Code will decide if your Http Response is a Success or Error
                    override fun onResponse(call: Call<List<Subjects>>, response: Response<List<Subjects>>) {
                        if (response.isSuccessful) {
                            var index = 0
                            var count = 0

                            // Your status code is in the range of 200's
                            val destinationList = response.body()!!
                               if (destinationList.isEmpty()){
                                   Log.d("data", "it is Empty")
                               }else{
                                   val tosub = destinationList[index].Subject

                                   /*while (count < destinationList.size){
                                       list.add(destinationList[index].Subject)
                                       count++
                                       if (count == destinationList.size){

                                       }else{
                                           index++
                                       }

                                   }*/

                                   if (tosub == selectionsub){
                                       val intent = Intent(this@MainActivity,ExamPortal::class.java)
                                       //startActivity(intent)
                                       //finish()
                                       Log.d("Status","Application Logged in Examportal")
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
                            examtimeid.text = " Error Code 500 \n please contact support service"
                            Toast.makeText(this@MainActivity,"Server Error 500",Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Invoked in case of Network Error or Establishing connection with Server
                    // or Error Creating Http Request or Error Processing Http Response
                    override fun onFailure(call: Call<List<Subjects>>, t: Throwable) {
                        examtimeid.text = "Server error 500 \n please contact support service"
                    }
                })
            }


        }

    }
}