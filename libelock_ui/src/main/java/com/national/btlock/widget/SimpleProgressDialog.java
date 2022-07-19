package com.national.btlock.widget;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;

import com.national.btlock.ui.R;


public class SimpleProgressDialog extends ProgressDialog {

	public SimpleProgressDialog(Context context) {
		super(context, AlertDialog.THEME_HOLO_LIGHT);
		this.setIndeterminate(true);
		this.setCancelable(true);
		this.getWindow().setGravity(Gravity.CENTER);
	}

	@Override
	public void show() {
		super.show();
		this.setContentView(R.layout.simple_progress);

	}

}
