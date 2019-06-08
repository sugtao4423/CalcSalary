package sugtao4423.calcsalary

import android.content.Context

class DBUtils(context: Context) {

    private val db = SQLHelper(context).writableDatabase

    fun getWorkItems(): ArrayList<WorkItem> {
        val result = arrayListOf<WorkItem>()
        db.rawQuery("SELECT ROWID, * FROM 'database'", null).apply {
            while (moveToNext()) {
                val rowId = getInt(0)
                val hour = getInt(1)
                val minute = getInt(2)
                val changed = getString(3)
                val jikyu = getInt(4)
                val memo = getString(5)

                val item = WorkItem(rowId, jikyu, hour, minute, changed, memo)
                result.add(item)
            }
            close()
        }
        return result
    }

    fun createWorkItem(hour: Int, minute: Int, changed: String, jikyu: Int, memo: String) {
        val sql = "INSERT INTO 'database' VALUES (?, ?, ?, ?, ?)"
        val bindArgs = arrayOf(
                hour.toString(),
                minute.toString(),
                changed,
                jikyu.toString(),
                memo
        )
        db.compileStatement(sql).apply {
            bindAllArgsAsStrings(bindArgs)
            execute()
            close()
        }
    }

    fun updateWorkTime(targetRowid: Int, hour: Int, minute: Int, changed: String, jikyu: Int, memo: String) {
        val sql = "UPDATE 'database' SET hour = ?, minute = ?, changed = ?, jikyu = ?, memo = ? WHERE ROWID = ?"
        val bindArgs = arrayOf(
                hour.toString(),
                minute.toString(),
                changed,
                jikyu.toString(),
                memo,
                targetRowid.toString()
        )
        db.compileStatement(sql).apply {
            bindAllArgsAsStrings(bindArgs)
            execute()
            close()
        }
    }

    fun deleteWorkItem(targetRowid: Int) {
        val sql = "DELETE FROM 'database' WHERE ROWID = $targetRowid"
        db.execSQL(sql)
    }

    fun close() {
        db.close()
    }

}