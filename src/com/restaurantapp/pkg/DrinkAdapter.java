package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkAdapter extends BaseAdapter {
	Calendar c = Calendar.getInstance();
	private static ArrayList<ItemDetails> itemDetailsrrayList;

	private Integer[] imgid = { R.drawable.coffee, R.drawable.coffee,
			R.drawable.coffee, R.drawable.coffee, R.drawable.coffee,
			R.drawable.coffee };

	private LayoutInflater l_Inflater;

	private Context context;

	// private OnClickListener listner;

	public DrinkAdapter(Context context, ArrayList<ItemDetails> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
		this.context = context;

	}

	public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater
					.inflate(R.layout.drink_images_layout, null);
			holder = new ViewHolder();

			holder.itemImage = (ImageView) convertView
					.findViewById(R.id.drink_icon_image);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.itemImage.setImageBitmap(itemDetailsrrayList.get(position)
				.getImage());

		return convertView;
	}

	static class ViewHolder {

		ImageView itemImage;

	}
}
