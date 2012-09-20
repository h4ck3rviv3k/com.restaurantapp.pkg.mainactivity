package com.restaurantapp.pkg;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BeveragesList extends HeaderFooterActivity {

	private Cursor food_items;
	private String item_name;
	private String item_descrp;
	private String item_price;

	private ArrayList<ItemDetails> image_details;

	private String menu;
	private TextView layout_title_name;
	private String title_name;
	private String item_id;
	private Cursor photo_cursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beverages_layout);
		Intent i = getIntent();
		menu = i.getStringExtra("menu_name");

		footerNavigation();
		headerNavigation();
		getFoodItems();
		layout_title_name = (TextView) findViewById(R.id.beveragelist_title);
		layout_title_name.setText(menu);
		getFoodItems();
		image_details = GetSearchResults();
		final ListView lv1 = (ListView) findViewById(R.id.beverageLV_main);

		lv1.setAdapter(new ItemListBaseAdapter(this, image_details));

		lv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				Object o = lv1.getItemAtPosition(position);
				ItemDetails obj_itemDetails = (ItemDetails) o;

				showFoodDetail(obj_itemDetails.getItem_id(),
						obj_itemDetails.getName(),
						obj_itemDetails.getItemDescription(),
						obj_itemDetails.getPrice());
			}

			private void showFoodDetail(String id, String name, String descrp,
					String price) {
				// TODO Auto-generated method stub
				db = (new DBConnection(getApplicationContext()))
						.getConnection();
				byte[] image = null;
				try {
					photo_cursor = db.rawQuery(
							"select photo from item where item_code='" + id
									+ "'", null);
					photo_cursor.moveToPosition(0);
					image = photo_cursor.getBlob(0);
				} catch (Exception e) {
					Log.d("error", e.toString());
				}
				ItemDetails it = new ItemDetails();
				it.setImage(image);
				final Dialog dialog = new Dialog(BeveragesList.this);

				dialog.setContentView(R.layout.custom_dialog_layout);
				dialog.setTitle(name + " Rs :" + price);
				dialog.setCancelable(true);

				TextView dialog_txt = (TextView) dialog
						.findViewById(R.id.food_content);
				dialog_txt.setText(descrp);

				ImageView dialog_img = (ImageView) dialog
						.findViewById(R.id.sel_item_img);
				dialog_img.setImageBitmap(it.getImage());

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
		});
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

}