package com.restaurantapp.pkg;

import android.graphics.Bitmap;

public class BookItem {

	private String author;
	private String title;
	private Bitmap image;

	public BookItem() {
		// TODO Auto-generated constructor stub
	}

	public BookItem(String _author, String _title) {
		this.author = _author;
		this.title = _title;
		// this.image = _image;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String _author) {
		this.author = _author;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String _title) {
		this.title = _title;
	}

	public Bitmap getImage() {
		return this.image;
	}

	public void setImage(Bitmap _image) {
		this.image = _image;
	}

	public static final BookItem[] ALL_BOOKS = { new BookItem("Beer", "D005"),
			new BookItem("Wine", "D001"), new BookItem("Cocktails", "D003"),
			new BookItem("mocktails", "D004"), new BookItem("Brandy", "D002") };
}