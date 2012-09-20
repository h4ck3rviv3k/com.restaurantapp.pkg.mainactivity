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

public class FavouriteActivity extends HeaderFooterActivity {

	public Cursor food_items;
	public String item_name;
	public String item_descrp;
	public String item_price;

	public ArrayList<ItemDetails> image_details;

	public String menu;
	public TextView layout_title_name;
	public String title_name;
	public String item_id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favourites_dialog);
		ItemDetails item_details = new ItemDetails();

		footerNavigation();
		headerNavigation();

		// layout_title_name = (TextView) findViewById(R.id.beveragelist_title);
		// layout_title_name.setText("Vivek's Favourite");
		getFoodItems();

		final ListView lv1 = (ListView) findViewById(R.id.listV_main);

		lv1.setAdapter(new FavouriteAdapter(getApplicationContext(),
				image_details));

		lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				Object o = lv1.getItemAtPosition(position);
				ItemDetails obj_itemDetails = (ItemDetails) o;

				showFoodDetail(obj_itemDetails.getName(),
						obj_itemDetails.getItemDescription(),
						obj_itemDetails.getPrice());
			}

			private void showFoodDetail(String name, String descrp, String price) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(FavouriteActivity.this);

				dialog.setContentView(R.layout.custom_dialog_layout);
				dialog.setTitle(name + " " + price);
				dialog.setCancelable(true);

				TextView dialog_txt = (TextView) dialog
						.findViewById(R.id.food_content);
				dialog_txt.setText(descrp);

				ImageView dialog_img = (ImageView) dialog
						.findViewById(R.id.sel_item_img);
				dialog_img.setImageResource(R.drawable.paneer);

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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getFoodItems();

		final ListView lv1 = (ListView) findViewById(R.id.listV_main);
		FavouriteAdapter ad = new FavouriteAdapter(this, image_details);
		lv1.setAdapter(ad);

	}

	public void getFoodItems() {
		SQLiteDatabase db = openOrCreateDatabase("restaurant",
				MODE_WORLD_READABLE, null);

		food_items = db.rawQuery("select * from favourite_tbl", null);
		food_items.moveToPosition(0);
		image_details = GetSearchResults();

	}

	private ArrayList<ItemDetails> GetSearchResults() {
		ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();
		Log.d("length favourite", "" + food_items.getInt(0));

		try {
			if (food_items != null) {

				do {
					fav_item = db.rawQuery("select * from item", null);
					fav_item.moveToPosition(0);
					item_id = fav_item.getString(1);
					item_name = fav_item.getString(0);
					item_descrp = fav_item.getString(2);
					item_price = fav_item.getString(17);
					ItemDetails item_details = new ItemDetails();
					item_details.setItem_id(item_id);
					item_details.setName(item_name);
					item_details.setItemDescription(item_descrp);
					item_details.setPrice(item_price);
					item_details.setImage(fav_item.getBlob(10));
					results.add(item_details);
				} while (food_items.moveToNext());
				db.close();
			}
		} catch (Exception e) {
			Log.d("Coooooool Errorrrrrrrrrrrr", e.toString());
		}

		return results;
	}

}