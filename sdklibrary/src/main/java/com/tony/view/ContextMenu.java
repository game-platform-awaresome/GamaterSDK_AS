package com.tony.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class ContextMenu {
	private final Context context;
	private final View container;
	private final ListView listView;

	private BaseAdapter adapter;
	private OnItemClickListener mItemClickListener;

	private Drawable belowBackground;
	private Drawable aboveBackground;

	private PopupWindow popupWindow;

	public ContextMenu(final Context context) {
		this.context = context;
		this.container = View.inflate(context, ResourceUtil.getLayoutId("vsgm_tony_popup_menu"), null);
		this.container.setBackgroundColor(Color.BLACK);
		this.container.setOnClickListener(new BaseOnClickListener() {
			@Override
			public void onBaseClick(View v) {
				close();
			}
		});
		this.listView = (ListView) this.container.findViewById(ResourceUtil.getId("list"));

		this.listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				ContextMenu.this.close();
				if (mItemClickListener != null)
					mItemClickListener.onItemClick(parent, view, position, id);
			}
		});
	}

	/**
	 * Indicate whether this pop-up menu is showing on screen.
	 * 
	 * @return <code>true</code> if this pop-up menu is showing on screen;
	 *         otherwise, <code>false</code>.
	 */
	public boolean isShowing() {
		return this.popupWindow.isShowing();
	}

	/**
	 * Sets the background drawables for this pop-up menu.
	 * 
	 * @param belowBackground
	 *            the background drawable to be used for showing this pop-up
	 *            menu below the anchor view.
	 * @param aboveBackground
	 *            the background drawable to be used for showing this pop-up
	 *            menu above the anchor view.
	 * @return this pop-up menu.
	 */
	public ContextMenu setBackground(final Drawable belowBackground, final Drawable aboveBackground) {
		this.belowBackground = belowBackground;
		this.aboveBackground = aboveBackground;

		return this;
	}

	/**
	 * Sets the background drawables for this pop-up menu.
	 * 
	 * @param belowBackground
	 *            the background drawable to be used for showing this pop-up
	 *            menu below the anchor view.
	 * @param aboveBackground
	 *            the background drawable to be used for showing this pop-up
	 *            menu above the anchor view.
	 * @return this pop-up menu.
	 */
	public ContextMenu setBackground(final int belowBackground, final int aboveBackground) {
		return this.setBackground(this.context.getResources().getDrawable(belowBackground),
			   this.context.getResources().getDrawable(aboveBackground));
	}

	/**
	 * Sets the callback when a menu item is clicked.
	 * 
	 * @param listener
	 *            a callback when a menu item is clicked.
	 * @return this pop-up menu.
	 */
	public ContextMenu setOnItemClickListener(OnItemClickListener listener) {
		this.mItemClickListener = listener;
		return this;
	}

	/**
	 * Cleans up any resources used by {@link ContextMenu}.
	 * <p>
	 * Applications that uses {@link ContextMenu} must call {@link #onPause()}
	 * in its {@link android.app.Activity#onPause()} to closes any pop-up menu
	 * that is visible. It is safe to call {@link #onPause()} even if it is not
	 * visible.
	 * </p>
	 */
	public void onPause() {
		this.close();
	}

	/**
	 * Closes the pop-up menu if it is visible.
	 */
	public void close() {
		if (this.popupWindow != null) {
			this.popupWindow.dismiss();
		}
	}

	/**
	 * Shows the pop-up menu below/above the given anchor view.
	 * <p>
	 * The pop-up menu will be shown below the given anchor view if the bottom
	 * of the anchor view is below the half-way of the screen; otherwise, it
	 * will be shown above the anchor view.
	 * </p>
	 */
	public void show(final View anchor, final BaseAdapter listAdapter, final int width) {
		this.close();

		this.popupWindow = new PopupWindow(this.container, width, 0, true);
		this.popupWindow.setBackgroundDrawable(new BitmapDrawable());
		this.popupWindow.setOutsideTouchable(true);
		this.popupWindow.setWindowLayoutMode(0, ViewGroup.LayoutParams.WRAP_CONTENT);

		final DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

		final int[] coordinates = new int[2];
		anchor.getLocationOnScreen(coordinates);

		this.adapter = listAdapter;
		this.listView.setAdapter(this.adapter);

		this.popupWindow.update();

		if (coordinates[1] + anchor.getHeight() > metrics.heightPixels / 2) {
			this.popupWindow.showAsDropDown(anchor, 0, -anchor.getHeight());
		} else {
			this.popupWindow.showAsDropDown(anchor);
		}

	}
}
