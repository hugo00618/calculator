package info.hugoyu.calculator.android.util;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public abstract class MyGestureDetector extends SimpleOnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onSwipe();

				if (e2.getX() - e1.getX() > 0) {
					onSwipeRight();
				} else {
					onSwipeLeft();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	public abstract void onSwipe();

	public abstract void onSwipeLeft();

	public abstract void onSwipeRight();

}