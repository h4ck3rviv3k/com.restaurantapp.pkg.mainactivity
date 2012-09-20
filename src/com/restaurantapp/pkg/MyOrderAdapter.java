package com.restaurantapp.pkg;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.provider.OpenableColumns;
import android.sax.StartElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyOrderAdapter extends BaseAdapter {

	private static ArrayList<ItemDetails> itemDetailsrrayList;

	private LayoutInflater l_Inflater;

	private MyOrderList context;

	private String table_name;

	public MyOrderAdapter(MyOrderList context, ArrayList<ItemDetails> results,
			String table_name) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
		this.context = context;
		this.table_name = table_name;

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
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.layout_myorder, parent,
					false);

			holder.txt_itemName = (TextView) convertView
					.findViewById(R.id.myorder_titletag);

			holder.txt_itemPrice = (TextView) convertView
					.findViewById(R.id.myorder_price);
			holder.txt_itemQuantity = (TextView) convertView
					.findViewById(R.id.myorder_quantity);
			holder.itemImage = (ImageView) convertView
					.findViewById(R.id.myorder_food_img);
			holder.plusBtn = (ImageButton) convertView
					.findViewById(R.id.myorder_plus_btn);
			holder.minusBtn = (ImageButton) convertView
					.findViewById(R.id.myorder_minus_btn);
			holder.removeBtn = (Button) convertView
					.findViewById(R.id.myorder_remove_btn);
			holder.edittext = (TextView) convertView
					.findViewById(R.id.myorder_edit_txt);

			holder.plusBtn.setOnClickListener(new View.OnClickListener() {

				private SQLiteDatabase db;
				private ItemDetails item_list;
				private ContentValues cv;
				int count = 1;

				@Override
				public void onClick(View v) {

					item_list = itemDetailsrrayList.get((Integer) v.getTag());
					count = item_list.getcount();
					count++;

					try {
						DBConnection dbcon = new DBConnection(context);
						db = dbcon.getConnection();
						cv = new ContentValues();
						cv.put("quantity", count);
						db.update(table_name, cv,
								"item_id='" + item_list.getItem_id() + "'",
								null);
						Log.d("Count befre", "" + count);
						notifydata();

					} catch (Exception e) {
						Log.d("Count", "" + e);
					}

				}
			});

			holder.minusBtn.setOnClickListener(new View.OnClickListener() {

				private SQLiteDatabase db;
				private ItemDetails item_list;
				private ContentValues cv;
				int count;

				@Override
				public void onClick(View v) {

					item_list = itemDetailsrrayList.get((Integer) v.getTag());
					count = item_list.getcount();

					if (count > 1)
						count--;
					else
						count = 1;

					try {
						DBConnection dbcon = new DBConnection(context);
						db = dbcon.getConnection();
						cv = new ContentValues();
						cv.put("quantity", count);
						db.update(table_name, cv,
								"item_id='" + item_list.getItem_id() + "'",
								null);
						notifydata();

					} catch (Exception e) {
						Log.d("Count", "" + e);
					}

				}
			});

			holder.removeBtn.setOnClickListener(new View.OnClickListener() {

				private SQLiteDatabase db;

				private ItemDetails item_list;
				private ContentValues cv;

				@Override
				public void onClick(View v) {

					try {
						item_list = itemDetailsrrayList.get((Integer) v
								.getTag());

						DBConnection dbcon = new DBConnection(context);
						db = dbcon.getConnection();

						db.delete(table_name,
								"item_id='" + item_list.getItem_id() + "'",
								null);
						db.close();

						notifydata();
					} catch (Exception e) {
						Log.d("errorrrrrr", e.toString());
					}

				}
			});

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt_itemName
				.setText(itemDetailsrrayList.get(position).getName());
		holder.txt_itemQuantity.setText("Quantity");

		holder.txt_itemPrice.setText("Rs :"
				+ itemDetailsrrayList.get(position).getPrice());
		holder.itemImage.setImageBitmap(itemDetailsrrayList.get(position)
				.getImage());

		holder.edittext.setText(Integer.toString(itemDetailsrrayList.get(
				position).getcount()));

		holder.plusBtn.setTag(position);
		holder.minusBtn.setTag(position);

		holder.removeBtn.setText("Remove");
		holder.removeBtn.setTag(position);

		return convertView;
	}

	public class ViewHolder {

		public TextView edittext;
		public TextView txt_itemQuantity;
		public ImageButton minusBtn;
		public ImageButton plusBtn;
		public Button removeBtn;

		TextView txt_itemName;
		TextView txt_itemDescription;
		TextView txt_itemPrice;
		ImageView itemImage;

		public ViewHolder() {
			// TODO Auto-generated constructor stub
		}

	}

	public void notifydata() {
		context.Total = 0;
		context.quantity = 0;
		context.getFoodItems("order_tbl");
		context.ad = new MyOrderAdapter(context, context.image_details,
				"order_tbl");
		context.ad.notifyDataSetChanged();
		context.lv1.setAdapter(context.ad);

	}
}
