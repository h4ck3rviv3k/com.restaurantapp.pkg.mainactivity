package com.restaurantapp.pkg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.restaurantapp.pkg.LoginButton.SessionListener;
import com.restaurantapp.pkg.SessionEvents.AuthListener;
import com.restaurantapp.pkg.SessionEvents.LogoutListener;

public class MyOrderList extends Activity {

	public class LogoutRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			SessionEvents.onLogoutFinish();
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

	}

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

	public Cursor food_items = null;
	private String item_name;
	private String item_descrp;
	private String item_price;
	public ArrayList<ItemDetails> image_details;
	public String bill_no = "";
	private String item_id;
	private Cursor order_item;
	private Button order_btn;
	public MyOrderAdapter ad;
	private Button cancel_btn;
	private SQLiteDatabase db;
	private float Checkout_Total = 0;
	private AlertDialog mydialog;
	protected String table_no;
	protected String order_no;
	public int quantity = 0;
	protected int itemid;
	protected Cursor temp_item;
	public float Total = 0;
	public ListView lv1;
	public int bill_count = 100;
	private Cursor fav_item;
	private String table_name;
	private Calendar c;
	private TextView Title_txt;
	private String device_id;
	private String print_order_items = "";
	private String col_name;

	public static String APP_ID = "346381592110362"; // Replace your App ID

	public Facebook facebook;
	public AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	public SharedPreferences mPrefs;
	private AlertDialog noItemalert;
	private Dialog checkOut;
	private Cursor mac;
	private String Main_table = "T1";
	public ProgressDialog pdialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_myorder_view);
		db = openOrCreateDatabase("restaurant", MODE_WORLD_READABLE, null);
		c = Calendar.getInstance();

		// ----initilaizee facebook objects-
		facebook = new Facebook(APP_ID);

		mAsyncRunner = new AsyncFacebookRunner(facebook);
		SessionStore.restore(facebook, this);

		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		table_name = getIntent().getStringExtra("table_name");

		lv1 = (ListView) findViewById(R.id.myorder_lv);
		Title_txt = (TextView) findViewById(R.id.myorder_total);
		Title_txt.setText("Total :" + Total);

		order_btn = (Button) findViewById(R.id.myorder_btn);
		cancel_btn = (Button) findViewById(R.id.myorder_backbtn);

		cancel_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(MyOrderList.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Intent intent = new Intent(MyOrderList.this,
									FoodCategoryTabs.class);
							Bundle extras = new Bundle();
							extras.putString("category_id", "food_category");
							extras.putString("category_name",
									"food_category_id");
							intent.putExtras(extras);
							startActivity(intent);
							overridePendingTransition(R.anim.slide_in_up,
									R.anim.slide_out_up);

						} catch (Exception e) {
						}
						pdialog.dismiss();
					}
				}.start();

			}
		});

		// ----------------on order click--------------
		order_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int flagx = 0;
				if (food_items.getCount() != 0)
					showpopup();
				else
					checkOutPopUp(flagx);

			}

		});

	}

	protected void showpopup() {
		final int flag = 1;

		mydialog = new AlertDialog.Builder(this)
				.setTitle("My Order Alert")
				.setMessage(
						"Thank you Dear Customer, Your Order will processed soon Would you like to proceed further...")
				.setPositiveButton("Ok", new AlertDialog.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						DBConnection dbConnection = new DBConnection(
								getApplicationContext());
						dbConnection.createSocket();
						try {
							ArrayList<String> specialReq = new ArrayList<String>();
							ArrayList<String> quantityList = new ArrayList<String>();
							ArrayList<String> itemNameList = new ArrayList<String>();
							String tempItemId = null;
							Cursor cursor_order = db
									.rawQuery(
											"select item.Item_name, order_tbl.special_request,order_tbl.quantity,item.item_code from item, order_tbl where order_tbl.item_id=item.item_code",
											null);
							cursor_order.moveToFirst();
							db.execSQL("create table if not exists Checkout_order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER)");
							if (cursor_order != null) {
								do {
									db.execSQL("insert into Checkout_order_tbl(order_id,item_id,special_request,quantity) values(100,'"
											+ cursor_order.getString(3)
											+ "','"
											+ cursor_order.getString(1)
											+ "',"
											+ cursor_order.getInt(2) + ")");

									itemNameList.add(cursor_order.getString(0));
									quantityList.add(cursor_order.getInt(2)
											+ "");

									specialReq.add(cursor_order.getString(1));
								} while (cursor_order.moveToNext());
								dbConnection.dataOutputStream.writeUTF("5");
								Log.d("after 5 utf", "vdgfd");
								dbConnection.dataOutputStream
										.write(itemNameList.size());
								for (int l = 0; l < itemNameList.size(); l++) {
									dbConnection.dataOutputStream
											.writeUTF(Main_table);
									dbConnection.dataOutputStream
											.writeUTF(itemNameList.get(l));
									dbConnection.dataOutputStream
											.writeUTF(specialReq.get(l));
									dbConnection.dataOutputStream
											.writeUTF(quantityList.get(l));
								}
							}
						} catch (Exception ex) {

						}
						checkOutPopUp(flag);
					}
				})
				.setNeutralButton("Discard", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mydialog.dismiss();

					}
				})

				.create();
		mydialog.show();
	}

	public void checkOutPopUp(final int flag_value) {
		checkOut = new AlertDialog.Builder(MyOrderList.this)
				.setTitle("CheckOut Alert !")
				.setMessage(
						"Would you like to continue to add items or checkout..")
				.setPositiveButton("Continue",
						new AlertDialog.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								if (flag_value != 0) {
									db.delete("order_tbl", "order_id=100", null);
								}

								pdialog = ProgressDialog.show(MyOrderList.this,
										"", "Loading. Please wait...", true);
								new Thread() {
									public void run() {
										try {
											Intent intent = new Intent(
													MyOrderList.this,
													FoodCategoryTabs.class);
											Bundle extras = new Bundle();
											extras.putString("category_id",
													"food_category");
											extras.putString("category_name",
													"food_category_id");
											intent.putExtras(extras);
											startActivity(intent);
											overridePendingTransition(
													R.anim.slide_in_up,
													R.anim.slide_out_up);

										} catch (Exception e) {
										}
										pdialog.dismiss();
									}
								}.start();

							}

						})
				.setNegativeButton("Checkout",
						new AlertDialog.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								if (flag_value != 0) {
									Toast.makeText(getApplicationContext(),
											"checkout click", Toast.LENGTH_LONG)
											.show();
									DBConnection dbConnection = new DBConnection(

									getApplicationContext());
									try {

										dbConnection.createSocket();
										dbConnection.dataOutputStream
												.writeUTF("3");
									} catch (Exception e) {

									}
									// -----------Delete Order
									// Table--------------
									db.delete(table_name, "order_id=100", null);
									// -----------Handling Checkout Order
									// Table--------------
									try {
										Cursor checkout_cursor = db
												.rawQuery(
														"Select * from Checkout_order_tbl",
														null);
										checkout_cursor.moveToFirst();
										Cursor check_order_item;

										if (checkout_cursor != null) {
											checkout_cursor.moveToFirst();
											do {
												check_order_item = db
														.rawQuery(
																"select item_price,item_name from item where item_code='"
																		+ checkout_cursor
																				.getString(1)
																		+ "'",
																null);
												check_order_item.moveToFirst();
												int check_quantity = checkout_cursor
														.getInt(3);
												Checkout_Total += check_quantity
														* check_order_item
																.getInt(0);

											} while (checkout_cursor
													.moveToNext());
											check_order_item.close();
											checkout_cursor.close();
										}

									} catch (Exception e) {
										Log.d("dialog error", e.toString());
									}
									continueFunction(dbConnection);
									// ----------Handling Favourite Table
									Cursor fav_cursor = db.rawQuery(
											"select * from temp_favourite_tbl",
											null);
									fav_cursor.moveToFirst();
									int length = fav_cursor.getCount();
									try {
										dbConnection.dataOutputStream
												.write(length);
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									if (fav_cursor != null) {
										do {
											try {
												String query = "insert into favourite_tbl values('";
												query = query
														+ fav_cursor
																.getString(0)
														+ "','"
														+ fav_cursor
																.getString(1)
														+ "')";
												dbConnection.dataOutputStream
														.writeUTF(query);
											} catch (Exception e) {

											}

										} while (fav_cursor.moveToNext());
									}
									db.execSQL("DROP TABLE IF EXISTS temp_favourite_tbl");
									db.delete("Checkout_order_tbl",
											"order_id=100", null);
									db.execSQL("create table if not exists temp_favourite_tbl(item_id varchar2,user_id varchar2,primary key(item_id))");
								}
								if (HeaderFooterActivity.facebook != null) {
									if (HeaderFooterActivity.facebook
											.isSessionValid()) {
										ContentValues cv = new ContentValues();
										cv.put("login_txt", "Login");
										cv.put("username", "");
										cv.put("user_id", "");
										db.update("login_tbl", cv, null, null);

										HeaderFooterActivity.mAsyncRunner
												.logout(getApplicationContext(),
														new LogoutRequestListener());

									}
								} else {

									Log.d("logout me exeption", "");
								}

								pdialog = ProgressDialog.show(MyOrderList.this,
										"", "Loading. Please wait...", true);
								new Thread() {
									public void run() {
										try {

											Intent intent = new Intent(
													MyOrderList.this,
													GridViewMenu.class);

											startActivity(intent);
											overridePendingTransition(
													R.anim.slide_in_up,
													R.anim.slide_out_up);
										} catch (Exception e) {
										}
										pdialog.dismiss();
									}
								}.start();

							}
						})

				.create();
		checkOut.show();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		table_name = getIntent().getStringExtra("table_name");
		getFoodItems(table_name);
		// calculateOrder();
		final ListView lv1 = (ListView) findViewById(R.id.myorder_lv);
		ad = new MyOrderAdapter(this, image_details, table_name);
		ad.notifyDataSetChanged();
		lv1.setAdapter(ad);
	}

	public void continueFunction(DBConnection dbConnection) {
		try {
			Cursor customer_order = db.rawQuery(
					"select bill_no from auto_bill", null);
			customer_order.moveToFirst();

			bill_no = Main_table + customer_order.getCount();

			dbConnection.dataOutputStream
					.writeUTF("insert into auto_bill(bill_no) values('"
							+ bill_no + "')");
			db.execSQL("insert into auto_bill(bill_no) values('" + bill_no
					+ "')");

			System.out.println("passed");

			dbConnection.dataOutputStream
					.writeUTF("insert into CUSTOMER_BILL(table_no,bill_no,status,price,day,month,year) values('"
							+ Main_table
							+ "','"
							+ bill_no
							+ "','UNPAID',"
							+ Checkout_Total
							+ ",'"
							+ c.get(Calendar.DAY_OF_MONTH)
							+ "','"
							+ (c.get(Calendar.MONTH) + 1)
							+ "','"
							+ c.get(Calendar.YEAR) + "')");
			dbConnection.dataOutputStream.writeUTF(bill_no);
			Log.d("Month", "" + c.get(Calendar.MONTH));
			ArrayList<String> specialReq = new ArrayList<String>();
			ArrayList<String> quantityList = new ArrayList<String>();
			ArrayList<String> itemNameList = new ArrayList<String>();
			ArrayList<String> itemPriceList = new ArrayList<String>();
			String tempItemId = null;
			Cursor cursor_order = db
					.rawQuery(
							"select item.Item_name, Checkout_order_tbl.special_request,Checkout_order_tbl.quantity,item.item_price from item, Checkout_order_tbl where Checkout_order_tbl.item_id=item.item_code",
							null);
			cursor_order.moveToFirst();
			Log.d("item price ", "" + cursor_order.getInt(3));
			if (cursor_order != null) {
				do {

					itemNameList.add(cursor_order.getString(0));
					quantityList.add(cursor_order.getInt(2) + "");
					itemPriceList.add(cursor_order.getInt(3) + "");
					specialReq.add(cursor_order.getString(1));
				} while (cursor_order.moveToNext());
				dbConnection.dataOutputStream.write(itemNameList.size());
				for (int l = 0; l < itemNameList.size(); l++) {
					dbConnection.dataOutputStream.writeUTF(Main_table);
					dbConnection.dataOutputStream.writeUTF(itemNameList.get(l));
					dbConnection.dataOutputStream.writeUTF(specialReq.get(l));
					dbConnection.dataOutputStream.writeUTF(quantityList.get(l));
					dbConnection.dataOutputStream
							.writeUTF(itemPriceList.get(l));
				}
			}

			dbConnection.dataOutputStream.writeFloat(Checkout_Total);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("EDC", "" + e);
		}

	}

	public void getFoodItems(String tableName) {
		try {
			food_items = db.rawQuery("select * from " + tableName, null);
			food_items.moveToFirst();
			image_details = getFavResults();
			if (food_items.getCount() != 0) {
				Log.d("count khali nahi h", "bilkl nai h");

				calculateOrder();

			} else {
				Total = 0;
				Title_txt.setText("Total :" + Total);
			}
		} catch (Exception e) {
			Total = 0;
			Title_txt.setText("Total :" + Total);
		}

	}

	public void calculateOrder() {
		Cursor order;

		Cursor customer_order;
		String bill_no;

		int count;
		Cursor favourite_cursor;

		try {
			DBConnection dbcon = new DBConnection(getApplicationContext());
			db = dbcon.getConnection();
			food_items.moveToFirst();

			col_name = food_items.getColumnName(0);
			Log.d("----------------col_name", col_name);

			if (food_items != null) {
				food_items.moveToFirst();
				Log.d("----------------col_name", "" + food_items.getCount());
				order_no = food_items.getString(0);

				do {
					order = db.rawQuery(
							"select item_price,item_name from item where item_code='"
									+ food_items.getString(1) + "'", null);
					order.moveToFirst();
					quantity = food_items.getInt(3);
					Total += quantity * order.getInt(0);
					Title_txt.setText("Total :" + Total);
					Log.d("-------to", "" + Total);
				} while (food_items.moveToNext());
				order.close();
				food_items.close();
			}

		} catch (Exception e) {
			Log.d("dialog error", e.toString());
		}

	}

	private ArrayList<ItemDetails> getFavResults() {
		ArrayList<ItemDetails> results = new ArrayList<ItemDetails>();

		try {
			if (food_items != null) {
				Log.d("order_tbl ID", "" + food_items.getString(1));
				do {
					fav_item = db.rawQuery(
							"select * from item where item_code='"
									+ food_items.getString(1) + "'", null);
					fav_item.moveToFirst();
					item_id = fav_item.getString(1);
					item_name = fav_item.getString(0);
					item_descrp = fav_item.getString(2);
					item_price = fav_item.getString(17);
					ItemDetails item_details = new ItemDetails();
					item_details.setName(item_name);
					item_details.setItem_id(item_id);
					item_details.setItemDescription(item_descrp);
					item_details.setPrice(item_price);
					item_details.setImage(fav_item.getBlob(10));
					item_details.setcount(food_items.getInt(3));
					results.add(item_details);
				} while (food_items.moveToNext());

			}
		} catch (Exception e) {
			Log.d("Coooooool Errorrrrrrrrrrrr", e.toString());
		}
		db.close();
		return results;
	}

}