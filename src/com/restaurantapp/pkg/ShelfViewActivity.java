/**
 * ShelfView Application v1.2
 * 
 * This project is a tutorial project and free.
 * 
 *  Android version: IceCream Sandwich
 *  @author: Omid nazifi
 *  Date: 5/10/2012
 * 
 */
package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.List;

import org.quaere.alias.ListVisitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShelfViewActivity extends HeaderFooterActivity {
	/** Called when the activity is first created. */
	private VerticalAdapter verListAdapter;

	private Cursor food_items;
	private String item_id;
	private String item_name;
	private String item_descrp;
	private String item_price;
	private String menu;
	private Dialog dialog;
	private String total_request = "";
	private Cursor photo_cursor;
	private SQLiteDatabase Db;
	private Cursor drink_cat_cursor;
	public Library lb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent i = getIntent();
		menu = i.getStringExtra("menu_name");

		headerNavigation();
		footerNavigation();

		// Db=new DBConnection(getApplicationContext()).getConnection();
		// drink_cat_cursor=Db.rawQuery("select * from Drink_category", null);
		// drink_cat_cursor.moveToFirst();
		// if(drink_cat_cursor!=null){
		// do{
		//
		// }while(drink_cat_cursor.moveToNext());
		// }

		/*
		 * Calling Library & BookItem classes for create list of groups
		 * groupbyArrayBookItem return back array of array of items
		 */
		lb = new Library();
		for (BookItem item : BookItem.ALL_BOOKS) {
			lb.addBookItem(item);
		}

		ListView drink_list = (ListView) findViewById(R.id.drinkList);
		ArrayList<ArrayList<BookItem>> groupList = new ArrayList<ArrayList<BookItem>>();
		groupList = lb.groupbyArrayBookItem(Library.AUTHOR);

		verListAdapter = new VerticalAdapter(this, R.layout.row, groupList);
		drink_list.setAdapter(verListAdapter);

		verListAdapter.notifyDataSetChanged();
	}

	/**
	 * This class add a list of ArrayList to ListView that it include multi
	 * items as bookItem.
	 */
	private class VerticalAdapter extends ArrayAdapter<ArrayList<BookItem>> {

		private int resource;

		public VerticalAdapter(Context _context, int _ResourceId,
				ArrayList<ArrayList<BookItem>> _items) {
			super(_context, _ResourceId, _items);
			this.resource = _ResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;

			if (convertView == null) {
				rowView = LayoutInflater.from(getContext()).inflate(resource,
						null);
			} else {
				rowView = convertView;
			}
			Log.d("drink_ID-----", lb.arrayBookItem.get(position).getTitle());
			if (position == 0) {
				final ArrayList<ItemDetails> items;
				final HorizontalListView hListView = (HorizontalListView) rowView
						.findViewById(R.id.subListview);
				getFoodItems(lb.arrayBookItem.get(position).getTitle());
				ArrayList<ItemDetails> image_details = new ArrayList<ItemDetails>();
				image_details = GetSearchResults();
				items = image_details;
				DrinkAdapter drinkadapter = new DrinkAdapter(
						getApplicationContext(), image_details);
				// HorizontalAdapter horListAdapter = new HorizontalAdapter(
				// getContext(), R.layout.item, getItem(position));
				hListView.setScrollbarFadingEnabled(true);

				hListView.setAdapter(drinkadapter);

				hListView.setOnItemClickListener(new OnItemClickListener() {

					private Cursor photo_cursor;
					private Object o;

					@Override
					public void onItemClick(AdapterView<?> a, View v, int pos,
							long id) {

						showFoodDetail(items.get(pos).getItem_id(),
								items.get(pos).getName(), items.get(pos)
										.getItemDescription(), items.get(pos)
										.getPrice());
					}

				});
			} else if (position == 1) {
				final ArrayList<ItemDetails> items1;
				final HorizontalListView hListView1 = (HorizontalListView) rowView
						.findViewById(R.id.subListview);
				getFoodItems(lb.arrayBookItem.get(position).getTitle());
				ArrayList<ItemDetails> image_details1 = new ArrayList<ItemDetails>();
				image_details1 = GetSearchResults();
				items1 = image_details1;
				DrinkAdapter drinkadapter1 = new DrinkAdapter(
						getApplicationContext(), image_details1);
				// HorizontalAdapter horListAdapter = new HorizontalAdapter(
				// getContext(), R.layout.item, getItem(position));
				hListView1.setScrollbarFadingEnabled(true);

				hListView1.setAdapter(drinkadapter1);

				hListView1.setOnItemClickListener(new OnItemClickListener() {

					private Cursor photo_cursor;
					private Object o;

					@Override
					public void onItemClick(AdapterView<?> a, View v, int pos,
							long id) {

						showFoodDetail(items1.get(pos).getItem_id(), items1
								.get(pos).getName(), items1.get(pos)
								.getItemDescription(), items1.get(pos)
								.getPrice());
					}

				});
			} else if (position == 2) {
				final ArrayList<ItemDetails> items2;
				final HorizontalListView hListView2 = (HorizontalListView) rowView
						.findViewById(R.id.subListview);
				getFoodItems(lb.arrayBookItem.get(position).getTitle());
				ArrayList<ItemDetails> image_details2 = new ArrayList<ItemDetails>();
				image_details2 = GetSearchResults();
				items2 = image_details2;
				DrinkAdapter drinkadapter2 = new DrinkAdapter(
						getApplicationContext(), image_details2);
				// HorizontalAdapter horListAdapter = new HorizontalAdapter(
				// getContext(), R.layout.item, getItem(position));
				hListView2.setScrollbarFadingEnabled(true);

				hListView2.setAdapter(drinkadapter2);

				hListView2.setOnItemClickListener(new OnItemClickListener() {

					private Cursor photo_cursor;
					private Object o;

					@Override
					public void onItemClick(AdapterView<?> a, View v, int pos,
							long id) {

						showFoodDetail(items2.get(pos).getItem_id(), items2
								.get(pos).getName(), items2.get(pos)
								.getItemDescription(), items2.get(pos)
								.getPrice());
					}

				});
			} else if (position == 3) {
				final ArrayList<ItemDetails> items3;
				final HorizontalListView hListView3 = (HorizontalListView) rowView
						.findViewById(R.id.subListview);
				getFoodItems(lb.arrayBookItem.get(position).getTitle());
				ArrayList<ItemDetails> image_details3 = new ArrayList<ItemDetails>();
				image_details3 = GetSearchResults();
				items3 = image_details3;
				DrinkAdapter drinkadapter3 = new DrinkAdapter(
						getApplicationContext(), image_details3);
				// HorizontalAdapter horListAdapter = new HorizontalAdapter(
				// getContext(), R.layout.item, getItem(position));
				hListView3.setScrollbarFadingEnabled(true);

				hListView3.setAdapter(drinkadapter3);

				hListView3.setOnItemClickListener(new OnItemClickListener() {

					private Cursor photo_cursor;
					private Object o;

					@Override
					public void onItemClick(AdapterView<?> a, View v, int pos,
							long id) {

						showFoodDetail(items3.get(pos).getItem_id(), items3
								.get(pos).getName(), items3.get(pos)
								.getItemDescription(), items3.get(pos)
								.getPrice());
					}

				});
			} else if (position == 4) {
				final ArrayList<ItemDetails> items4;
				final HorizontalListView hListView4 = (HorizontalListView) rowView
						.findViewById(R.id.subListview);
				getFoodItems(lb.arrayBookItem.get(position).getTitle());
				ArrayList<ItemDetails> image_details4 = new ArrayList<ItemDetails>();
				image_details4 = GetSearchResults();
				items4 = image_details4;
				DrinkAdapter drinkadapter = new DrinkAdapter(
						getApplicationContext(), image_details4);
				// HorizontalAdapter horListAdapter = new HorizontalAdapter(
				// getContext(), R.layout.item, getItem(position));
				hListView4.setScrollbarFadingEnabled(true);

				hListView4.setAdapter(drinkadapter);

				hListView4.setOnItemClickListener(new OnItemClickListener() {

					private Cursor photo_cursor;
					private Object o;

					@Override
					public void onItemClick(AdapterView<?> a, View v, int pos,
							long id) {

						showFoodDetail(items4.get(pos).getItem_id(), items4
								.get(pos).getName(), items4.get(pos)
								.getItemDescription(), items4.get(pos)
								.getPrice());
					}

				});
			}

			return rowView;
		}
	}

	private void showFoodDetail(final String item_id, final String name,
			String descrp, String price) {
		final List<String> specialRequest = new ArrayList<String>();

		// TODO Auto-generated method stub
		db = (new DBConnection(getApplicationContext())).getConnection();
		byte[] image = null;
		try {
			photo_cursor = db.rawQuery(
					"select photo from item where item_code='" + item_id + "'",
					null);
			photo_cursor.moveToPosition(0);
			image = photo_cursor.getBlob(0);
		} catch (Exception e) {
			Log.d("error", e.toString());
		}
		ItemDetails it = new ItemDetails();
		it.setImage(image);
		dialog = new Dialog(ShelfViewActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.custom_dialog_drink_layout);
		dialog.setCanceledOnTouchOutside(true);
		TextView title = (TextView) dialog.findViewById(R.id.Dialog_title);
		title.setText(name + " | Rs :" + price);

		TextView dialog_txt = (TextView) dialog.findViewById(R.id.food_content);
		dialog_txt.setText(descrp);
		dialog_txt.setTextColor(Color.BLACK);

		ImageView dialog_img = (ImageView) dialog
				.findViewById(R.id.sel_item_img);
		dialog_img.setImageBitmap(it.getImage());

		Button add_btn = (Button) dialog.findViewById(R.id.dialog_add_btn);
		add_btn.setOnClickListener(new View.OnClickListener() {

			private AlertDialog alert;

			@Override
			public void onClick(View v) {

				DBConnection dbcon = new DBConnection(getApplicationContext());
				db = dbcon.getConnection();
				final String[] items = { "Mountain Dew", "Mirinda", "Sprite",
						"lemon Juice", "Coka cola", "soda", "None" };
				final boolean[] selections = new boolean[items.length];
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ShelfViewActivity.this);
				builder.setTitle("Would you like to mix your drink with anything");
				builder.setMultiChoiceItems(items, selections,
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

									for (int i = 0; i < specialRequest.size(); i++) {

										total_request = total_request
												+ specialRequest.get(i) + "+";

									}
									db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");

									db.execSQL("insert into order_tbl(order_id,item_id,special_request,quantity) values(100,'"
											+ item_id
											+ "','"
											+ total_request
											+ "',1)");
									db.close();

									Toast.makeText(getApplicationContext(),
											name + " Added", Toast.LENGTH_SHORT)
											.show();

									alert.dismiss();

								} catch (Exception e) {
									dialog.dismiss();
									Toast.makeText(getApplicationContext(),
											"Item already Added",
											Toast.LENGTH_SHORT).show();
								}
							}

						});

				alert = builder.create();
				alert.show();

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

	public void closeBackDialog() {
		dialog.dismiss();

	}

	/*
	 * This class add some items to Horizontal ListView this ListView include
	 * several bookItem.
	 */
	// private class HorizontalAdapter extends ArrayAdapter<BookItem> {
	//
	// private int resource;
	//
	// public HorizontalAdapter(Context _context, int _textViewResourceId,
	// ArrayList<BookItem> _items) {
	// super(_context, _textViewResourceId, _items);
	// this.resource = _textViewResourceId;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// View retval = LayoutInflater.from(getContext()).inflate(
	// this.resource, null);
	//
	// TextView topText = (TextView) retval.findViewById(R.id.title);
	// TextView bottomText = (TextView) retval.findViewById(R.id.author);
	//
	// topText.setText(getItem(position).getAuthor());
	// bottomText.setText(getItem(position).getTitle());
	//
	// return retval;
	// }
	// }

	public void getFoodItems(String drink_rowId) {
		SQLiteDatabase db = openOrCreateDatabase("restaurant",
				MODE_WORLD_READABLE, null);

		food_items = db.rawQuery("select * from item where drink_category_id='"
				+ drink_rowId + "'", null);
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