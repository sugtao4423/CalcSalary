package sugtao4423.calcsalary

import android.content.Context

class DBUtils(context: Context) {

    private val db = SQLHelper(context).writableDatabase

    fun getWorkItems(): ArrayList<WorkItem> {
        val result = arrayListOf<WorkItem>()
        db.rawQuery("SELECT ROWID, * FROM salaries", null).apply {
            while (moveToNext()) {
                val rowId = getInt(0)
                val wage = getInt(1)
                val minutes = getInt(2)
                val changed = getString(3)
                val memo = getString(4)

                val item = WorkItem(rowId, wage, minutes, changed, memo)
                result.add(item)
            }
            close()
        }
        return result
    }

    fun createWorkItem(wage: Int, minutes: Int, changed: String, memo: String) {
        val sql = "INSERT INTO salaries VALUES (?, ?, ?, ?)"
        val bindArgs = arrayOf(
                wage.toString(),
                minutes.toString(),
                changed,
                memo
        )
        db.compileStatement(sql).apply {
            bindAllArgsAsStrings(bindArgs)
            execute()
            close()
        }
    }

    fun updateWorkTime(targetRowId: Int, wage: Int, minutes: Int, changed: String, memo: String) {
        val sql = "UPDATE salaries SET wage = ?, minutes = ?, changed = ?, memo = ? WHERE ROWID = ?"
        val bindArgs = arrayOf(
                wage.toString(),
                minutes.toString(),
                changed,
                memo,
                targetRowId.toString()
        )
        db.compileStatement(sql).apply {
            bindAllArgsAsStrings(bindArgs)
            execute()
            close()
        }
    }

    fun deleteWorkItem(targetRowId: Int) {
        val sql = "DELETE FROM salaries WHERE ROWID = $targetRowId"
        db.execSQL(sql)
    }

    fun close() {
        db.close()
    }

}