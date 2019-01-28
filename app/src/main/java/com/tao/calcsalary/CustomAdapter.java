package com.tao.calcsalary;

import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<WorkItem>{
	private LayoutInflater mInflater;

	public CustomAdapter(Context context){
		super(context, android.R.layout.simple_list_item_1);
		mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}

	class ViewHolder{
		TextView worktime, changed, totalyen, memo;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder;
		WorkItem item = getItem(position);

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			TextView worktime = (TextView)convertView.findViewById(R.id.worktime);
			TextView changed = (TextView)convertView.findViewById(R.id.changed);
			TextView totalyen = (TextView)convertView.findViewById(R.id.totalyen);
			TextView memo = (TextView)convertView.findViewById(R.id.memo);

			holder = new ViewHolder();
			holder.worktime = worktime;
			holder.changed = changed;
			holder.totalyen = totalyen;
			holder.memo = memo;

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		double yen = (item.getHour() + (double)item.getMinute() / 60) * item.getJikyu();
		String yen_str = NumberFormat.getCurrencyInstance().format(yen);

		holder.worktime.setText(String.valueOf(item.getHour()) + "時間" + String.valueOf(item.getMinute()) + "分");
		holder.changed.setText("更新：" + item.getChanged());
		holder.totalyen.setText(yen_str);
		holder.memo.setText(item.getMemo());

		return convertView;
	}
}
