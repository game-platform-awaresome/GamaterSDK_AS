package com.tony.floatmenu;

import org.json.JSONException;
import org.json.JSONObject;

import com.gamater.common.Config;
import com.gamater.define.SPUtil;
import com.gamater.util.AppUtil;
import com.gamater.util.DensityUtil;
import com.gamater.util.ResourceUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class FloatIcon extends FrameLayout {
	private static final int FADE_OUT_TIME = 3000;
	private static final int ICON_ALPHE = 125;

	private SDKMenuManager mMenuManager;

	private AbsoluteLayout.LayoutParams mParams;
	private int mScreenWidth;
	private int mViewWidth;

	private boolean isLeft = true;

	private boolean isAlignBorder = true;

	private boolean isMenuShowing = false;

	private Point lastPoint;

	private TextView mNoteNum;
	private int mNoteCount = 0;

	private boolean isTouchable = true;

	private boolean isRedPoint;

	public boolean isTouchable() {
		return isTouchable;
	}

	public void setTouchable(boolean i) {
		isTouchable = i;
	}

	public boolean isLeft() {
		return this.isLeft;
	}

	public boolean isMenuShowing() {
		return this.isMenuShowing;
	}

	public void setMenuShowing(boolean isShowing) {
		this.isMenuShowing = isShowing;
		if (Config.isGmLogo) {
			if (isShowing) {
				mIconImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_hema_hide_float_icon"));
			} else {
				mIconImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_hema_float_icon"));
			}
		}
	}

	public Point getLastPosition() {
		return this.lastPoint;
	}

	public void resetPosition() {
		mParams.x = lastPoint.x;
		mParams.y = lastPoint.y;
		checkParams();
		mMenuManager.setViewPosition(this, mParams.x, mParams.y);
		SPUtil.setFloatMenuOrigin(getContext(), mParams.x, mParams.y);
	}

	private ImageView mIconImage;

	@SuppressLint("NewApi")
	public FloatIcon(Activity activity, int menuHeight) {
		super(activity);
		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mViewWidth = menuHeight;
		mIconImage = new ImageView(activity);
		int p = DensityUtil.dip2px(activity, 2);
		// int p = 0;
		if (Config.isOkgameLogo) {
			try {
				mIconImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_okgame_float_icon"));
			} catch (Exception e) {
			}
		} else if (Config.isGmLogo) {
			p = DensityUtil.dip2px(activity, 2);
			mIconImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_hema_float_icon"));
		} else {
			mIconImage.setImageResource(ResourceUtil.getDrawableId("vsgm_tony_float_icon"));
		}
		mIconImage.setPadding(p, p, p, p);
		addView(mIconImage, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mNoteNum = new TextView(activity);
		mNoteNum.setBackgroundResource(ResourceUtil.getDrawableId("vsgm_tony_float_num_bg"));
		mNoteNum.setTextColor(Color.WHITE);
		mNoteNum.setGravity(Gravity.CENTER);
		mNoteNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		mNoteNum.setPadding(0, 0, DensityUtil.dip2px(activity, 3), DensityUtil.dip2px(activity, 4));
		int numSize = DensityUtil.dip2px(activity, 20);
		LayoutParams params = new LayoutParams(numSize, numSize);
		params.rightMargin = -DensityUtil.dip2px(activity, 3);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mNoteNum, params);
		setNoteNumVisible(false);

		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mMenuManager = SDKMenuManager.getInstance(activity);
	}

	public void setRedPoint(boolean i) {
		isRedPoint = i;
	}

	public void setNoteNum(int num) {
		mNoteCount = num;
		if (mNoteNum != null) {
			if (num > 0) {
				mNoteNum.setText(num + "");
				if (!isMenuShowing) {
					resetViewAlpha();
					mNoteNum.setVisibility(View.VISIBLE);
					startViewAlpha();
				}
			} else if (isRedPoint) {
				mNoteNum.setText("");
				if (!isMenuShowing)
					mNoteNum.setVisibility(View.VISIBLE);
			} else {
				mNoteNum.setText("");
				mNoteNum.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void setNoteNumVisible(boolean visible) {
		if (mNoteNum != null) {
			mNoteNum.setVisibility(visible && (mNoteCount > 0 || isRedPoint) ? View.VISIBLE : View.INVISIBLE);
		}
	}

	private int getScreenHeight() {
		return SDKMenuManager.getInstance(null).getHeight();
	}

	public void checkParams() {
		if (mParams.x < 0) {
			mParams.x = 0;
		}
		if (mParams.x > mScreenWidth - mViewWidth) {
			mParams.x = mScreenWidth - mViewWidth;
		}
		if (getScreenHeight() > 0 && mParams.y > getScreenHeight() - mViewWidth) {
			mParams.y = getScreenHeight() - mViewWidth;
		}
		int i = AppUtil.dip2px(getContext(), 24);
		if (mParams.x < i) {
			mParams.x = 0;
			isAlignBorder = true;
		} else if ((mScreenWidth - mViewWidth - mParams.x) < i) {
			mParams.x = mScreenWidth - mViewWidth;
			isAlignBorder = true;
		} else {
			isAlignBorder = false;
		}
		if (mParams.x < mScreenWidth / 2) {
			isLeft = true;
		} else {
			isLeft = false;
		}
	}

	public void setParams(AbsoluteLayout.LayoutParams params) {
		mParams = params;
		checkParams();
	}

	public AbsoluteLayout.LayoutParams getParams() {
		return mParams;
	}

	private int firstX, firstY, lastX, lastY;
	private int deltaX, deltaY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isTouchable) {
			return false;
		}
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastPoint = new Point(mParams.x, mParams.y);
			resetViewAlpha();
			firstX = x;
			firstY = y;
			lastX = firstX;
			lastY = firstY;
			deltaX = x - getLeft();
			deltaY = y - getTop();
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) event.getRawX() - lastX;
			int dy = (int) event.getRawY() - lastY;
			int ax = (int) event.getRawX() - firstX;
			int ay = (int) event.getRawY() - firstY;
			if (Math.abs(ax) < 10 && Math.abs(ay) < 10)
				break;
			isMoveing = true;
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			mParams.x = x - deltaX;
			mParams.y = y - deltaY;
			mMenuManager.setViewPosition(this, mParams.x, mParams.y);
			mMenuManager.updateIconView();
			mMenuManager.updateDeteleView();
			break;
		case MotionEvent.ACTION_UP:
			isMoveing = false;
			checkParams();
			if (Math.abs(firstX - lastX) == 0 && Math.abs(firstY - lastY) == 0) {
				mMenuManager.updateMenuView();
				mMenuManager.hideDeleteView();
				JSONObject data = new JSONObject();
				try {
					data.put("id", 0);
				} catch (JSONException e) {
				}
				return true;
			} else {
				SPUtil.setFloatMenuOrigin(getContext(), mParams.x, mParams.y);
				mParams.y = getTop();
				mMenuManager.setViewPosition(this, mParams.x, mParams.y);
				mMenuManager.updateIconView();
				if (!mMenuManager.hideDeleteView()) {
					startViewAlpha();
				}
			}
			break;
		}
		return true;
	}

	private boolean isMoveing;

	private Runnable fadeOut = new Runnable() {
		@Override
		public void run() {
			if (!isMenuShowing && !isMoveing)
				alphaAnim();
		}
	};

	private void setViewAlpha(int alpha) {
		mIconImage.setAlpha(alpha);
		mNoteNum.getBackground().setAlpha(alpha);
		mNoteNum.setTextColor(Color.argb(alpha, 255, 255, 255));
	}

	private void alphaAnim() {
		AlphaAnimation a = new AlphaAnimation(1f, ICON_ALPHE / 255f);
		a.setDuration(300);
		a.setFillAfter(true);
		startAnimation(a);
	}

	public void removeAlphaCallback() {
		removeCallbacks(rotate);
		removeCallbacks(fadeOut);
	}

	public void resetViewAlpha() {
		removeAlphaCallback();
		clearAnimation();
		setViewAlpha(255);
	}

	public void startViewAlpha() {
		resetViewAlpha();
		if (isAlignBorder) {
			postDelayed(rotate, ROTATE_OUT_TIME);
		} else {
			postDelayed(fadeOut, FADE_OUT_TIME);
		}
	}

	private static final int ROTATE_OUT_TIME = 1500;

	private Runnable rotate = new Runnable() {
		@Override
		public void run() {
			if (!isMenuShowing && !isMoveing)
				rotateAnim();
		}
	};

	private void rotateAnim() {
		AnimationSet as = new AnimationSet(true);
		float rotate = isLeft ? 45 : -45;
		RotateAnimation a = new RotateAnimation(0, rotate, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		a.setDuration(300);
		as.addAnimation(a);
		float tx = isLeft ? -0.5f : 0.5f;
		TranslateAnimation a1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, tx,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
		a1.setDuration(300);
		AlphaAnimation a2 = new AlphaAnimation(1f, ICON_ALPHE / 255f);
		a2.setStartOffset(ROTATE_OUT_TIME / 2);
		a2.setDuration(300);
		// a2.setFillAfter(true);
		as.addAnimation(a2);
		as.addAnimation(a1);
		as.setFillAfter(true);
		// as.setAnimationListener(new AnimationListener() {
		// @Override
		// public void onAnimationStart(Animation animation) {
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// // postDelayed(fadeOut, ROTATE_OUT_TIME / 2);
		// }
		// });
		startAnimation(as);
	}
}
