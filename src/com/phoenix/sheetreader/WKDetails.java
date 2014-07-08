/*
 * Copyright (C) 2011 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phoenix.sheetreader;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.WorkSheetCell;
import com.pras.WorkSheetRow;

/**
 * @author Prasanta Paul
 * 
 */
public class WKDetails extends Activity {

	int wkID;
	int spID;
	ArrayList<WorkSheetRow> rows;
	String[] cols;
	TextView tv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		wkID = intent.getIntExtra("wk_id", -1);
		spID = intent.getIntExtra("sp_id", -1);

		if (wkID == -1 || spID == -1) {
			finish();
			return;
		}

		tv = new TextView(this.getApplicationContext());

		new MyTask().execute();
	}

	private class MyTask extends AsyncTask {

		Dialog dialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new Dialog(WKDetails.this);
			dialog.setTitle("Please wait");
			TextView tv = new TextView(WKDetails.this.getApplicationContext());
			tv.setText("Featching records...");
			dialog.setContentView(tv);
			dialog.show();
		}

		@Override
		protected Object doInBackground(Object... params) {
			// Read Spread Sheet list from the server.
			SpreadSheetFactory factory = SpreadSheetFactory.getInstance();
			// Read from local Cache
			ArrayList<SpreadSheet> sps = factory.getAllSpreadSheets(false);
			SpreadSheet sp = sps.get(spID);
			WorkSheet wk = sp.getAllWorkSheets(false).get(wkID);
			cols = wk.getColumns();
			rows = wk.getData(false);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog.isShowing())
				dialog.cancel();

			if (rows == null || rows.size() == 0) {
				tv.setText("No record exists....");
				setContentView(tv);
				return;
			}

			StringBuffer record = new StringBuffer();

			Log.i("Prasanta", "Columns: " + cols);

			if (cols != null) {
				record.append("Columns: [" + cols + "]\n");
			}
			record.append("Number of Records: " + rows.size() + "\n");

			for (int i = 0; i < rows.size(); i++) {
				WorkSheetRow row = rows.get(i);
				record.append("[ Row ID " + (i + 1) + " ]\n");

				ArrayList<WorkSheetCell> cells = row.getCells();

				for (int j = 0; j < cells.size(); j++) {
					WorkSheetCell cell = cells.get(j);
					record.append(cell.getName() + " = " + cell.getValue()
							+ "\n");
				}
			}

			tv.setText(record);
			setContentView(tv);
		}
	}
}
