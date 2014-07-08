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

import com.phoenix.sheetreader.auth.AndroidAuthenticator;
import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Show list of WorkSheets
 * 
 * @author Prasanta Paul
 *
 */
public class GSSDetails extends Activity {

	ListView list;
	int spID = -1;
	ArrayList<WorkSheet> workSheets;
	TextView tv;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		spID = intent.getIntExtra("sp_id", -1);
		
		if(spID == -1){
			finish();
			return;
		}
		
		list = new ListView(this.getApplicationContext());
		tv = new TextView(this.getApplicationContext());
		
		new MyTask().execute();
	}
	
	private class MyTask extends AsyncTask{

		Dialog dialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new Dialog(GSSDetails.this);
			dialog.setTitle("Please wait");
			TextView tv = new TextView(GSSDetails.this.getApplicationContext());
			tv.setText("Featching SpreadSheet details...");
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
			workSheets = sp.getAllWorkSheets();
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(dialog.isShowing())
				dialog.cancel();
			
			if(workSheets == null || workSheets.size() == 0){
		        tv.setText("No spreadsheet exists in your account...");
		        setContentView(tv);
		    }
		    else{
		        //tv.setText(spreadSheets.size() + "  spreadsheets exists in your account...");
		    	ArrayAdapter<String> arrayAdaper = new ArrayAdapter<String>(GSSDetails.this.getApplicationContext(), android.R.layout.simple_list_item_1);
		    	for(int i=0; i<workSheets.size(); i++){
		    		WorkSheet wk = workSheets.get(i);
		    		arrayAdaper.add(wk.getTitle());
		    	}
		    	Log.i("Prasanta", "Number of entries..."+ arrayAdaper.getCount());
		    	list.addHeaderView(tv);
		    	list.setAdapter(arrayAdaper);
		    	tv.setText("Number of WorkSheets ("+ workSheets.size() +")");
		    	
		    	list.setOnItemClickListener(new OnItemClickListener(){

					public void onItemClick(AdapterView<?> adapterView, View view,
							int position, long id) {
						// Show details of the SpreadSheet
						if(position == 0)
							return;
						
						Toast.makeText(GSSDetails.this.getApplicationContext(), "Showing WorkSheet details, please wait...", Toast.LENGTH_LONG).show();
						
						// Start SP Details Activity 
						Intent i = new Intent(GSSDetails.this, SheetDetails.class);
						i.putExtra("wk_id", position - 1);
						i.putExtra("sp_id", spID);
						startActivity(i);
					}
		    	});
		    	setContentView(list);
		    }
		}

	}
}
