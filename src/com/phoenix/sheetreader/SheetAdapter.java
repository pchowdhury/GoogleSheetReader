package com.phoenix.sheetreader;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SheetAdapter extends BaseAdapter {
	ArrayList<ArrayList<String>> mItems = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> mFilteredItems = new ArrayList<ArrayList<String>>();
	private LayoutInflater mInflater;
	private int mNumberOfTitles;

	public SheetAdapter(Context context, ArrayList<ArrayList<String>> list,
			int noOftitles) {
		mInflater = LayoutInflater.from(context);
		this.mItems = list;
		this.mNumberOfTitles = noOftitles;
	}

	@Override
	public int getCount() {
		return mFilteredItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mFilteredItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sheet_row, null);
			holder = new ViewHolder();
			holder.mTxtRowNo = (TextView) convertView
					.findViewById(R.id.txtRowNo);
			holder.mTxtLabelNo = (TextView) convertView
					.findViewById(R.id.txtLabelNo);
			holder.mTxtContent = (TextView) convertView
					.findViewById(R.id.txtContent);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ArrayList<String> list = (ArrayList<String>) getItem(position);

		holder.mTxtRowNo.setText((position + 1) + ".");
		String content = "\n";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				holder.mTxtLabelNo.setText(list.get(i));
				continue;
			}
			content += list.get(i);
			if (i != list.size() - 1) {
				content += "\n";
			}
		}
		holder.mTxtContent.setText(content);
		convertView.setTag(holder);
		return convertView;
	}

	public String applyFilter(String filter) {
		int titlesCount = 0;
		int filterCount = 0;
		if (filter == null || filter.trim().equals("")) {
			mFilteredItems = mItems;
			titlesCount = mNumberOfTitles;
		} else {
			mFilteredItems = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < mItems.size(); i++) {
				boolean alreadyAdded = false;
				ArrayList<String> list = mItems.get(i);
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).toLowerCase()
							.contains(filter.toLowerCase())) {
						if (!alreadyAdded) {
							alreadyAdded = true;
							mFilteredItems.add(list);
							titlesCount += (list.size() - 1);
						}
						filterCount++;
					}
				}
			}
		}
		notifyDataSetChanged();
		return mFilteredItems.size() + " Records, " + titlesCount
				+ " Titles and " + filterCount + " matches";
	}

	class ViewHolder {
		TextView mTxtRowNo;
		TextView mTxtLabelNo;
		TextView mTxtContent;
	}

}
