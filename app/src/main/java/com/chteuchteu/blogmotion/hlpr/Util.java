package com.chteuchteu.blogmotion.hlpr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chteuchteu.blogmotion.R;

public class Util {
	public interface ProgressListener {
		void onPreExecute();
		void onProgress(int progress, int total);
		void onPostExecute();
	}

	public static String getPref(Context context, String key) {
		return context.getSharedPreferences("user_pref", Context.MODE_PRIVATE).getString(key, "");
	}

	public static void setPref(Context context, String key, String value) {
		if (value.equals(""))
			removePref(context, key);
		else {
			SharedPreferences prefs = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(key, value);
			editor.apply();
		}
	}

	public static void removePref(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(key);
		editor.apply();
	}

	public static boolean hasPref(Context context, String key) {
		return context.getSharedPreferences("user_pref", Context.MODE_PRIVATE).contains(key);
	}

	public static int getStatusBarHeight(Context c) {
		int result = 0;
		int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0)
			result = c.getResources().getDimensionPixelSize(resourceId);
		return result;
	}

	public static boolean isStatusBarTransparencyAvailable(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int id = context.getResources().getIdentifier("config_enableTranslucentDecor", "bool", "android");
			return id != 0 && context.getResources().getBoolean(id);
		}
		return false;
	}

	/**
	 * Prepares a Gmail-style progressbar on the actionBar
	 * Should be call in onCreate
	 * @param activity Activity
	 */
	public static ProgressBar prepareGmailStyleProgressBar(final Activity activity, final ActionBar actionBar) {
		// create new ProgressBar and style it
		final ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 24));
		progressBar.setProgress(0);
		progressBar.setVisibility(View.GONE);

		// retrieve the top view of our application
		final FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
		decorView.addView(progressBar);

		// Here we try to position the ProgressBar to the correct position by looking
		// at the position where content area starts. But during creating time, sizes
		// of the components are not set yet, so we have to wait until the components
		// has been laid out
		// Also note that doing progressBar.setY(136) will not work, because of different
		// screen densities and different sizes of actionBar
		ViewTreeObserver observer = progressBar.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				View contentView = decorView.findViewById(android.R.id.content);
				int actionBarHeight = actionBar != null ? actionBar.getHeight() : Util.getActionBarHeight(activity);
				int y = Util.getStatusBarHeight(activity) + actionBarHeight;

				progressBar.setY(y + contentView.getY() - 10);
				progressBar.setProgressDrawable(activity.getResources().getDrawable(
						R.drawable.progress_horizontal_holo_no_background_light));

				ViewTreeObserver observer = progressBar.getViewTreeObserver();
				observer.removeGlobalOnLayoutListener(this);
			}
		});

		return progressBar;
	}

	public static int getActionBarHeight(Context c) {
		final TypedArray styledAttributes = c.getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.actionBarSize });
		int height = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();
		return height;
	}

	public static final class Fonts {
		/* ENUM Custom Fonts */
		public enum CustomFont {
			Roboto_Medium("Roboto-Medium.ttf");

			final String file;
			private CustomFont(String fileName) { this.file = fileName; }
			public String getValue() { return this.file; }
		}

		/* Fonts */
		public static void setFont(Context c, ViewGroup g, CustomFont font) {
			Typeface mFont = Typeface.createFromAsset(c.getAssets(), font.getValue());
			setFont(g, mFont);
		}

		public static void setFont(Context c, TextView t, CustomFont font) {
			Typeface mFont = Typeface.createFromAsset(c.getAssets(), font.getValue());
			t.setTypeface(mFont);
		}

		public static void setFont(Context c, Button t, CustomFont font) {
			Typeface mFont = Typeface.createFromAsset(c.getAssets(), font.getValue());
			t.setTypeface(mFont);
		}

		private static void setFont(ViewGroup group, Typeface font) {
			int count = group.getChildCount();
			View v;
			for (int i = 0; i < count; i++) {
				v = group.getChildAt(i);
				if (v instanceof TextView) {
					((TextView) v).setTypeface(font);
				} else if (v instanceof ViewGroup)
					setFont((ViewGroup) v, font);
			}
		}
	}

	public enum TransitionStyle { DEEPER, SHALLOWER }
	public static void setTransition(Context context, TransitionStyle transitionStyle) {
		switch (transitionStyle) {
			case DEEPER:
				((Activity) context).overridePendingTransition(R.anim.deeper_in, R.anim.deeper_out);
				break;
			case SHALLOWER:
				((Activity) context).overridePendingTransition(R.anim.shallower_in, R.anim.shallower_out);
				break;
		}
	}

	public static void setViewAlpha(View view, float alpha) {
		AlphaAnimation alphaAnim = new AlphaAnimation(alpha, alpha);
		alphaAnim.setDuration(0);
		alphaAnim.setFillAfter(true);
		view.startAnimation(alphaAnim);
	}
}
