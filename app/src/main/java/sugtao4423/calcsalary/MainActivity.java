package sugtao4423.calcsalary;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnItemClickListener{

    private SQLiteDatabase db;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ListView list = new ListView(this);
        list.setOnItemClickListener(this);
        adapter = new CustomAdapter(this);
        list.setAdapter(adapter);
        setContentView(list);

        db = new SQLHelper(this).getWritableDatabase();

        load();
    }

    public void load(){
        adapter.clear();

        ArrayList<Integer> rowid = new ArrayList<Integer>();
        ArrayList<Integer> hour = new ArrayList<Integer>();
        ArrayList<Integer> minute = new ArrayList<Integer>();
        ArrayList<String> changed = new ArrayList<String>();
        ArrayList<Integer> jikyu = new ArrayList<Integer>();
        ArrayList<String> memo = new ArrayList<String>();

        Cursor c = db.rawQuery("select ROWID, * from database", null);
        boolean mov = c.moveToFirst();
        if(!mov){
            add();
            return;
        }
        while(mov){
            rowid.add(c.getInt(0));
            hour.add(c.getInt(1));
            minute.add(c.getInt(2));
            changed.add(c.getString(3));
            jikyu.add(c.getInt(4));
            memo.add(c.getString(5));

            mov = c.moveToNext();
        }
        WorkItem[] item = new WorkItem[rowid.size()];

        for(int i = 0; i < rowid.size(); i++)
            item[i] = new WorkItem(rowid.get(i), jikyu.get(i), hour.get(i), minute.get(i), changed.get(i), memo.get(i));

        adapter.addAll(item);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = new String[]{"時間追加", "変更", "この項目を削除"};
        builder.setItems(items, new OnClickListener(){

            @SuppressLint("InflateParams")
            @Override
            public void onClick(DialogInterface dialog, int which){
                final WorkItem item = (WorkItem)parent.getItemAtPosition(position);
                if(which == 0){
                    AlertDialog.Builder add_dialog = new AlertDialog.Builder(MainActivity.this);
                    View add_view = getLayoutInflater().inflate(R.layout.addtime, null);
                    final EditText add_hour = (EditText)add_view.findViewById(R.id.add_hour);
                    final EditText add_minute = (EditText)add_view.findViewById(R.id.add_minute);
                    add_hour.setWidth((int)add_hour.getTextSize() * 5);
                    add_minute.setWidth((int)add_minute.getTextSize() * 5);
                    add_dialog.setView(add_view);
                    add_dialog.setNegativeButton("キャンセル", null)
                            .setPositiveButton("OK", new OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    int hour = editGetInt(add_hour);
                                    int minute = editGetInt(add_minute);
                                    hour += item.getHour();
                                    minute += item.getMinute();
                                    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(new Date());
                                    db.execSQL("update database set hour=" + hour + ", minute=" + minute + ", "
                                            + "changed='" + date + "' where ROWID=" + item.getRowid());
                                    load();
                                }
                            });
                    add_dialog.create().show();
                }
                if(which == 1){
                    View v = getLayoutInflater().inflate(R.layout.create, null);
                    final EditText create_hour = (EditText)v.findViewById(R.id.add_hour);
                    final EditText create_minute = (EditText)v.findViewById(R.id.add_minute);
                    final EditText create_jikyu = (EditText)v.findViewById(R.id.create_jikyu);
                    final EditText create_memo = (EditText)v.findViewById(R.id.create_memo);
                    final EditText create_change = (EditText)v.findViewById(R.id.create_change);

                    int width = (int)create_hour.getTextSize() * 5;
                    create_hour.setWidth(width);
                    create_minute.setWidth(width);
                    create_jikyu.setWidth(width);

                    create_hour.setText(String.valueOf(item.getHour()));
                    create_minute.setText(String.valueOf(item.getMinute()));
                    create_jikyu.setText(String.valueOf(item.getJikyu()));
                    create_memo.setText(item.getMemo());
                    create_change.setText(item.getChanged());

                    AlertDialog.Builder change = new AlertDialog.Builder(MainActivity.this);
                    change.setTitle("変更")
                            .setView(v)
                            .setNegativeButton("キャンセル", null)
                            .setPositiveButton("OK", new OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    int hour = editGetInt(create_hour);
                                    int minute = editGetInt(create_minute);
                                    int jikyu = editGetInt(create_jikyu);

                                    db.execSQL("update database set hour=" + hour + ", minute=" + minute + ", "
                                            + "changed='" + create_change.getText().toString() + "', jikyu=" + jikyu +
                                            ", memo='" + create_memo.getText().toString() + "' where ROWID=" + item.getRowid());
                                    load();
                                }
                            });
                    change.create().show();
                }
                if(which == 2){
                    ListView del = new ListView(MainActivity.this);
                    CustomAdapter del_ada = new CustomAdapter(MainActivity.this);
                    del.setAdapter(del_ada);
                    del_ada.add(item);
                    AlertDialog.Builder delete = new AlertDialog.Builder(MainActivity.this);
                    delete.setCustomTitle(del)
                            .setMessage("本当にこの項目を削除しますか？")
                            .setNegativeButton("キャンセル", null)
                            .setPositiveButton("OK", new OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    db.execSQL("delete from database where ROWID=" + item.getRowid());
                                    load();
                                }
                            });
                    delete.create().show();
                }
            }
        });
        builder.create().show();
    }

    @SuppressLint("InflateParams")
    public void add(){
        View v = getLayoutInflater().inflate(R.layout.create, null);
        final EditText create_hour = (EditText)v.findViewById(R.id.add_hour);
        final EditText create_minute = (EditText)v.findViewById(R.id.add_minute);
        final EditText create_jikyu = (EditText)v.findViewById(R.id.create_jikyu);
        final EditText create_memo = (EditText)v.findViewById(R.id.create_memo);

        v.findViewById(R.id.text_change).setVisibility(View.GONE);
        v.findViewById(R.id.create_change).setVisibility(View.GONE);

        int width = (int)create_hour.getTextSize() * 5;
        create_hour.setWidth(width);
        create_minute.setWidth(width);
        create_jikyu.setWidth(width);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("項目を追加")
                .setView(v)
                .setNegativeButton("キャンセル", null)
                .setPositiveButton("OK", new OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int hour = editGetInt(create_hour);
                        int minute = editGetInt(create_minute);
                        int jikyu = editGetInt(create_jikyu);
                        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(new Date());
                        db.execSQL("insert into database values(" + hour + ", " + minute + ", '" + date + "', " + jikyu + ", '"
                                + create_memo.getText().toString() + "')");
                        load();
                    }
                });
        builder.create().show();
    }

    public int editGetInt(EditText edit){
        String text = edit.getText().toString();
        if(text.isEmpty())
            return 0;
        else
            return Integer.parseInt(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuItem add = menu.add(0, Menu.FIRST, Menu.NONE, "追加");
        add.setIcon(R.drawable.ic_add_circle_outline_black_24dp);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == Menu.FIRST){
            add();
        }
        return super.onOptionsItemSelected(item);
    }

}
