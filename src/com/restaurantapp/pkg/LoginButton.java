/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.restaurantapp.pkg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.restaurantapp.pkg.SessionEvents.AuthListener;
import com.restaurantapp.pkg.SessionEvents.LogoutListener;

public class LoginButton extends ImageButton {

	private Facebook mFb;
	private Context context;
	private Handler mHandler;
	private SessionListener mSessionListener = new SessionListener();
	private String[] mPermissions;
	private Activity mActivity;
	private Context mcontext;

	public LoginButton(Context context) {
		super(context);
	}

	public LoginButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LoginButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(final Activity activity, final Facebook fb) {

		init(activity, fb, new String[] {});

	}

	public void init(final Activity activity, final Facebook fb,
			final String[] permissions) {
		try {
			mActivity = activity;
			mFb = fb;
			mPermissions = permissions;
			mHandler = new Handler();

			setBackgroundColor(Color.TRANSPARENT);
			setAdjustViewBounds(true);
			if (fb.isSessionValid()) {
				setImageResource(R.drawable.logout_button);

			} else {
				setImageResource(R.drawable.login_button);

			}

			drawableStateChanged();

			SessionEvents.addAuthListener(mSessionListener);
			SessionEvents.addLogoutListener(mSessionListener);
			setOnClickListener(new ButtonOnClickListener());
		} catch (Exception ex) {
			Toast.makeText(getContext(), "no internet connection",
					Toast.LENGTH_SHORT).show();
		}
	}

	public final class ButtonOnClickListener implements OnClickListener {

		public void onClick(View arg0) {
			if (mFb.isSessionValid()) {
				SessionEvents.onLogoutBegin();
				AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(mFb);
				asyncRunner.logout(getContext(), new LogoutRequestListener());
			} else {
				mFb.authorize(mActivity, mPermissions,
						new LoginDialogListener());
			}

		}
	}

	private final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			SessionEvents.onLoginSuccess();
		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
		
		
	}

	public class LogoutRequestListener extends BaseRequestListener {

		public void onComplete(String response, final Object state) {

			mHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	public class SessionListener implements AuthListener, LogoutListener {

		public void onAuthSucceed() {
			setImageResource(R.drawable.logout_button);
			SessionStore.save(mFb, getContext());

		}

		public void onAuthFail(String error) {
		}

		public void onLogoutBegin() {
		}

		public void onLogoutFinish() {
			SessionStore.clear(getContext());
			setImageResource(R.drawable.login_button);

		}
	}

}
