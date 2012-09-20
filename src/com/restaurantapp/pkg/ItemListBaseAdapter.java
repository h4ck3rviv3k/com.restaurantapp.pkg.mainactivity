package com.restaurantapp.pkg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class ItemListBaseAdapter extends BaseAdapter {
	public class WallPostRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			Log.d("Facebook-Example", "Got response: " + response);
			String message = "<empty>";
			try {
				JSONObject json = Util.parseJson(response);
				message = json.getString("message");
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}

		}
	}

	public class SampleDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
				HeaderFooterActivity.mAsyncRunner.request(postId,
						new WallPostRequestListener());

			} else {
				Log.d("Facebook-Example", "No wall post made");
			}
		}
	}

	Calendar c = Calendar.getInstance();
	private static ArrayList<ItemDetails> itemDetailsrrayList;

	private Integer[] imgid = { R.drawable.coffee, R.drawable.coffee,
			R.drawable.coffee, R.drawable.coffee, R.drawable.coffee,
			R.drawable.coffee };

	private LayoutInflater l_Inflater;

	private Context context;
	private AlertDialog alert;

	// private OnClickListener listner;

	public ItemListBaseAdapter(Context context, ArrayList<ItemDetails> results) {
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
						Cursor login_cur = db.rawQuery(
								"select * from login_tbl", null);
						login_cur.moveToFirst();
						db.execSQL("create table if not exists temp_favourite_tbl(item_id varchar2,user_id varchar2,primary key(item_id))");
						Log.d("user_id", "value--->>" + login_cur.getString(2));
						if (!(login_cur.getString(2)).equals("null")) {
							db.execSQL("insert into temp_favourite_tbl(item_id,user_id) values('"
									+ item_list.getItem_id()
									+ "','"
									+ login_cur.getString(2) + "')");
							db.close();
							login_cur.close();
							Toast.makeText(context,
									"You Liked " + item_list.getName(),
									Toast.LENGTH_SHORT).show();
							if (HeaderFooterActivity.facebook.isSessionValid()) {
								HeaderFooterActivity.facebook.dialog(context,
										"feed", new SampleDialogListener());

							}
						} else
							Toast.makeText(context.getApplicationContext(),
									"Login with facebook to like",
									Toast.LENGTH_SHORT).show();

					} catch (Exception e) {
						Toast.makeText(context.getApplicationContext(),
								"You already liked", Toast.LENGTH_SHORT).show();
					}

				}
			});

			holder.addBtn.setOnClickListener(new View.OnClickListener() {

				private SQLiteDatabase db;

				private ItemDetails item_list;
				List<String> specialRequest = new ArrayList<String>();
				String total_request = "";

				@Override
				public void onClick(View v) {

					item_list = itemDetailsrrayList.get((Integer) v.getTag());

					DBConnection dbcon = new DBConnection(context);
					db = dbcon.getConnection();
					final String[] items = { "More Spicy", "Less Spicy",
							"exclude onion", "less salt", "More Sugar",
							"Less Sugar", "None" };
					final boolean[] selections = new boolean[items.length];

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("Pick your Choice");
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
									try {
										for (int i = 0; i < specialRequest
												.size(); i++) {

											total_request = total_request
													+ specialRequest.get(i)
													+ "+";
										}

										// db.execSQL("DROP table order_tbl");
										// db.execSQL("DROP table if exists order_tbl");

										db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");

										db.execSQL("insert into order_tbl(order_id,item_id,special_request,quantity) values(100,'"
												+ item_list.getItem_id()
												+ "','"
												+ total_request
												+ "',1)");

										db.close();
										Toast.makeText(context,
												item_list.getName() + " Added",
												Toast.LENGTH_SHORT).show();
										alert.dismiss();
									} catch (Exception e) {
										Toast.makeText(
												context.getApplicationContext(),
												"Item already Added",
												Toast.LENGTH_SHORT).show();
									}

								}
							});

					alert = builder.create();
					alert.show();

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

		holder.likeBtn.setText("Like");
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
}
