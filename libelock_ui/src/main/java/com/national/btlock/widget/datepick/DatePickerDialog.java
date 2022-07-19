package com.national.btlock.widget.datepick;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.DatePicker;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.national.btlock.utils.DisplayUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DatePickerDialog extends android.app.DatePickerDialog {

//	private String mTitle;

	public DatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth, String title) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
//		mTitle = title;

		TextView tv = new TextView(context);
		LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		tv.setLayoutParams(lp);
		tv.setPadding(DisplayUtil.dip2px(context, 20),
				DisplayUtil.dip2px(context, 20), DisplayUtil.dip2px(context, 20),
				DisplayUtil.dip2px(context, 20));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tv.setText(title);
		tv.setTextColor(Color.parseColor("#33b5e5"));
		setCustomTitle(tv);

//		updateTitle(year, monthOfYear, dayOfMonth);

	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		updateTitle(year, month, day);
	}

	private void updateTitle(int year, int month, int day) {
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		// setTitle(getFormat().format(mCalendar.getTime()));
//		setTitle(mTitle);

	}

	/*
	 * the format for dialog tile,and you can override this method
	 */
	public SimpleDateFormat getFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	};
}