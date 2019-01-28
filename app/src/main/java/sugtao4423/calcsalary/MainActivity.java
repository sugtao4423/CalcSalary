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

    private DBUtils dbUtils;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ListView list = new ListView(this);
        list.setOnItemClickListener(this);
        adapter = new CustomAdapter(this);
        list.setAdapter(adapter);
        setContentView(list);

        dbUtils = new DBUtils(getApplicationContext());

        load();
    }

    public void load(){
        adapter.clear();

        ArrayList<WorkItem> items = dbUtils.getWorkItems();
        if(items.size() > 0){
            adapter.addAll(items);
        }else{
            add();
        }
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
                    View addView = getLayoutInflater().inflate(R.layout.addtime, null);
                    final EditText addHour = (EditText)addView.findViewById(R.id.add_hour);
                    final EditText addMinute = (EditText)addView.findViewById(R.id.add_minute);
                    addHour.setWidth((int)addHour.getTextSize() * 5);
                    addMinute.setWidth((int)addMinute.getTextSize() * 5);
                    new AlertDialog.Builder(MainActivity.this)
                            .setView(addView)
                            .setNegativeButton("キャンセル", null)
                            .setPositiveButton("OK", new OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    int hour = editText2Int(addHour);
                                    int minute = editText2Int(addMinute);
                                    hour += item.getHour();
                                    minute += item.getMinute();
                                    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(new Date());
                                    dbUtils.updateWorkTime(item.getRowid(), hour, minute, date, item.getJikyu(), item.getMemo());
                                    load();
                                }
                            })
                            .show();
                }
                if(which == 1){
                    View v = getLayoutInflater().inflate(R.layout.create, null);
                    final EditText createHour = (EditText)v.findViewById(R.id.add_hour);
                    final EditText createMinute = (EditText)v.findViewById(R.id.add_minute);
                    final EditText createJikyu = (EditText)v.findViewById(R.id.create_jikyu);
                    final EditText createMemo = (EditText)v.findViewById(R.id.create_memo);
                    final EditText createChange = (EditText)v.findViewById(R.id.create_change);

                    int width = (int)createHour.getTextSize() * 5;
                    createHour.setWidth(width);
                    createMinute.setWidth(width);
                    createJikyu.setWidth(width);

                    createHour.setText(String.valueOf(item.getHour()));
                    createMinute.setText(String.valueOf(item.getMinute()));
                    createJikyu.setText(String.valueOf(item.getJikyu()));
                    createMemo.setText(item.getMemo());
                    createChange.setText(item.getChanged());

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("変更")
                            .setView(v)
                            .setNegativeButton("キャンセル", null)
                            .setPositiveButton("OK", new OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    int hour = editText2Int(createHour);
                                    int minute = editText2Int(createMinute);
                                    int jikyu = editText2Int(createJikyu);
                                    dbUtils.updateWorkTime(item.getRowid(), hour, minute, createChange.getText().toString(), jikyu, createMemo.getText().toString());
                                    load();
                                }
                            })
                            .show();
                }
                if(which == 2){
                    ListView delItemListView = new ListView(MainActivity.this);
                    CustomAdapter delItemAdapter = new CustomAdapter(MainActivity.this);
                    delItemListView.setAdapter(delItemAdapter);
                    delItemAdapter.add(item);
                    new AlertDialog.Builder(MainActivity.this)
                            .setCustomTitle(delItemListView)
                            .setMessage("本当にこの項目を削除しますか？")
                            .setNegativeButton("キャンセル", null)
                            .setPositiveButton("OK", new OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dbUtils.deleteWorkItem(item.getRowid());
                                    load();
                                }
                            })
                            .show();
                }
            }
        });
        builder.create().show();
    }

    @SuppressLint("InflateParams")
    public void add(){
        View v = getLayoutInflater().inflate(R.layout.create, null);
        final EditText createHour = (EditText)v.findViewById(R.id.add_hour);
        final EditText createMinute = (EditText)v.findViewById(R.id.add_minute);
        final EditText createJikyu = (EditText)v.findViewById(R.id.create_jikyu);
        final EditText createMemo = (EditText)v.findViewById(R.id.create_memo);

        v.findViewById(R.id.text_change).setVisibility(View.GONE);
        v.findViewById(R.id.create_change).setVisibility(View.GONE);

        int width = (int)createHour.getTextSize() * 5;
        createHour.setWidth(width);
        createMinute.setWidth(width);
        createJikyu.setWidth(width);

        new AlertDialog.Builder(this)
                .setTitle("項目を追加")
                .setView(v)
                .setNegativeButton("キャンセル", null)
                .setPositiveButton("OK", new OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int hour = editText2Int(createHour);
                        int minute = editText2Int(createMinute);
                        int jikyu = editText2Int(createJikyu);
                        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(new Date());
                        dbUtils.createWorkItem(hour, minute, date, jikyu, createMemo.getText().toString());
                        load();
                    }
                })
                .show();
    }

    public int editText2Int(EditText editText){
        String text = editText.getText().toString();
        if(text.isEmpty()){
            return 0;
        }else{
            return Integer.parseInt(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, Menu.FIRST, Menu.NONE, "追加")
                .setIcon(R.drawable.ic_add_circle_outline_black_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == Menu.FIRST){
            add();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        dbUtils.close();
    }

}
