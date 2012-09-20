package com.restaurantapp.pkg;

import java.io.File;
import java.io.ObjectOutputStream.PutField;
import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

	private SQLiteDatabase db;
	private Cursor cursor;
	private ProgressDialog pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.start);
		final Button btn = (Button) findViewById(R.id.startbtn);
		final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.GONE);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				pdialog = ProgressDialog.show(MainActivity.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {

							// try {
							//
							// DBConnection dbConnection = new DBConnection(
							// getApplicationContext());
							//
							// db = dbConnection.getConnection();
							// db.execSQL("DROP TABLE IF EXISTS login_tbl");
							// db.execSQL("DROP TABLE IF EXISTS Checkout_order_tbl");
							// db.execSQL("DROP TABLE IF EXISTS temp_favourite_tbl");
							// db.execSQL("DROP TABLE IF EXISTS order_tbl");
							// db.execSQL("create table if not exists login_tbl(login_txt varchar2,username varchar2,user_id varchar2)");
							// db.execSQL("insert into login_tbl(login_txt,username,user_id) values('Login','','null')");
							// db.execSQL("create table if not exists temp_favourite_tbl(item_id varchar2,user_id varchar2,primary key(item_id))");
							// db.execSQL("create table if not exists order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER,primary key(item_id))");
							// db.execSQL("create table if not exists Checkout_order_tbl(order_id VARCHAR(50) NOT NULL,item_id varchar2,special_request varchar2,quantity INTEGER)");
							// dbConnection.createSocket();
							// dbConnection.dataOutputStream.writeUTF("1");
							//
							// int totalRow =
							// dbConnection.dataInputStream.read();
							//
							// String column_name;
							// String dataType;
							// String length;
							//
							// String columnDescription;
							//
							// for (int i = 0; i < totalRow; i++) {
							//
							// String table_name = dbConnection.dataInputStream
							// .readUTF();
							// db.execSQL("DROP TABLE IF EXISTS " + table_name);
							//
							// int table_row =
							// dbConnection.dataInputStream.read();
							// Vector<Object> columnList = new Vector<Object>();
							// Vector<String> columnNames = new
							// Vector<String>();
							// for (int j = 0; j < table_row; j++) {
							// column_name = dbConnection.dataInputStream
							// .readUTF();
							// dataType =
							// dbConnection.dataInputStream.readUTF();
							// length = dbConnection.dataInputStream.readUTF();
							// columnDescription = column_name + " " + dataType;
							// columnList.add(columnDescription);
							// columnNames.add(column_name);
							// }
							// int totalElement = columnList.size();
							//
							// String sqlQuery = null;
							// sqlQuery = "CREATE TABLE IF NOT EXISTS " +
							// table_name
							// + " (";
							// for (int k = 1; k < totalElement; k++) {
							// sqlQuery = sqlQuery + columnList.elementAt(k - 1)
							// + ",";
							// }
							// sqlQuery = sqlQuery
							// + columnList.elementAt(totalElement - 1);
							// sqlQuery = sqlQuery + ");";
							// // textIn.setText(sqlQuery);
							// Log.d("Query", sqlQuery);
							//
							// db.execSQL(sqlQuery);
							// int rowInTable =
							// dbConnection.dataInputStream.read();
							// for (int r = 0; r < rowInTable; r++) {
							// try {
							// ContentValues cv = new ContentValues();
							// String sqlQuery2 = "INSERT INTO " + table_name
							// + " values(";
							// int s;
							// for (s = 0; s < totalElement - 1; s++) {
							// String aa = dbConnection.dataInputStream
							// .readUTF();
							// if (aa.equals("blob")) {
							//
							// byte[] b = Base64
							// .decode(dbConnection.dataInputStream
							// .readUTF());
							// cv.put(columnNames.elementAt(s), b);
							// // sqlQuery2 = sqlQuery2 + "'" + b +
							// // "',";
							// } else {
							//
							// // sqlQuery2 = sqlQuery2 + "'" + aa +
							// // "',";
							//
							// cv.put(columnNames.elementAt(s), aa);
							//
							// }
							// }
							// String bb = dbConnection.dataInputStream
							// .readUTF();
							// if (bb.equals("blob")) {
							//
							// byte[] b1 = Base64
							// .decode(dbConnection.dataInputStream
							// .readUTF());
							// // sqlQuery2 = sqlQuery2 + "'" + b1;
							// cv.put(columnNames.elementAt(s), b1);
							// } else {
							//
							// // sqlQuery2 = sqlQuery2 + "'" + bb; //
							// // cv.put(columnNames.elementAt(s),
							// // aa);
							// cv.put(columnNames.elementAt(s), bb);
							// }
							// db.insert(table_name, null, cv);
							// // sqlQuery2 = sqlQuery2 + "');";
							// // db.execSQL(sqlQuery2);
							// Log.d("Query", sqlQuery2);
							//
							// } catch (Exception e) {
							// Log.d("Error in socket", "" + e);
							// }
							//
							// }
							// }
							// db.close();
							//
							// // progress.setVisibility(View.GONE);
							// } catch (Exception e) {
							// e.printStackTrace();
							// Log.d("Connection Error", e.toString());
							//
							// }

							Intent intent = new Intent(MainActivity.this,
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
	}

}
