package com.restaurantapp.pkg;

import android.R.style;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

@SuppressWarnings("deprecation")
public class FoodCategoryTabs extends HeaderFooterActivity {

	private Cursor category;

	public SQLiteDatabase db;

	private TabHost tabhost;

	private String category_names;

	private String category_names_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_category_layout);
		headerNavigation();
		footerNavigation();
		Bundle extra = getIntent().getExtras();
		category_names = extra.getString("category_id");
		category_names_id = extra.getString("category_name");
		Log.d("categroy name", category_names_id);
		LocalActivityManager lm = new LocalActivityManager(this, false);

		tabhost = (TabHost) findViewById(R.id.tabhost);
		lm.dispatchCreate(savedInstanceState);
		tabhost.setup(lm);
		TabWidget tw = tabhost.getTabWidget();
		tw.setOrientation(LinearLayout.HORIZONTAL);
		SQLiteDatabase db = openOrCreateDatabase("restaurant",
				MODE_WORLD_READABLE, null);
		category = db.rawQuery("select category_id,category_name from '"
				+ category_names + "'", null);
		category.moveToFirst();
		db.close();

		if (category != null) {
			do {

				TabSpec indspec = tabhost.newTabSpec(category.getString(1));

				indspec.setIndicator(category.getString(1));
				Intent IndianIntent = new Intent(this,
						ListViewImagesActivity.class);
				IndianIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle tab_extra = getIntent().getExtras();
				tab_extra.putString("categoryId", category.getString(0));
				tab_extra.putString("main_categoryId", category_names_id);
				IndianIntent.putExtras(tab_extra);
				indspec.setContent(IndianIntent);
				tabhost.addTab(indspec);
				tabhost.setCurrentTab(0);

			} while (category.moveToNext());
		}

		final int tabChildrenCount = tw.getChildCount();
		tabhost.getTabWidget().setPadding(10, 10, 24, 0);
		for (int i = 0; i < tabChildrenCount; i++) {
			// change the value 110 to whatever suits you for tab width
			tabhost.getTabWidget().getChildAt(i).getLayoutParams().width = 270;
			tabhost.getTabWidget().getChildAt(i).getLayoutParams().height = 50;

			tabhost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.mytab_roundcorners);

			final TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			tv.setTextColor(this.getResources().getColorStateList(
					R.color.tab_text));
			tv.setTextSize(16);
			tabhost.getTabWidget().getChildAt(0)
					.setBackgroundResource(R.drawable.bg_list_item_pressed);

		}

		tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				for (int i = 0; i < tabChildrenCount; i++) {
					// change the value 110 to whatever suits you for tab
					// width
					tabhost.getTabWidget().getChildAt(i).getLayoutParams().width = 270;
					tabhost.getTabWidget().getChildAt(i).getLayoutParams().height = 50;
					tabhost.getTabWidget()
							.getChildAt(i)
							.setBackgroundResource(
									R.drawable.mytab_roundcorners);

				}
				tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
						.setBackgroundResource(R.drawable.bg_list_item_pressed);

			}
		});

	}

}
