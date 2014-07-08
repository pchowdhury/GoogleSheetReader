/*
 * Copyright (C) 2011 Pushpan, 
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
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.phoenix.sheetreader.auth.AndroidAuthenticator;
import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.WorkSheetCell;
import com.pras.WorkSheetRow;

/**
 * @author Pushpan
 * 
 */
public class SheetDetails extends Activity {

	// int wkID;
	// int spID;
	ArrayList<WorkSheetRow> rows;
	String[] cols;
	ListView mListView;
	TextView mEmptyView;
	EditText mSearchView;
	ArrayList<ArrayList<String>> mItems = new ArrayList<ArrayList<String>>();
	private TextView mTxtCountView;
	SheetAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_details);
		mListView = (ListView) findViewById(R.id.listView);
		mEmptyView = (TextView) findViewById(R.id.emptyView);
		mSearchView = (EditText) findViewById(R.id.searchView);
		mTxtCountView = (TextView) findViewById(R.id.txtCountView);
		// Intent intent = getIntent();
		// wkID = intent.getIntExtra("wk_id", -1);
		// spID = intent.getIntExtra("sp_id", -1);
		//
		// if (wkID == -1 || spID == -1) {
		// finish();
		// return;
		// }
		mSearchView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				searchinList(s.toString());

			}
		});
		new SheetLoaderTask().execute();
	}

	protected void searchinList(String filter) {
		if (mAdapter != null) {
			mTxtCountView.setText(mAdapter.applyFilter(filter));
		}
	}

	private class SheetLoaderTask extends AsyncTask<Object, Object, Object> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(SheetDetails.this, "Please wait",
					"Connecting...");
			dialog.show();
		}

		@Override
		protected Object doInBackground(Object... params) {
			// Read Spread Sheet list from the server.
			SpreadSheetFactory factory = SpreadSheetFactory
					.getInstance(new AndroidAuthenticator(SheetDetails.this));
			dialog.setMessage("Featching records...");
			// Read from local Cache
			ArrayList<SpreadSheet> sps = factory.getAllSpreadSheets(true,
					"dvd list", false);
			// SpreadSheet sp = sps.get(spID);
			SpreadSheet sp = sps.get(0);
			WorkSheet wk = sp.getAllWorkSheets(true).get(0);
			cols = wk.getColumns();
			rows = wk.getData(false);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (dialog != null && dialog.isShowing())
				dialog.cancel();

			if (rows == null || rows.size() == 0) {
				mEmptyView.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
				mTxtCountView.setText("0 Records.");
				return;
			} else {
				mEmptyView.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
			}

			// StringBuffer record = new StringBuffer();

			mItems = new ArrayList<ArrayList<String>>();

			// if (cols != null) {
			// record.append("Columns: [" + cols + "]\n");
			// }
			// record.append("Number of Records: " + rows.size() + "\n");

			int titlesCount = 0;
			for (int i = 0; i < rows.size(); i++) {
				WorkSheetRow row = rows.get(i);
				// record.append("[ Row ID " + (i + 1) + " ]\n");
				ArrayList<WorkSheetCell> cells = row.getCells();
				ArrayList<String> list = new ArrayList<String>();
				mItems.add(list);
				for (int j = 0; j < cells.size(); j++) {
					WorkSheetCell cell = cells.get(j);
					String value = cell.getValue();
					if(value.contains("a_n_d")){
						value=value.replace("a_n_d", "&");
					}
					if (j == 0) {
						list.add((i + 1) + "\n" + value);
						continue;
					}
					list.add(value);
					titlesCount++;
					// record.append(cell.getName() + " = " + cell.getValue()
					// + "\n");
				}
			}
			mAdapter = new SheetAdapter(SheetDetails.this, mItems, titlesCount);
			mListView.setAdapter(mAdapter);
			mTxtCountView.setText(mAdapter.applyFilter(""));
			cols = null;
			rows = null;
		}

	}
}
