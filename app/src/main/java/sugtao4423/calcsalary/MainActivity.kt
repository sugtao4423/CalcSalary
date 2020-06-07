package sugtao4423.calcsalary

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    companion object {
        const val KEY_ITEM_REVERSE = "isReverse"
    }

    private lateinit var dbUtils: DBUtils
    private lateinit var adapter: CustomAdapter
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = ListView(this)
        list.onItemClickListener = this
        adapter = CustomAdapter(this)
        list.adapter = adapter
        setContentView(list)

        dbUtils = DBUtils(applicationContext)
        pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        load()
    }

    private fun load() {
        adapter.clear()

        val items = dbUtils.getWorkItems()
        if (items.isNotEmpty()) {
            if (pref.getBoolean(KEY_ITEM_REVERSE, false)) {
                items.map {
                    adapter.insert(it, 0)
                }
            } else {
                adapter.addAll(items)
            }
        } else {
            add()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent == null) {
            return
        }

        val items = arrayOf("時間追加", "変更", "この項目を削除")
        AlertDialog.Builder(this)
                .setItems(items) { _, which ->
                    val item = parent.getItemAtPosition(position) as WorkItem
                    when (which) {
                        0 -> {
                            val v = layoutInflater.inflate(R.layout.addtime, parent, false)
                            val addHour = v.findViewById<EditText>(R.id.add_hour)
                            val addMinute = v.findViewById<EditText>(R.id.add_minute)
                            val editTextWidth = addHour.textSize.toInt() * 5
                            addHour.width = editTextWidth
                            addMinute.width = editTextWidth
                            AlertDialog.Builder(this)
                                    .setView(v)
                                    .setNegativeButton("キャンセル", null)
                                    .setPositiveButton("OK") { _, _ ->
                                        val totalMinutes = item.minutes + hourMin2min(addHour, addMinute)
                                        val date = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(Date())
                                        dbUtils.updateWorkTime(item.rowId, item.wage, totalMinutes, date, item.memo)
                                        load()
                                    }
                                    .show()
                        }
                        1 -> {
                            val v = layoutInflater.inflate(R.layout.create, parent, false)
                            val createHour = v.findViewById<EditText>(R.id.add_hour)
                            val createMinute = v.findViewById<EditText>(R.id.add_minute)
                            val createWage = v.findViewById<EditText>(R.id.create_wage)
                            val createMemo = v.findViewById<EditText>(R.id.create_memo)
                            val createChange = v.findViewById<EditText>(R.id.create_change)

                            (createHour.textSize.toInt() * 5).let {
                                createHour.width = it
                                createMinute.width = it
                                createWage.width = it
                            }

                            val hourMin = min2hourMin(item.minutes)
                            createHour.setText(hourMin.first.toString())
                            createMinute.setText(hourMin.second.toString())
                            createWage.setText(item.wage.toString())
                            createMemo.setText(item.memo)
                            createChange.setText(item.changed)

                            AlertDialog.Builder(this)
                                    .setView(v)
                                    .setTitle("変更")
                                    .setNegativeButton("キャンセル", null)
                                    .setPositiveButton("OK") { _, _ ->
                                        val totalMinutes = hourMin2min(createHour, createMinute)
                                        val wage = editText2Int(createWage)
                                        dbUtils.updateWorkTime(item.rowId, wage, totalMinutes, createChange.text.toString(), createMemo.text.toString())
                                        load()
                                    }
                                    .show()
                        }
                        2 -> {
                            val delItemListView = ListView(this)
                            val delItemAdapter = CustomAdapter(this)
                            delItemListView.adapter = delItemAdapter
                            delItemAdapter.add(item)
                            AlertDialog.Builder(this)
                                    .setCustomTitle(delItemListView)
                                    .setMessage("本当にこの項目を削除しますか？")
                                    .setNegativeButton("キャンセル", null)
                                    .setPositiveButton("OK") { _, _ ->
                                        dbUtils.deleteWorkItem(item.rowId)
                                        load()
                                    }
                                    .show()
                        }
                    }
                }
                .show()
    }

    private fun add() {
        val v = layoutInflater.inflate(R.layout.create, null)
        val createHour = v.findViewById<EditText>(R.id.add_hour)
        val createMinute = v.findViewById<EditText>(R.id.add_minute)
        val createWage = v.findViewById<EditText>(R.id.create_wage)
        val createMemo = v.findViewById<EditText>(R.id.create_memo)

        v.findViewById<TextView>(R.id.text_change).visibility = View.GONE
        v.findViewById<EditText>(R.id.create_change).visibility = View.GONE

        (createHour.textSize.toInt() * 5).let {
            createHour.width = it
            createMinute.width = it
            createWage.width = it
        }

        AlertDialog.Builder(this)
                .setTitle("項目を追加")
                .setView(v)
                .setNegativeButton("キャンセル", null)
                .setPositiveButton("OK") { _, _ ->
                    val totalMinutes = hourMin2min(createHour, createMinute)
                    val wage = editText2Int(createWage)
                    val date = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(Date())
                    dbUtils.createWorkItem(wage, totalMinutes, date, createMemo.text.toString())
                    load()
                }
                .show()
    }

    private fun editText2Int(editText: EditText): Int {
        return editText.text.toString().let {
            if (it.isEmpty()) 0 else it.toInt()
        }
    }

    private fun min2hourMin(min: Int): Pair<Int, Int> {
        val hours = min / 60
        val minutes = min - hours * 60
        return Pair(hours, minutes)
    }

    private fun hourMin2min(hours: EditText, minutes: EditText): Int {
        return editText2Int(hours) * 60 + editText2Int(minutes)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, Menu.FIRST, Menu.NONE, "逆順")?.apply {
            setIcon(R.drawable.ic_format_line_spacing_black_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        menu?.add(0, Menu.FIRST + 1, Menu.NONE, "追加")?.apply {
            setIcon(R.drawable.ic_add_circle_outline_black_24dp)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            Menu.FIRST -> {
                val isReserve = pref.getBoolean(KEY_ITEM_REVERSE, false)
                pref.edit().putBoolean(KEY_ITEM_REVERSE, !isReserve).apply()
                load()
            }
            Menu.FIRST + 1 -> add()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbUtils.close()
    }

}
