package com.gamater.sdk.fragments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gamater.account.MobUserManager;
import com.gamater.common.Config;
import com.gamater.common.http.HttpRequest;
import com.gamater.common.http.HttpRequest.HttpEventListener;
import com.gamater.sdk.common.ConfigUtil;
import com.gamater.sdk.game.GamaterSDK;
import com.gamater.util.DensityUtil;
import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

/**
 * 
 * @author Robert lo 所有窗口的超类
 */
public class MVBaseFragment extends Fragment implements HttpEventListener {
	private View rootView;
	private View title;
	private View titleWithBack;
	private View btnBack;
	private MVLoadingFragment loadingFragment;

	private boolean isShowing;

	private boolean isLoginCloseEnable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(findLayoutId("vsgm_tony_fragment_base"),
				container, false);

		loadingFragment = new MVLoadingFragment();
		isLoginCloseEnable = ConfigUtil.getConfigEnable(ConfigUtil.LOGIN_CLOSE);

		View btnClose = rootView.findViewById(findId("btnClose"));
		title = rootView.findViewById(findId("vsgm_tony_left_logo"));
		titleWithBack = rootView.findViewById(findId("title_with_back"));
		btnBack = rootView.findViewById(findId("btnBack"));
		ImageView centerLogo = (ImageView) rootView
				.findViewById(findId("vsgm_tony_center_logo"));
		ImageView leftLogo = (ImageView) rootView
				.findViewById(findId("vsgm_tony_left_logo"));
		if (Config.isOkgameLogo) {
			int padding = DensityUtil.dip2px(getActivity(), 8);
			try {
				centerLogo
						.setImageResource(findDrawableId("vsgm_tony_logo_okgame"));
				centerLogo.setPadding(padding, padding, padding, padding);
				leftLogo.setImageResource(findDrawableId("vsgm_tony_logo_okgame"));
				leftLogo.setPadding(padding, padding, padding, padding);
			} catch (Exception e) {
			}
		} else if (Config.isGmLogo) {
			int padding = DensityUtil.dip2px(getActivity(), 8);
			try {
				centerLogo
						.setImageResource(findDrawableId("vsgm_tony_logo_gamemy"));
				centerLogo.setPadding(-padding, padding, 0, padding);
				leftLogo.setImageResource(findDrawableId("vsgm_tony_logo_gamemy"));
				leftLogo.setPadding(-padding, padding, 0, padding);
			} catch (Exception e) {
			}
		} else {
			int padding = DensityUtil.dip2px(getActivity(), 8);
			try {
				centerLogo.setImageResource(findDrawableId("vsgm_tony_logo"));
				centerLogo.setPadding(-padding, padding, 0, padding);
				leftLogo.setImageResource(findDrawableId("vsgm_tony_logo"));
				leftLogo.setPadding(-padding, padding, 0, padding);
			} catch (Exception e) {
			}
		}

