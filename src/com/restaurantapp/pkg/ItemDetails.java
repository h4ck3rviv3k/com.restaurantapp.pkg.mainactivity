package com.restaurantapp.pkg;

import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ItemDetails {

	private int count;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String string) {
		this.price = string;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		System.gc();
		if (this.image != null) {
			this.image.recycle();
			this.image = null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inTempStorage = new byte[16 * 1024];
		options.inPurgeable = true;
		// options.inSampleSize = 3;
		this.image = BitmapFactory.decodeByteArray(image, 0, image.length,
				options);
		// Bitmap scaledBitmap = Bitmap.createScaledBitmap(this.image, 300, 300,
		// true);
		// System.gc();
		//
		// if (this.image != null) {
		// this.image.recycle();
		// this.image = null;
		// }
		// this.image = scaledBitmap;

	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getItem_id() {
		return item_id;
	}

	public int getcount() {
		return count;
	}

	public void setcount(int count) {
		this.count = count;
	}

	public void setSpecial_request(String special_request) {
		this.special_request = special_request;
	}

	public String getSpecial_request() {
		return special_request;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	private String item_id;
	public int flag = 0;
	private String special_request;
	private String name;
	private String itemDescription;
	private String price;
	private Bitmap image;

}
