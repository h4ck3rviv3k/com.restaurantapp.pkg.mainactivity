package com.restaurantapp.pkg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class DBConnection extends Activity {
	Socket socket = null;

	public DataOutputStream dataOutputStream = null;
	public DataInputStream dataInputStream = null;
	private Context mcontext;

	private SQLiteDatabase db;

	public DBConnection(Context context) {
		mcontext = context;

	}

	public void createSocket() {

		try {
			socket = new Socket("192.168.184.50", 8888);

			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {

			Intent intent = new Intent(getApplicationContext(),
					FoodCategoryTabs.class);
			startActivity(intent);
		} catch (IOException e) {
			Intent intent = new Intent(getApplicationContext(),
					FoodCategoryTabs.class);
			startActivity(intent);

		}

	}

	public SQLiteDatabase getConnection() {

		db = mcontext.openOrCreateDatabase("restaurant", MODE_WORLD_READABLE,
				null);
		try {
			db = SQLiteDatabase.openDatabase(
					"/data/data/com.restaurantapp.pkg/databases/restaurant",
					null, SQLiteDatabase.CREATE_IF_NECESSARY);
			// Toast.makeText(this, "DB was opened!", 1).show();
		} catch (SQLiteException e) {
			Toast.makeText(this, e.getMessage(), 1).show();
		}
		return db;
	}

}
