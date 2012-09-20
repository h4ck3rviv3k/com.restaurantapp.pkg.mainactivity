package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.OpenableColumns;
import android.sax.StartElementListener;
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

public class FavouriteAdapter extends BaseAdapter {
	Calendar c = Calendar.getInstance();
	private static ArrayList<ItemDetails> itemDetailsrrayList;

	private LayoutInflater l_Inflater;

	private Context context;
	protected AlertDialog alert;

	// private OnClickListener listner;

	public FavouriteAdapter(Context context, ArrayList<ItemDetails> results) {
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
			convertView = l_Inflater.inflate(R.layout.foodlist_menu_view, null);
			holder = new ViewHolder();
			holder.txt_itemName = (TextView) convertView
					.findViewById(R.id.title);
			holder.txt_itemDescription = (TextView) convertView
					.findViewById(R.id.detail);
			holder.txt_itemPrice = (TextView) convertView
					.findViewById(R.id.price);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.img);
			holder.likeBtn = (Button) convertView
					.findViewById(R.id.like_food_btn);
			holder.addBtn = (Button) convertView
					.findViewById(R.id.Add_food_btn);

			holder.likeBtn.setOnClickListener(new View.OnClickListener() {

				public ItemDetails item_list;
				public SQLiteDatabase db;

				@Override
				public void onClick(View v) {

					try {
						item_list = itemDetailsrrayList.get((Integer) v
								.getTag());
						DBConnection dbcon = new DBConnection(context);
						db = dbcon.getConnection();
						// db.execSQL("DROP table favourite_tbl");
						// db.execSQL("create table if not exists favourite_tbl(item_id varchar2,user_id varchar2,primary key(item_id))");
						String user_id = (new GlobalVariables()).getUser_id();
						if (user_id != "") {
							db.delete("temp_favourite_tbl", "item_id='"
									+ item_list.getItem_id() + "'", null);
							db.close();
							notifydata();
							Toast.makeText(context,
									"You UnLiked " + item_list.getName(),
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Toast.makeText(context.getApplicationContext(),
								e.toString(), Toast.LENGTH_SHORT).show();
						db.delete("favourite_tbl",
								"item_id='" + item_list.getItem_id() + "'",
								null);
						db.close();

					}

				}
			});

			holder.addBtn.setOnClickListener(new View.OnClickListener() {

				private SQLiteDatabase db;

				private ItemDetails item_list;
				String specialRequest = "";

				@Override
				public void onClick(View v) {

					try {
						item_list = itemDetailsrrayList.get((Integer) v
								.getTag());

						DBConnection dbcon = new DBConnection(context);
						db = dbcon.getConnection();

						// db.execSQL("DROP table order_tbl");
						// db.execSQL("DROP table if exists order_tbl");

						db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");

						db.execSQL("insert into order_tbl(order_id,item_id,special_request,quantity) values(100,'"
								+ item_list.getItem_id()
								+ "','"
								+ specialRequest + "',1)");

						db.close();
						Toast.makeText(context, item_list.getName() + " Added",
								Toast.LENGTH_SHORT).show();

					} catch (Exception e) {
						Toast.makeText(context.getApplicationContext(),
								"Item Already Added", Toast.LENGTH_SHORT)
								.show();
					}

				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt_itemName
				.setText(itemDetailsrrayList.get(position).getName());
		holder.txt_itemDescription.setText(itemDetailsrrayList.get(position)
				.getItemDescription());
		holder.txt_itemPrice.setText("Rs "
				+ itemDetailsrrayList.get(position).getPrice());
		holder.itemImage.setImageBitmap(itemDetailsrrayList.get(position)
				.getImage());

		holder.likeBtn.setText("UnLike");
		holder.likeBtn.setTag(position);

		holder.addBtn.setText("Add");
		holder.addBtn.setTag(position);

		return convertView;
	}

	static class ViewHolder {

		TextView txt_itemName;
		TextView txt_itemDescription;
		TextView txt_itemPrice;
		ImageView itemImage;
		Button likeBtn;
		Button addBtn;
	}

	public void notifydata() {

		(new HeaderFooterActivity()).headerNavigation();

	}
}
