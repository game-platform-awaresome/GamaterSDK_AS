package com.gamater.dialog.util;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.StateSet;

public class DrawableUtil {
	public static ShapeDrawable createRoundedRectDrawable(int color,
			float radius, float insetWidth) {
		float[] outerRadii = new float[] { radius, radius, radius, radius,
				radius, radius, radius, radius };
		float innerRadius = radius - insetWidth;
		float[] innerRadii = new float[] { innerRadius, innerRadius,
				innerRadius, innerRadius, innerRadius, innerRadius,
				innerRadius, innerRadius };
		RectF inset = new RectF(insetWidth, insetWidth, insetWidth, insetWidth);
		RoundRectShape roundRectShape = new RoundRectShape(outerRadii, inset,
				innerRadii);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
		shapeDrawable.getPaint().setColor(color);
		return shapeDrawable;
	}

	public static ShapeDrawable createRoundDrawable(int color, float radius) {
		float[] outerRadii = new float[] { radius, radius, radius, radius,
				radius, radius, radius, radius };
		float[] innerRadii = new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		RectF inset = new RectF(0, 0, 0, 0);
		RoundRectShape roundRectShape = new RoundRectShape(outerRadii, inset,
				innerRadii);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
		shapeDrawable.getPaint().setColor(color);
		return shapeDrawable;
	}

	public static StateListDrawable createRoundColorStateListDrawable(
			float radius, float insetWidth, int defaultColor, int pressedColor,
			int disableColor) {
		Drawable drawablePressed = createRoundDrawable(pressedColor, radius);
		Drawable drawableDefault = createRoundDrawable(defaultColor, radius);
		Drawable drawableDisable = createRoundDrawable(disableColor, radius);
		return createStateListDrawable(drawablePressed, drawableDefault,
				drawableDisable);
	}

	public static StateListDrawable createStateListDrawable(
			Drawable drawablePressed, Drawable drawableDefault,
			Drawable drawableDisable) {
		StateListDrawable stateListDrawable = new StateListDrawable();
		if (drawablePressed != null)
			stateListDrawable
					.addState(new int[] { android.R.attr.state_pressed },
							drawablePressed);
		if (drawableDisable != null)
			stateListDrawable.addState(
					new int[] { -android.R.attr.state_enabled },
					drawableDisable);
		if (drawableDefault != null)
			stateListDrawable.addState(StateSet.WILD_CARD, drawableDefault);
		return stateListDrawable;
	}
}
