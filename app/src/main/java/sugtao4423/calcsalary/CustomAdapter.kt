package sugtao4423.calcsalary

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.NumberFormat

class CustomAdapter(context: Context) : ArrayAdapter<WorkItem>(context, android.R.layout.simple_list_item_1) {

    private val mInflater = context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private data class ViewHolder(
            val workTime: TextView,
            val changed: TextView,
            val totalYen: TextView,
            val memo: TextView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item, parent, false)
            val workTime = view.findViewById<TextView>(R.id.worktime)
            val changed = view.findViewById<TextView>(R.id.changed)
            val totalYen = view.findViewById<TextView>(R.id.totalyen)
            val memo = view.findViewById<TextView>(R.id.memo)

            holder = ViewHolder(workTime, changed, totalYen, memo)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = getItem(position) ?: return view!!

        holder.workTime.text = "${item.hour}時間${item.minute}分"
        holder.changed.text = "更新:${item.changed}"
        holder.totalYen.text = run {
            val yen = (item.hour + item.minute.toDouble() / 60) * item.jikyu
            NumberFormat.getCurrencyInstance().format(yen)
        }
        holder.memo.text = item.memo
        view!!.setBackgroundResource(
                if (position % 2 == 0) R.drawable.position0 else R.drawable.position1
        )

        return view
    }

}