package sugtao4423.calcsalary

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLHelper(context: Context) : SQLiteOpenHelper(context, "database", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE salaries(wage INTEGER, minutes INTEGER, changed TEXT, memo TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            onCreate(db!!)
            db.execSQL("INSERT INTO salaries SELECT jikyu, hour*60+minute, changed, memo FROM 'database'")
            db.execSQL("DROP TABLE 'database'")
        }
    }

}