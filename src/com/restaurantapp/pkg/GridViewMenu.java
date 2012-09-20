package com.restaurantapp.pkg;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AnalogClock;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GridViewMenu extends HeaderFooterActivity {

	public Button home;
	public Button food;
	public Button special;
	public Button beverage;
	public Button wine;
	public Button myorder;
	public ProgressBar progress;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		ImageView ag = (ImageView) findViewById(R.id.clock_show);
		RotateAnimation anim = new RotateAnimation(0f, 360f, 0f, 0f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(3);
		anim.setDuration(1200);
		// ag.setAnimation(anim);

		Gallery gallery = (Gallery) findViewById(R.id.gallery_img);
		gallery.setAdapter(new ImageAdapter(this));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(GridViewMenu.this, "" + position,
						Toast.LENGTH_SHORT).show();
			}
		});
		ag.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(GridViewMenu.this, "",
						"Loading. Please wait...", true);
				new Thread() {
					public void run() {
						try {
							Thread.sleep(3000);
						} catch (Exception e) {
						}
						dialog.dismiss();
					}
				}.start();
				Intent intent = new Intent(GridViewMenu.this,
						FoodCategoryTabs.class);
				Bundle extras = new Bundle();
				extras.putString("category_id", "food_category");
				extras.putString("category_name", "food_category_id");
				intent.putExtras(extras);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_up,
						R.anim.slide_out_up);

			}
		});
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private Integer[] mImageIds = { R.drawable.paneer, R.drawable.paneer,
				R.drawable.icon, R.drawable.icon, R.drawable.cookie,
				R.drawable.paneer, R.drawable.paneer, R.drawable.paneer,
				R.drawable.paneer, R.drawable.paneer, R.drawable.paneer,
				R.drawable.paneer, R.drawable.paneer, R.drawable.paneer,
				R.drawable.paneer, R.drawable.paneer, R.drawable.paneer,
				R.drawable.paneer };

		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.Theme);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Theme_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			i.setImageResource(mImageIds[position]);
			i.setLayoutParams(new Gallery.LayoutParams(300, 250));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}
	}

}
