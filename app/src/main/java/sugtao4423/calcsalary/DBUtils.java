package sugtao4423.calcsalary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DBUtils{

    private SQLiteDatabase db;

    public DBUtils(Context context){
        db = new SQLHelper(context).getWritableDatabase();
    }

    public ArrayList<WorkItem> getWorkItems(){
        ArrayList<WorkItem> result = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ROWID, * FROM database", null);
        while(c.moveToNext()){
            int rowid = c.getInt(0);
            int hour = c.getInt(1);
            int minute = c.getInt(2);
            String changed = c.getString(3);
            int jikyu = c.getInt(4);
            String memo = c.getString(5);

            WorkItem item = new WorkItem(rowid, jikyu, hour, minute, changed, memo);
            result.add(item);
        }
        c.close();
        return result;
    }

    public void createWorkItem(int hour, int minute, String changed, int jikyu, String memo){
        String sql = "INSERT INTO database VALUES (?, ?, ?, ?, ?)";
        String[] bindArgs = new String[]{
                String.valueOf(hour),
                String.valueOf(minute),
                changed,
                String.valueOf(jikyu),
                memo
        };
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindAllArgsAsStrings(bindArgs);
        stmt.execute();
        stmt.close();
    }

    public void updateWorkTime(int targetRowid, int hour, int minute, String changed, int jikyu, String memo){
        String sql = "UPDATE database SET hour = ?, minute = ?, changed = ?, jikyu = ?, memo = ? WHERE ROWID = ?";
        String[] bindArgs = new String[]{
                String.valueOf(hour),
                String.valueOf(minute),
                changed,
                String.valueOf(jikyu),
                memo,
                String.valueOf(targetRowid)
        };
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindAllArgsAsStrings(bindArgs);
        stmt.execute();
        stmt.close();
    }

    public void deleteWorkItem(int targetRowid){
        String sql = "DELETE FROM database WHERE ROWID = " + targetRowid;
        db.execSQL(sql);
    }

    public void close(){
        db.close();
    }

}
