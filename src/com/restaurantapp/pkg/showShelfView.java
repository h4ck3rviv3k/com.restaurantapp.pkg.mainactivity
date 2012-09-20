package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.Calendar;

import com.restaurantapp.pkg.DrinkAdapter.ViewHolder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class showShelfView extends HeaderFooterActivity {

	private Cursor food_items;
	private String item_name;
	private String item_descrp;
	private String item_price;
	public Object o;
	private ArrayList<ItemDetails> image_details;
	private int categoryid;
	public int pos;
	private String item_id;
	private SQLiteDatabase db;
	private String menu;
	private ImageView imageView;
	private ArrayList<ItemDetails> results;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drink_shelf_layout);
		Intent in = getIntent();
		menu = in.getStringExtra("menu_name");
		footerNavigation();
		headerNavigation();
		getFoodItems();
		image_details = GetSearchResults();

		int numRow = 8;
		int numCol = 15;

		TableLayout tblLayout = (TableLayout) findViewById(R.id.tblLayout);

		for (int i = 0; i < numRow; i++) {
			HorizontalScrollView HSV = new HorizontalScrollView(this);
			HSV.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));

			TableRow tblRow = new TableRow(this);
			tblRow.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			tblRow.setBackgroundResource(R.drawable.bookshelf);

			for (int j = 0; j < numCol; j++) {
				imageView = new ImageView(this);
			//	imageView.setImageResource(new ImageAdapter(results));


				tblRow.addView(imageView, j);
			}

			HSV.addView(tblRow);
			tblLayout.addView(HSV, i);
		}

	}

	public void getFoodItems() {
		SQLiteDatabase db = openOrCreateDatabase("restaurant",
				MODE_WORLD_READABLE, null);

		food_items = db.rawQuery("select * from item where menu_type='" + menu
				+ "'", null);
		food_items.moveToPosition(0);

		db.close();

	}

	private ArrayList<ItemDetails> GetSearchResults() {
		ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
		int i = 0;
		try {
			if (food_items != null) {

				do {
					item_id = food_items.getString(1);
					item_name = food_items.getString(0);
					item_descrp = food_items.getString(2);
					item_price = food_items.getString(17);
					ItemDetails item_details = new ItemDetails();
					item_details.setName(item_name);
					item_details.setItem_id(item_id);
					item_details.setItemDescription(item_descrp);
					item_details.setPrice(item_price);
					item_details.setImage(food_items.getBlob(10));

					results.add(item_details);
				} while (food_items.moveToNext());

			}
		} catch (Exception e) {
			Log.d("Coooooool Errorrrrrrrrrrrr", e.toString());
		}

		return results;
	}

	public class ImageAdapter extends BaseAdapter {
		Calendar c = Calendar.getInstance();
		private ArrayList<ItemDetails> itemDetailsrrayList;

		private LayoutInflater l_Inflater;

		private Context context;
		private Cursor photo_cursor;

		// private OnClickListener listner;

		public ImageAdapter(ArrayList<ItemDetails> results) {
			itemDetailsrrayList = results;
			l_Inflater = LayoutInflater.from(context);

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
				convertView = l_Inflater.inflate(R.layout.drink_images_layout,
						null);
				holder = new ViewHolder();

				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.drink_icon_image);
				holder.itemImage.setTag(position);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.itemImage.setImageBitmap(itemDetailsrrayList.get(position)
					.getImage());
			holder.itemImage.setOnClickListener(new View.OnClickListener() {

				private ItemDetails item_list;

				@Override
				public void onClick(View v) {
					item_list = itemDetailsrrayList.get((Integer) v.getTag());
					showFoodDetail(item_list.getItem_id(), item_list.getName(),
							item_list.getItemDescription(),
							item_list.getPrice());

				}
			});

			return convertView;
		}

		private void showFoodDetail(final String item_id, String name,
				String descrp, String price) {
			// TODO Auto-generated method stub
			db = (new DBConnection(getApplicationContext())).getConnection();
			byte[] image = null;
			try {
				photo_cursor = db.rawQuery(
						"select photo from item where item_code='" + item_id
								+ "'", null);
				photo_cursor.moveToPosition(0);
				image = photo_cursor.getBlob(0);
			} catch (Exception e) {
				Log.d("error", e.toString());
			}
			ItemDetails it = new ItemDetails();
			it.setImage(image);
			final Dialog dialog = new Dialog(showShelfView.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.setContentView(R.layout.custom_dialog_drink_layout);
			TextView title = (TextView) dialog.findViewById(R.id.Dialog_title);
			title.setText(name + " | Rs :" + price);

			TextView dialog_txt = (TextView) dialog
					.findViewById(R.id.food_content);
			dialog_txt.setText(descrp);
			dialog_txt.setTextColor(Color.BLACK);

			ImageView dialog_img = (ImageView) dialog
					.findViewById(R.id.sel_item_img);
			dialog_img.setImageBitmap(it.getImage());

			Button add_btn = (Button) dialog.findViewById(R.id.dialog_add_btn);
			add_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					DBConnection dbcon = new DBConnection(
							getApplicationContext());
					db = dbcon.getConnection();
					HeaderFooterActivity h = new HeaderFooterActivity();
					// db.execSQL("DROP table order_tbl");
					// db.execSQL("DROP table if exists order_tbl");
					try {
						db.execSQL("create table if not exists order_tbl(order_id INTEGER NOT NULL,item_id nvarchar2,quantity INTEGER,primary key(item_id))");

						db.execSQL("insert into order_tbl(order_id,item_id,special_request,quantity) values(100,'"
								+ item_id + "','none',1)");
						db.close();
						dialog.dismiss();
					} catch (Exception e) {
						dialog.dismiss();
						Toast.makeText(getApplicationContext(),
								"Item already Added", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			Button dialog_btn = (Button) dialog
					.findViewById(R.id.dialog_cancel_btn);
			dialog_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}

		class ViewHolder {

			ImageView itemImage;

		}
	}

}