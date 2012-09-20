package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncContext;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.restaurantapp.pkg.SessionEvents.AuthListener;
import com.restaurantapp.pkg.SessionEvents.LogoutListener;

public class HeaderFooterActivity extends Activity {

	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {

		}

		public void onAuthFail(String error) {

		}
	}

	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {

		}

		public void onLogoutFinish() {

		}
	}

	public Button home;
	public Button food;
	public Button special;
	public Button beverage;
	public AlertDialog checkpopup = null;
	public AlertDialog checkpopup1 = null;
	public AlertDialog AssistPopup = null;
	public Button wine;
	public Button myorder;
	public ListView cat_list;
	public LoginButton fblogin_btn;
	public static String APP_ID = "346381592110362"; // Replace your App ID

	public static Facebook facebook;
	public static AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	public SharedPreferences mPrefs;
	public int count;
	public SQLiteDatabase db;
	public Cursor fav_item;
	public Cursor count_cursor;
	public ImageButton fav_btn;
	public TextView username;
	public Context context;
	public ImageButton search_btn;
	private Cursor items;
	private ImageButton assist_btn;
	public String Main_table = "T2";
	ItemDetails it = new ItemDetails();
	private Cursor mac;
	public ProgressDialog pdialog;

	public void headerNavigation() {
		db = (new DBConnection(getApplicationContext())).getConnection();

		// WifiManager wifiManager = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		// WifiInfo wInfo = wifiManager.getConnectionInfo();
		// String macAddress = wInfo.getMacAddress();
		//
		// Log.d("IMEI no.", macAddress);
		// mac = db.rawQuery("select * from imei_tbl where imei_code='"
		// + macAddress + "'", null);
		// mac.moveToFirst();
		// if (mac != null)
		// Main_table = mac.getString(1);
		items = db.rawQuery("select * from login_tbl", null);
		items.moveToFirst();
		assist_btn = (ImageButton) findViewById(R.id.third_tab);
		fav_btn = (ImageButton) findViewById(R.id.first_tab);
		username = (TextView) findViewById(R.id.username_id);
		fblogin_btn = (LoginButton) findViewById(R.id.fb_login_btn);

		search_btn = (ImageButton) findViewById(R.id.second_tab);
		facebook = new Facebook(APP_ID);

		mAsyncRunner = new AsyncFacebookRunner(facebook);
		SessionStore.restore(facebook, this);

		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());

		try {

			fblogin_btn.init(HeaderFooterActivity.this, facebook);

			mAsyncRunner.request("me", new SampleRequestListener());

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "no internet connection",
					Toast.LENGTH_SHORT).show();

		}

		// ------------Assist Button Handling

		assist_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				AssistPopup = new AlertDialog.Builder(HeaderFooterActivity.this)
						.setTitle("Assistance Alert !")
						.setMessage("Do you Need Assistance..")
						.setPositiveButton("Assistance",
								new AlertDialog.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										try {
											DBConnection dbConnection = new DBConnection(
													getApplicationContext());
											dbConnection.createSocket();
											dbConnection.dataOutputStream
													.writeUTF("4");
											dbConnection.dataOutputStream
													.writeUTF(Main_table);
											restartFirstActivity();

										} catch (Exception e) {

										}
										AssistPopup.dismiss();

									}
								})

						.setNegativeButton("Dismiss",
								new AlertDialog.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										AssistPopup.dismiss();

									}
								})

						.create();
				AssistPopup.show();
			}
		});

		// --Advance Search Button handling---------

		search_btn.setOnClickListener(new View.OnClickListener() {

			private Cursor cursor_sub_cat;
			private ArrayList<String> Sub_CategoryList;
			private ListView sub1_cat_list;
			private ListView sub2_cat_list;
			private ArrayList<String> Sub2_CategoryList;
			private Cursor sub_item_cat;
			private Cursor photo_cursor;
			public Button first_all;
			public Button second_all;
			public Button third_all;
			private Button search_food;
			private Button search_beverage;
			private Button search_special;
			private Button search_drink;
			private ArrayList<String> beverage_list;
			public View row;
			public View row2;
			public View row3;
			public Dialog dialog_search;

			@Override
			public void onClick(View v) {
				db = (new DBConnection(getApplicationContext()))
						.getConnection();
				final Dialog search_dialog = new Dialog(
						HeaderFooterActivity.this,
						android.R.style.Theme_Translucent_NoTitleBar);
				search_dialog
						.setContentView(R.layout.advance_search_dialog_layout);
				search_dialog.setTitle(R.layout.search_title_buttons);
				first_all = (Button) search_dialog
						.findViewById(R.id.first_row_btn1);
				second_all = (Button) search_dialog
						.findViewById(R.id.first_row_btn2);
				third_all = (Button) search_dialog
						.findViewById(R.id.first_row_btn3);
				cat_list = (ListView) search_dialog
						.findViewById(R.id.second_row_list1);

				sub1_cat_list = (ListView) search_dialog
						.findViewById(R.id.second_row_list2);
				sub2_cat_list = (ListView) search_dialog
						.findViewById(R.id.second_row_list3);

				search_food = (Button) search_dialog
						.findViewById(R.id.search_title_food);
				search_beverage = (Button) search_dialog
						.findViewById(R.id.search_title_beverages);
				search_special = (Button) search_dialog
						.findViewById(R.id.search_title_specials);
				search_drink = (Button) search_dialog
						.findViewById(R.id.search_title_drink);
				Button cancel_btn = (Button) search_dialog
						.findViewById(R.id.dialog_cancel_btn);

				fillFood("Food", "Food_category", "Food_sub_category",
						"Food_category_id", "Food_sub_category_id");

				search_beverage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						search_beverage.setBackgroundColor(Color
								.parseColor("#3BB9FF"));
						search_food.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_special.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_drink.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						fillFood("Beverage", "Beverage_category",
								"Beverage_sub_category",
								"Beverage_category_id",
								"Beverage_sub_category_id");

					}
				});

				search_special.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						search_beverage.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_food.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_special.setBackgroundColor(Color
								.parseColor("#3BB9FF"));
						search_drink.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						fillFood("Special", "Special_category",
								"Special_sub_category", "Special_category_id",
								"Special_sub_category_id");

					}
				});

				search_drink.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						search_beverage.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_food.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_special.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_drink.setBackgroundColor(Color
								.parseColor("#3BB9FF"));
						fillFood("Drink", "Drink_category",
								"Drink_sub_category", "Drink_category_id",
								"Drink_sub_category_id");

					}
				});

				search_food.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						search_beverage.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_food.setBackgroundColor(Color
								.parseColor("#3BB9FF"));
						search_special.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						search_drink.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.likenadd_btn));
						fillFood("Food", "Food_category", "Food_sub_category",
								"Food_category_id", "Food_sub_category_id");

					}
				});

				cancel_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						search_dialog.dismiss();
					}
				});

				search_dialog.show();

			}

			private void fillFood(String menuType, String category_tbl,
					String sub_category_tbl, String cat_id, String sub_cat_id) {
				if (menuType.equals("Food"))
					search_food.setBackgroundColor(Color.parseColor("#3BB9FF"));

				first_all.setBackgroundResource(R.color.transparent_green);
				second_all.setBackgroundResource(R.color.transparent_green);
				third_all.setBackgroundResource(R.color.transparent_green);
				cat_list.setAdapter(null);
				sub1_cat_list.setAdapter(null);
				sub2_cat_list.setAdapter(null);
				final String menu = menuType;
				final String category = category_tbl;
				final String Sub_category = sub_category_tbl;
				final String mcat_id = cat_id;
				final String msub_cat_id = sub_cat_id;

				fillCategoryList(category, Sub_category, mcat_id, msub_cat_id);
				fillSub1CategoryList(Sub_category, msub_cat_id);
				fillSub2CategoryList(menu);
				first_all.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						first_all
								.setBackgroundResource(R.color.transparent_green);
						fillCategoryList(category, Sub_category, mcat_id,
								msub_cat_id);

					}

				});
				second_all.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						second_all
								.setBackgroundResource(R.color.transparent_green);
						fillSub1CategoryList(Sub_category, msub_cat_id);
					}

				});
				third_all.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						third_all
								.setBackgroundResource(R.color.transparent_green);
						fillSub2CategoryList(menu);
					}

				});
			}

			private void fillCategoryList(String a, String b, String c, String d) {
				final String mcategory = a;
				final String msub_category = b;
				final String mcat_id = c;
				final String msub_cat_id = d;

				// ----Fill Category List---------------------------------
				try {
					Cursor cursor_cat = db.rawQuery("select * from "
							+ mcategory, null);
					cursor_cat.moveToFirst();

					String[] category = new String[cursor_cat.getCount()];
					int i = 0;
					if (cursor_cat != null) {
						do {
							category[i] = cursor_cat.getString(1);
							i++;

						} while (cursor_cat.moveToNext());
					}

					ArrayList<String> CategoryList = new ArrayList<String>();
					CategoryList.addAll(Arrays.asList(category));

					ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
							getApplicationContext(),
							R.layout.foodlist_catagory_view, CategoryList);

					cat_list.setAdapter(listAdapter);
				} catch (Exception e) {
					// TODO: handle exception
				}

				cat_list.setOnItemClickListener(new OnItemClickListener() {

					private String cursor;
					private Cursor select_cat_id;

					public void onItemClick(AdapterView<?> a, View v,
							int position, long id) {
						first_all.setBackgroundResource(R.color.search_back);

						if (row != null) {
							row.setBackgroundResource(R.color.search_back);
						}
						row = v;
						v.setBackgroundResource(R.color.transparent_green);

						try {

							sub1_cat_list.setAdapter(null);
							cursor = (String) cat_list
									.getItemAtPosition(position);

							select_cat_id = db.rawQuery(
									"select category_id from " + mcategory
											+ " where category_name='" + cursor
											+ "'", null);
							select_cat_id.moveToFirst();
							Log.d("cat_id", "" + select_cat_id.getInt(0));

							Cursor cursor_sub_cat = db.rawQuery(
									"select sub_category_name from "
											+ msub_category
											+ " where category_id='"
											+ select_cat_id.getString(0) + "'",
									null);
							cursor_sub_cat.moveToFirst();
							Log.d("cat_id", cursor_sub_cat.getString(0));
							getSubCategoryData(cursor_sub_cat);
							// -----------------3rd Column---------
							String[] sub_category = {};
							Sub2_CategoryList = new ArrayList<String>();
							Sub2_CategoryList.addAll(Arrays
									.asList(sub_category));

							ArrayAdapter<String> Sub_listAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.foodlist_catagory_view,
									Sub2_CategoryList);
							sub2_cat_list.setAdapter(Sub_listAdapter);
						} catch (Exception e) {
							Log.d("cursor value", "" + e);
							String[] sub_category = {};
							Sub_CategoryList = new ArrayList<String>();
							Sub_CategoryList.addAll(Arrays.asList(sub_category));

							ArrayAdapter<String> Sub_listAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.foodlist_catagory_view,
									Sub_CategoryList);
							// Sub_listAdapter.notifyDataSetChanged();

							sub1_cat_list.setAdapter(Sub_listAdapter);

							// ----------filling item category..........
							try {

								Cursor m_cursor;
								db = (new DBConnection(getApplicationContext()))
										.getConnection();
								m_cursor = db.rawQuery(
										"select * from item where " + mcat_id
												+ "=" + select_cat_id.getInt(0),
										null);
								m_cursor.moveToFirst();
								String[] sub2_category = new String[m_cursor
										.getCount()];
								int j = 0;
								if (m_cursor != null) {
									do {
										sub2_category[j] = m_cursor
												.getString(0);
										Log.d("SubCatItem",
												m_cursor.getString(2));
										j++;

									} while (m_cursor.moveToNext());
								}

								Sub2_CategoryList = new ArrayList<String>();
								Sub2_CategoryList.addAll(Arrays
										.asList(sub2_category));

								ArrayAdapter<String> Sub2_listAdapter = new ArrayAdapter<String>(
										getApplicationContext(),
										R.layout.foodlist_catagory_view,
										Sub2_CategoryList);
								Sub_listAdapter.notifyDataSetChanged();
								sub2_cat_list.refreshDrawableState();
								sub2_cat_list.setAdapter(Sub2_listAdapter);

							} catch (Exception ex) {
								Log.d("Exception in filling items",
										ex.toString());
								String[] sub_category1 = {};
								Sub2_CategoryList = new ArrayList<String>();
								Sub2_CategoryList.addAll(Arrays
										.asList(sub_category1));

								ArrayAdapter<String> Sub2_listAdapter = new ArrayAdapter<String>(
										getApplicationContext(),
										R.layout.foodlist_catagory_view,
										Sub2_CategoryList);
								Sub_listAdapter.notifyDataSetChanged();
								sub2_cat_list.refreshDrawableState();
								sub2_cat_list.setAdapter(Sub2_listAdapter);
							}
						}

					}

				});

			}

			private void fillSub1CategoryList(final String cat,
					final String subcat_id) {

				// --------Fill Sub Category List------------
				try {
					cursor_sub_cat = db.rawQuery(
							"select sub_category_name from " + cat, null);
					cursor_sub_cat.moveToFirst();
					getSubCategoryData(cursor_sub_cat);
				} catch (Exception e) {
					// TODO: handle exception
				}

				sub1_cat_list.setOnItemClickListener(new OnItemClickListener() {

					private String cursor_item;

					@Override
					public void onItemClick(AdapterView<?> a, View v,
							int position, long id) {
						second_all.setBackgroundResource(R.color.search_back);
						if (row2 != null) {
							row2.setBackgroundResource(R.color.search_back);
						}
						row2 = v;
						v.setBackgroundResource(R.color.transparent_green);
						try {
							sub2_cat_list.setAdapter(null);
							cursor_item = (String) sub1_cat_list
									.getItemAtPosition(position);

							Cursor select_cat_id = db.rawQuery(
									"select sub_category_id from " + cat
											+ " where sub_category_name='"
											+ cursor_item + "'", null);
							select_cat_id.moveToFirst();
							Log.d("cat_id", "" + select_cat_id.getInt(0));

							Cursor cursor_sub_cat = db.rawQuery(
									"select * from item where " + subcat_id
											+ "='" + select_cat_id.getString(0)
											+ "'", null);
							cursor_sub_cat.moveToFirst();
							Log.d("cat_id", "" + cursor_sub_cat.getInt(0));
							getItemSubCategoryData(cursor_sub_cat);

						} catch (Exception e) {
							Log.d("cursor value", "" + e);
						}

					}

				});

			}

			private void fillSub2CategoryList(String menuType) {
				// --------Fill Item Category List-------------
				try {
					sub_item_cat = db.rawQuery(
							"select * from item where menu_type='" + menuType
									+ "'", null);
					sub_item_cat.moveToFirst();
					getItemSubCategoryData(sub_item_cat);
				} catch (Exception e) {
					// TODO: handle exception
				}

				sub2_cat_list.setOnItemClickListener(new OnItemClickListener() {

					private String cursor_item;

					@Override
					public void onItemClick(AdapterView<?> a, View v,
							int position, long id) {
						third_all.setBackgroundResource(R.color.search_back);
						if (row3 != null) {
							row3.setBackgroundResource(R.color.search_back);
						}
						row3 = v;
						v.setBackgroundResource(R.color.transparent_green);
						try {
							cursor_item = (String) sub2_cat_list
									.getItemAtPosition(position);

							Cursor select_cat_id = db.rawQuery(
									"select * from item where item_name='"
											+ cursor_item + "'", null);
							select_cat_id.moveToFirst();
							Log.d("cat_id", "" + select_cat_id.getInt(0));
							showFoodDetail(select_cat_id.getString(1),
									select_cat_id.getString(0),
									select_cat_id.getString(2),
									select_cat_id.getInt(3));

						} catch (Exception e) {
							Log.d("cursor value", "" + e);
						}

					}

				});
			}

			private void showFoodDetail(final String item_id,
					final String name, String descrp, int price) {
				final List<String> specialRequest = new ArrayList<String>();

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
					photo_cursor.close();
					db.close();
				} catch (Exception e) {
					Log.d("error", e.toString());
				}
				ItemDetails it = new ItemDetails();
				it.setImage(image);
				dialog_search = new Dialog(HeaderFooterActivity.this,
						android.R.style.Theme_Translucent_NoTitleBar);
				dialog_search
						.setContentView(R.layout.custom_dialog_drink_layout);
				TextView title = (TextView) dialog_search
						.findViewById(R.id.Dialog_title);
				title.setText(name + " | Rs :" + price);

				TextView dialog_txt = (TextView) dialog_search
						.findViewById(R.id.food_content);
				dialog_txt.setText(descrp);
				dialog_txt.setTextColor(Color.BLACK);

				ImageView dialog_img = (ImageView) dialog_search
						.findViewById(R.id.sel_item_img);
				dialog_img.setImageBitmap(it.getImage());

				Button add_btn = (Button) dialog_search
						.findViewById(R.id.dialog_add_btn);
				add_btn.setOnClickListener(new View.OnClickListener() {

					private AlertDialog alert;

					@Override
					public void onClick(View v) {

						DBConnection dbcon = new DBConnection(
								getApplicationContext());
						db = dbcon.getConnection();
						final String[] items = { "More Spicy", "Less Spicy",
								"exclude onion", "less salt", "More Sugar",
								"Less Sugar", "None" };
						final boolean[] selections = new boolean[items.length];
						AlertDialog.Builder builder = new AlertDialog.Builder(
								HeaderFooterActivity.this);
						builder.setTitle("Pick your Choice");
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
									private String total_request;

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										closeBackSearch();
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

											Toast.makeText(
													getApplicationContext(),
													"Item already Added",
													Toast.LENGTH_SHORT).show();
										}
									}
								});

						alert = builder.create();
						alert.show();

					}
				});

				Button dialog_btn = (Button) dialog_search
						.findViewById(R.id.dialog_cancel_btn);
				dialog_btn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog_search.dismiss();
					}
				});

				dialog_search.show();
			}

			public void closeBackSearch() {
				dialog_search.dismiss();
			}

			private void getSubCategoryData(Cursor cursor_sub_cat2) {

				Cursor m_cursor = cursor_sub_cat2;
				String[] sub_category = new String[m_cursor.getCount()];
				int j = 0;
				if (m_cursor != null) {
					do {
						sub_category[j] = m_cursor.getString(0);
						Log.d("SubCat", m_cursor.getString(0));
						j++;

					} while (m_cursor.moveToNext());

				}

				Sub_CategoryList = new ArrayList<String>();
				Sub_CategoryList.addAll(Arrays.asList(sub_category));

				ArrayAdapter<String> Sub_listAdapter = new ArrayAdapter<String>(
						getApplicationContext(),
						R.layout.foodlist_catagory_view, Sub_CategoryList);

				sub1_cat_list.setAdapter(Sub_listAdapter);

			}

			private void getItemSubCategoryData(Cursor cursor_sub_item) {

				Cursor m_cursor = cursor_sub_item;
				String[] sub_category = new String[m_cursor.getCount()];
				int j = 0;
				if (m_cursor != null) {
					do {
						sub_category[j] = m_cursor.getString(0);
						Log.d("SubCatItem", m_cursor.getString(2));
						j++;

					} while (m_cursor.moveToNext());
				}

				Sub2_CategoryList = new ArrayList<String>();
				Sub2_CategoryList.addAll(Arrays.asList(sub_category));

				ArrayAdapter<String> Sub_listAdapter = new ArrayAdapter<String>(
						getApplicationContext(),
						R.layout.foodlist_catagory_view, Sub2_CategoryList);
				Sub_listAdapter.notifyDataSetChanged();
				sub2_cat_list.refreshDrawableState();
				sub2_cat_list.setAdapter(Sub_listAdapter);

			}

		});
		// ----------Favourite Button handling---------

		fav_btn.setOnClickListener(new View.OnClickListener() {

			public ArrayList<ItemDetails> image_details;
			public Cursor food_items;
			public String item_id;
			public String item_name;
			public String item_descrp;
			public String item_price;
			public AlertDialog fbpopup;
			public Dialog fav_dialog;
			Cursor login_cur;

			@Override
			public void onClick(View v) {

				if (!facebook.isSessionValid()) {
					fbpopup = new AlertDialog.Builder(HeaderFooterActivity.this)
							.setTitle("favourite Alert !")
							.setMessage(
									"Click on the Login button to continue..")
							.setPositiveButton("Ok",
									new AlertDialog.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

											try {
												fblogin_btn
														.init(HeaderFooterActivity.this,
																facebook);
												mAsyncRunner
														.request(
																"me",
																new SampleRequestListener());
											} catch (Exception e) {
												Toast.makeText(
														getApplicationContext(),
														"no internet connection",
														Toast.LENGTH_SHORT)
														.show();
											}
											fbpopup.dismiss();

										}
									})

							.create();
					fbpopup.show();
				} else {

					final Dialog dialog = new Dialog(HeaderFooterActivity.this,
							R.style.PauseDialog);
					dialog.setContentView(R.layout.favourites_dialog);
					db = (new DBConnection(getApplicationContext()))
							.getConnection();
					login_cur = db.rawQuery("select * from login_tbl", null);
					login_cur.moveToFirst();
					dialog.setTitle(login_cur.getString(1));
					getFoodItems();
					image_details = GetSearchResults();
					final ListView lv1 = (ListView) dialog
							.findViewById(R.id.listV_main);
					lv1.setAdapter(new FavouriteAdapter(getBaseContext(),
							image_details));

					lv1.setOnItemClickListener(new OnItemClickListener() {

						public Object o;
						private Cursor photo_cursor;

						@Override
						public void onItemClick(AdapterView<?> a, View v,
								int position, long id) {

							o = lv1.getItemAtPosition(position);
							ItemDetails obj_itemDetails = (ItemDetails) o;

							showFoodDetail(obj_itemDetails.getItem_id(),
									obj_itemDetails.getName(),
									obj_itemDetails.getItemDescription(),
									obj_itemDetails.getPrice());
						}

						private void showFoodDetail(final String item_id,
								final String name, String descrp, String price) {
							final List<String> specialRequest = new ArrayList<String>();

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
								photo_cursor.close();
								db.close();
							} catch (Exception e) {
								Log.d("error", e.toString());
							}
							ItemDetails it = new ItemDetails();
							it.setImage(image);
							fav_dialog = new Dialog(
									HeaderFooterActivity.this,
									android.R.style.Theme_Translucent_NoTitleBar);
							fav_dialog
									.setContentView(R.layout.custom_dialog_drink_layout);
							TextView title = (TextView) fav_dialog
									.findViewById(R.id.Dialog_title);
							title.setText(name + " | Rs :" + price);

							TextView dialog_txt = (TextView) fav_dialog
									.findViewById(R.id.food_content);
							dialog_txt.setText(descrp);
							dialog_txt.setTextColor(Color.BLACK);

							ImageView dialog_img = (ImageView) fav_dialog
									.findViewById(R.id.sel_item_img);
							dialog_img.setImageBitmap(it.getImage());

							Button add_btn = (Button) fav_dialog
									.findViewById(R.id.dialog_add_btn);
							add_btn.setOnClickListener(new View.OnClickListener() {

								private AlertDialog alert;

								@Override
								public void onClick(View v) {

									DBConnection dbcon = new DBConnection(
											getApplicationContext());
									db = dbcon.getConnection();
									final String[] items = { "More Spicy",
											"Less Spicy", "exclude onion",
											"less salt", "More Sugar",
											"Less Sugar", "None" };
									final boolean[] selections = new boolean[items.length];
									AlertDialog.Builder builder = new AlertDialog.Builder(
											HeaderFooterActivity.this);
									builder.setTitle("Pick your Choice");
									builder.setMultiChoiceItems(
											items,
											selections,
											new DialogInterface.OnMultiChoiceClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which,
														boolean isChecked) {

													specialRequest
															.add(items[which]);

												}
											});

									builder.setNegativeButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												private String total_request;

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													closeBackDialog();
													try {
														for (int i = 0; i < specialRequest
																.size(); i++) {

															total_request = total_request
																	+ specialRequest
																			.get(i)
																	+ "+";

														}
														db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");

														db.execSQL("insert into order_tbl(order_id,item_id,special_request,quantity) values(100,'"
																+ item_id
																+ "','"
																+ total_request
																+ "',1)");
														db.close();

														Toast.makeText(
																getApplicationContext(),
																name + " Added",
																Toast.LENGTH_SHORT)
																.show();

														alert.dismiss();

													} catch (Exception e) {

														Toast.makeText(
																getApplicationContext(),
																"Item already Added",
																Toast.LENGTH_SHORT)
																.show();
													}
												}
											});

									alert = builder.create();
									alert.show();

								}

							});

							Button dialog_btn = (Button) fav_dialog
									.findViewById(R.id.dialog_cancel_btn);
							dialog_btn
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {
											fav_dialog.dismiss();
										}
									});

							fav_dialog.show();
						}

						// ---------
					});

					Button dialog_btn = (Button) dialog
							.findViewById(R.id.fav_dialog_cancel_btn);
					dialog_btn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}

			public void closeBackDialog() {
				fav_dialog.dismiss();

			}

			public void getFoodItems() {
				db = openOrCreateDatabase("restaurant", MODE_WORLD_READABLE,
						null);

				food_items = db.rawQuery(
						"select * from favourite_tbl where user_id='"
								+ login_cur.getString(2)
								+ "'  union select * from temp_favourite_tbl",
						null);
				food_items.moveToPosition(0);

			}

			public ArrayList<ItemDetails> GetSearchResults() {
				ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();

				try {
					if (food_items != null) {

						do {
							fav_item = db.rawQuery(
									"select * from item where item_code='"
											+ food_items.getString(0) + "'",
									null);
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
						food_items.close();

					}
				} catch (Exception e) {
					Log.d("Coooooool Errorrrrrrrrrrrr", e.toString());
				}

				return results;

			}

		});
		db.close();
	}

	public void restartFirstActivity() {
		Intent intent = getIntent();
		startActivity(intent);
	}

	public void footerNavigation() {

		db = (new DBConnection(getApplicationContext())).getConnection();
		home = (Button) findViewById(R.id.bottom_first_tab);
		home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				checkpopup1 = new AlertDialog.Builder(HeaderFooterActivity.this)
						.setTitle("CheckOut Alert !")
						.setMessage("Are you sure want to Checkout...")
						.setPositiveButton("Checkout",
								new AlertDialog.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										Cursor order_cursor = db
												.rawQuery(
														"select * from order_tbl",
														null);
										order_cursor.moveToFirst();
										Log.d("order_cursor",
												"" + order_cursor.getCount());
										if (order_cursor.getCount() != 0) {

											checkpopup = new AlertDialog.Builder(
													HeaderFooterActivity.this)
													.setTitle(
															"CheckOut Alert !")
													.setMessage(
															"You have items added in your cart, Would you like to order...")
													.setPositiveButton(
															"Ok",
															new AlertDialog.OnClickListener() {

																public void onClick(
																		DialogInterface dialog,
																		int which) {

																	Intent intent5 = new Intent(
																			getBaseContext(),
																			MyOrderList.class);
																	intent5.putExtra(
																			"table_name",
																			"order_tbl");

																	startActivity(intent5);
																	overridePendingTransition(
																			R.anim.slide_in_up,
																			R.anim.slide_out_up);
																}
															})
													.setNegativeButton(
															"Checkout",
															new AlertDialog.OnClickListener() {

																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	deletenCreateTbls();
																	Intent intent = new Intent(
																			HeaderFooterActivity.this,
																			GridViewMenu.class);
																	startActivity(intent);
																	overridePendingTransition(
																			R.anim.slide_in_up,
																			R.anim.slide_out_up);
																}

															})
													.setNeutralButton(
															"Dismiss",
															new AlertDialog.OnClickListener() {

																public void onClick(
																		DialogInterface dialog,
																		int which) {

																	checkpopup
																			.dismiss();
																}
															})

													.create();
											checkpopup.show();

										} else {
											deletenCreateTbls();
											Intent intent = new Intent(
													HeaderFooterActivity.this,
													GridViewMenu.class);
											startActivity(intent);
											overridePendingTransition(
													R.anim.slide_in_up,
													R.anim.slide_out_up);

										}
									}
								})
						.setNegativeButton("Dismiss",
								new AlertDialog.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										checkpopup1.dismiss();
									}
								})

						.create();
				checkpopup1.show();
			}

			public void deletenCreateTbls() {
				db.execSQL("DROP TABLE IF EXISTS login_tbl");
				db.execSQL("DROP TABLE IF EXISTS Checkout_order_tbl");
				db.execSQL("DROP TABLE IF EXISTS temp_favourite_tbl");
				db.execSQL("DROP TABLE IF EXISTS order_tbl");
				db.execSQL("create table if not exists login_tbl(login_txt varchar2,username varchar2,user_id varchar2)");
				db.execSQL("insert into login_tbl(login_txt,username,user_id) values('Login','','null')");
				db.execSQL("create table if not exists temp_favourite_tbl(item_id varchar2,user_id varchar2,primary key(item_id))");
				db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");
				db.execSQL("create table if not exists Checkout_order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER)");
			}
		});

		// Food Navigation--------------

		food = (Button) findViewById(R.id.bottom_third_tab);
		food.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(HeaderFooterActivity.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Intent intent1 = new Intent(getBaseContext(),
									FoodCategoryTabs.class);
							Bundle extras = new Bundle();
							extras.putString("category_id", "food_category");
							extras.putString("category_name",
									"food_category_id");
							intent1.putExtras(extras);
							startActivity(intent1);
							overridePendingTransition(R.anim.slide_in_up,
									R.anim.slide_out_up);

						} catch (Exception e) {
						}
						pdialog.dismiss();
					}
				}.start();
			}
		});

		// Special Navigation--------------

		special = (Button) findViewById(R.id.bottom_fourth_tab);
		special.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(HeaderFooterActivity.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Intent intent2 = new Intent(getBaseContext(),
									FoodCategoryTabs.class);
							Bundle extras = new Bundle();
							extras.putString("category_id", "Special_category");
							extras.putString("category_name",
									"special_category_id");
							intent2.putExtras(extras);
							startActivity(intent2);
							overridePendingTransition(R.anim.slide_in_up,
									R.anim.slide_out_up);

						} catch (Exception e) {
						}
						pdialog.dismiss();
					}
				}.start();
			}
		});

		// Beverage Navigation--------------

		beverage = (Button) findViewById(R.id.bottom_fifth_tab);
		beverage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(HeaderFooterActivity.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Intent intent3 = new Intent(getBaseContext(),
									FoodCategoryTabs.class);
							Bundle extras = new Bundle();
							extras.putString("category_id", "beverage_category");
							extras.putString("category_name",
									"beverage_category_id");
							intent3.putExtras(extras);
							startActivity(intent3);
							overridePendingTransition(R.anim.slide_in_up,
									R.anim.slide_out_up);
						} catch (Exception e) {
						}
						pdialog.dismiss();
					}
				}.start();
			}
		});

		// wine Navigation--------------

		wine = (Button) findViewById(R.id.bottom_sixth_tab);
		wine.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(HeaderFooterActivity.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Intent intent4 = new Intent(getBaseContext(),
									ShelfViewActivity.class);
							intent4.putExtra("menu_name", "Drink");
							startActivity(intent4);
							overridePendingTransition(R.anim.slide_in_up,
									R.anim.slide_out_up);

						} catch (Exception e) {
						}
						pdialog.dismiss();
					}
				}.start();
			}
		});

		// My Order Navigation------
		myorder = (Button) findViewById(R.id.bottom_order_tab);
		myorder.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(HeaderFooterActivity.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Intent intent5 = new Intent(getBaseContext(),
									MyOrderList.class);
							intent5.putExtra("table_name", "order_tbl");

							startActivity(intent5);
							overridePendingTransition(R.anim.slide_in_up,
									R.anim.slide_out_up);

						} catch (Exception e) {
						}
						pdialog.dismiss();
					}
				}.start();
			}
		});

	}

	public class SampleRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			try {
				// process the response here: executed in background thread
				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				final String name = json.getString("first_name");
				final String email = json.getString("email");

				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."
				HeaderFooterActivity.this.runOnUiThread(new Runnable() {
					public void run() {

						Log.d("name", name);

						db = (new DBConnection(getApplicationContext()))
								.getConnection();
						ContentValues cv = new ContentValues();
						cv.put("login_txt", "Logout");
						cv.put("username", "Hi! " + name);
						cv.put("user_id", email);
						db.update("login_tbl", cv, null, null);
						username.setText("Hi! " + name);

					}
				});
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}

	}

}
