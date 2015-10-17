package info.hugoyu.calculator.android.thread;

import info.hugoyu.calculator.android.activity.MainActivity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.animation.Animation;
import android.widget.ImageView;

public class BlurManager {

	public static final int TASK_COMPLETE = 1;

	private Handler mHandler;

	private static BlurManager sInstance = null;

	static {
		sInstance = new BlurManager();
	}

	private BlurManager() {

		mHandler = new Handler(Looper.getMainLooper()) {

			@Override
			public void handleMessage(Message inputMessage) {
				BlurTask bt = (BlurTask) inputMessage.obj;

				switch (inputMessage.what) {
				case TASK_COMPLETE:
					setBackground(bt);
					break;
				}
			}
		};

	}

	public static BlurManager getInstance() {
		return sInstance;
	}

	public void handleState(BlurTask bt, int state) {
		switch (state) {
		case TASK_COMPLETE:
			Message completeMessage = mHandler.obtainMessage(state, bt);
			completeMessage.sendToTarget();
			break;
		}
	}

	private void setBackground(BlurTask bt) {
		ImageView bg2 = bt.getBg2();
		Animation fadeIn = bt.getFadeIn();
		Drawable wallpaperDra = bt.getWallpaperDrawable();

		MainActivity.setBackground(wallpaperDra);

		bg2.setImageDrawable(wallpaperDra);
		bg2.setImageAlpha(255);

		bg2.startAnimation(fadeIn);
	}

}
