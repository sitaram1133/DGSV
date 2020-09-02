package akshay.client.exam.LocalDatabase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import akshay.client.exam.LocalQuetions


//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "QtnPaperDatabase"
        private val TABLE_CONTACTS = "McqTable"
        private val KEY_ID = "id"
        private val KEY_QTN = "Question"
        private val KEY_OPT1 = "Option1"
        private val KEY_OPT2 = "Option2"
        private val KEY_OPT3 = "Option3"
        private val KEY_OPT4 = "Option4"
        private val KEY_ANS = "Answer"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QTN + " TEXT,"
                + KEY_OPT1 + " TEXT," + KEY_OPT2 + " TEXT," + KEY_OPT3 + " TEXT," + KEY_OPT4 + " TEXT," + KEY_ANS + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addQuetions(emp: LocalQuetions):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_QTN, emp.Question) // Quation
        contentValues.put(KEY_OPT1,emp.Option1 ) // Option1
        contentValues.put(KEY_OPT2,emp.Option2 )
        contentValues.put(KEY_OPT3,emp.Option3 )
        contentValues.put(KEY_OPT4,emp.Option4 )
        contentValues.put(KEY_ANS,emp.Answer ) //Anwser
        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewQtnpaper():List<LocalQuetions>{
        val empList:ArrayList<LocalQuetions> = ArrayList<LocalQuetions>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Int
        var qtn: String
        var opt1: String
        var opt2: String
        var opt3: String
        var opt4: String
        var ans: Int
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getInt(cursor.getColumnIndex("id"))
                qtn = cursor.getString(cursor.getColumnIndex("Question"))
                opt1 = cursor.getString(cursor.getColumnIndex("Option1"))
                opt2 = cursor.getString(cursor.getColumnIndex("Option2"))
                opt3 = cursor.getString(cursor.getColumnIndex("Option3"))
                opt4 = cursor.getString(cursor.getColumnIndex("Option4"))
                ans = cursor.getInt(cursor.getColumnIndex("Answer"))


                val emp= LocalQuetions(userId = userId, Question = qtn, Option1 = opt1,  Option2 = opt2,  Option3 = opt3,
                    Option4 = opt4,  Answer = ans)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

}