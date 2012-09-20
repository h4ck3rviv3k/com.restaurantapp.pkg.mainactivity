package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class DrinkGridView extends HeaderFooterActivity {
	private Cursor food_items;
	private String item_name;
	private String item_descrp;
	private String item_price;
	public Object o;
	public Context mcontext;
	private ArrayList<ItemDetails> image_details;
	private int categoryid;
	public int pos;
	private String item_id;
	private SQLiteDatabase db;
	private String menu;
	private AlertDialog alert;
	private String total_request = "";
	public int flag = 0;
	public Dialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drinks_layout);
		mcontext = getApplicationContext();
		Intent i = getIntent();
		menu = i.getStringExtra("menu_name");
		footerNavigation();
		headerNavigation();
		getFoodItems();
		image_details = GetSearchResults();

		final GridView gv = (GridView) findViewById(R.id.drink_grid);
		gv.setAdapter(new DrinkAdapter(this, image_details));

		gv.setOnItemClickListener(new OnItemClickListener() {

			private Cursor photo_cursor;

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				pos = position;
				o = gv.getItemAtPosition(position);
				ItemDetails obj_itemDetails = (ItemDetails) o;
				gv.setSelection(position);
				showFoodDetail(obj_itemDetails.getItem_id(),
						obj_itemDetails.getName(),
						obj_itemDetails.getItemDescription(),
						obj_itemDetails.getPrice());
			}

			private void showFoodDetail(final String item_id,
					final String name, String descrp, String price) {
				final List<String> specialRequest = new ArrayList<String>();
				flag = 0;
				// TODO Auto-generated method stub
				db = (new DBConnection(getApplicationContext()))
						.getConnection();
				byte[] image = null;
				try {
					photo_cursor = db.rawQuery(
							"select photo from item where item_code='"
									+ item_id + "'", null);
					photo_cursor.moveToPosition(0);
					image = photo_cursor.getBlob(0);
				} catch (Exception e) {
					Log.d("error", e.toString());
				}
				ItemDetails it = new ItemDetails();
				it.setImage(image);
				dialog = new Dialog(DrinkGridView.this,
						android.R.style.Theme_Translucent_NoTitleBar);
				dialog.setContentView(R.layout.custom_dialog_drink_layout);
				dialog.setCanceledOnTouchOutside(true);
				TextView title = (TextView) dialog
						.findViewById(R.id.Dialog_title);
				title.setText(name + " | Rs :" + price);

				TextView dialog_txt = (TextView) dialog
						.findViewById(R.id.food_content);
				dialog_txt.setText(descrp);
				dialog_txt.setTextColor(Color.BLACK);

				ImageView dialog_img = (ImageView) dialog
						.findViewById(R.id.sel_item_img);
				dialog_img.setImageBitmap(it.getImage());

				Button add_btn = (Button) dialog
						.findViewById(R.id.dialog_add_btn);
				add_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						DBConnection dbcon = new DBConnection(
								getApplicationContext());
						db = dbcon.getConnection();
						final String[] items = { "Mountain Dew", "Mirinda",
								"Sprite", "lemon Juice", "Coka cola", "soda",
								"None" };
						final boolean[] selections = new boolean[items.length];
						AlertDialog.Builder builder = new AlertDialog.Builder(
								DrinkGridView.this);
						builder.setTitle("Would you like to mix your drink with anything");
						builder.setMultiChoiceItems(
								items,
								selections,
								new DialogInterface.OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {

										specialRequest.add(items[which]);

									}
								});

						builder.setNegativeButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										closeBackDialog();
										try {

											for (int i = 0; i < specialRequest
													.size(); i++) {

												total_request = total_request
														+ specialRequest.get(i)
														+ "+";

											}
											db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");

											db.execSQL("insert into order_tbl(order_id,item_id,special_request,quantity) values(100,'"
													+ item_id
													+ "','"
													+ total_request + "',1)");
											db.close();

											Toast.makeText(
													getApplicationContext(),
													name + " Added",
													Toast.LENGTH_SHORT).show();

											alert.dismiss();

										} catch (Exception e) {
											dialog.dismiss();
											Toast.makeText(
													getApplicationContext(),
													"Item already Added",
													Toast.LENGTH_SHORT).show();
										}
									}

								});

						alert = builder.create();
						alert.show();
						Log.d("flag out----", "" + flag);

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
		});
	}

	public void closeBackDialog() {
		dialog.dismiss();

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

}