		btnBack.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				onClose();
				FragmentManager mFragmentManager = getActivity()
						.getSupportFragmentManager();
				mFragmentManager.popBackStack();
			}
		});

		btnClose.setOnClickListener(new BaseOnClickListener() {

			@Override
			public void onBaseClick(View v) {
				onClose();
				MobUserManager mm = MobUserManager.getInstance();
				if (mm != null && mm.isLoginIng()) {
					mm.setIsLoginIng(false);
					GamaterSDK.getInstance().getAcGameSDKListener()
							.didCancel();
				}
				GamaterSDK.getInstance().resumeGmae(getActivity());
			}
		});
		if (!isLoginCloseEnable && MobUserManager.getInstance().isLoginIng()) {
			btnClose.setVisibility(View.GONE);
		}

		return rootView;
	}

	public boolean isShowing() {
		return this.isShowing;
	}

	private void onClose() {
		this.isShowing = false;
		hideSoftInput(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		this.isShowing = true;
	}

	@Override
	public void onStop() {
		super.onStop();
		this.isShowing = false;
	}

	public void hiddenBackBtn() {
		if (!isLoginCloseEnable && MobUserManager.getInstance().isLoginIng()) {
			btnBack.setVisibility(View.GONE);
		} else {
			title.setVisibility(View.VISIBLE);
			titleWithBack.setVisibility(View.GONE);
		}
	}

	public void showBackBtn() {
	}

	public void setContentViewByID(int layoutResID) {
		try {
			LinearLayout content = (LinearLayout) rootView
					.findViewById(findId("content"));

			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(layoutResID, null);
			content.addView(v);
		} catch (Exception e) {
			try {
				getActivity().finish();
			} catch (Exception e2) {
			}
		}
	}

	@Override
	public void requestDidSuccess(HttpRequest httpRequest, String result) {
		try {
			JSONObject obj = new JSONObject(result);
			int status = obj.getInt("status");

			if (status == 0) {
				int errorCode = obj.getInt("errorCode");
				Resources rs = getActivity().getResources();

				switch (errorCode) {
				case 100:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_100")));
					break;
				case 101:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_101")));
					break;
				case 102:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_102")));
					break;
				case 103:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_103")));
					break;
				case 104:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_104")));
					break;
				case 105:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_105")));
					break;
				case 106:
					// showErrorMsg(rs.getString(findStringId("vsgm_tony_err_106")));
					break;
				case 107:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_107")));
					break;
				case 108:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_108")));
					break;
				case 109:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_109")));
					break;
				case 211:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_211")));
					break;
				default:
					showErrorMsg(rs
							.getString(findStringId("vsgm_tony_err_unknown")));
					break;
				}
			}
		} catch (Exception e) {
		}

		try {
			FragmentManager mFragmentManager = getActivity()
					.getSupportFragmentManager();
			mFragmentManager.popBackStack();
		} catch (Exception e) {
		}
	}

	private void showErrorMsg(String msg) {
		showToast(msg);
	}

	@Override
	public void requestDidStart(HttpRequest httpRequest) {
		if (getActivity() == null) {
			return;
		}

		try {
			FragmentManager fm = getActivity().getSupportFragmentManager();

			if (!fm.getFragments().contains(loadingFragment)) {
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(findId("content"), loadingFragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void requestDidFailed(HttpRequest httpRequest) {
		if (getActivity() == null) {
			return;
		}
		try {
			FragmentManager mFragmentManager = getActivity()
					.getSupportFragmentManager();
			mFragmentManager.popBackStack();
			Resources rs = getActivity().getResources();
			showErrorMsg(rs.getString(findStringId("vsgm_tony_err_unknown")));
		} catch (Exception e) {
		}
	}

	public final void startFragment(Fragment f) {
		try {
			FragmentTransaction ft = getActivity().getSupportFragmentManager()
					.beginTransaction();
			ft.replace(findId("content"), f);
			ft.addToBackStack(null);
			ft.commit();
		} catch (Exception e) {
		}
	}

	public final void startFragment(MVBaseFragment f) {
		try {
			FragmentTransaction ft = getActivity().getSupportFragmentManager()
					.beginTransaction();
			ft.replace(findId("content"), f);
			ft.addToBackStack(null);
			ft.commit();
		} catch (Exception e) {
		}
	}

	public void startChildFragment(int id, Fragment f) {
		try {
			FragmentTransaction ft = getChildFragmentManager()
					.beginTransaction();
			ft.replace(id, f);
			ft.addToBackStack(null);
			ft.commit();
			setCanGoBack(true);
		} catch (Exception e) {
		}
	}

	public boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = ".*@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	protected void hideSoftInput(Context context) {
		try {
			InputMethodManager manager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (getActivity().getCurrentFocus() != null) {
				manager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
		}
	}

	public boolean shouldHandleEvent(MotionEvent paramMotionEvent) {
		if (isTouchEditText((ViewGroup) this.rootView, paramMotionEvent)) {
			return false;
		}
		return true;
	}

	private boolean isTouchEditText(ViewGroup paramViewGroup,
			MotionEvent paramMotionEvent) {
		try {
			for (int i = 0; i < paramViewGroup.getChildCount(); i++) {
				View localView = paramViewGroup.getChildAt(i);
				if ((localView instanceof ViewGroup)) {
					if (isTouchEditText((ViewGroup) localView, paramMotionEvent))
						return true;
				} else if ((localView instanceof EditText)) {
					int[] arrayOfInt = new int[2];
					localView.getLocationInWindow(arrayOfInt);
					int j = arrayOfInt[0];
					int k = arrayOfInt[1];
					int m = k + localView.getHeight();
					int n = j + localView.getWidth();
					if ((paramMotionEvent.getX() > j)
							&& (paramMotionEvent.getX() < n)
							&& (paramMotionEvent.getY() > k)
							&& (paramMotionEvent.getY() < m)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public void showToast(final String paramString) {
		if (getActivity() != null)
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Toast.makeText(MVBaseFragment.this.getActivity(),
								paramString, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
					}
				}
			});
	}

	public void showToast(final int paramInt) {
		if (getActivity() != null)
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Toast.makeText(MVBaseFragment.this.getActivity(),
								paramInt, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
					}
				}
			});
	}

	public void showToast(final CharSequence paramString) {
		if (getActivity() != null)
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Toast.makeText(MVBaseFragment.this.getActivity(),
								paramString, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
					}
				}
			});
	}

	protected int findLayoutId(String layoutName) {
		return ResourceUtil.getLayoutId(layoutName);
	}

	protected int findId(String name) {
		return ResourceUtil.getId(name);
	}

	protected int findStringId(String stringName) {
		return ResourceUtil.getStringId(stringName);
	}

	protected int findDrawableId(String drawableName) {
		return ResourceUtil.getDrawableId(drawableName);
	}

	private boolean canGoBack = false;

	public void setCanGoBack(boolean can) {
		canGoBack = can;
	}

	public boolean canGoBack() {
		return canGoBack;
	}

	public boolean onBackPressed() {
		if (getChildFragmentManager().getFragments() == null)
			return false;
		int size = getChildFragmentManager().getFragments().size();
		while (size > 0) {
			Fragment f = getChildFragmentManager().getFragments().get(--size);
			if (f != null && f instanceof MVBaseFragment) {
				MVBaseFragment fragment = (MVBaseFragment) f;
				if (fragment.onBackPressed()) {
					return true;
				} else {
					setCanGoBack(false);
					getChildFragmentManager().popBackStack();
				}
			} else if (f == null && size == 0) {
				return false;
			}
		}
		return true;
	}

	public void refreshData() {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (getChildFragmentManager().getFragments() == null)
			return;
		int size = getChildFragmentManager().getFragments().size();
		while (size > 0) {
			Fragment f = getChildFragmentManager().getFragments().get(--size);
			if (f != null && f instanceof MVBaseFragment) {
				MVBaseFragment fragment = (MVBaseFragment) f;
				fragment.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	public int getContentViewId() {
		return 0;
	}

}
