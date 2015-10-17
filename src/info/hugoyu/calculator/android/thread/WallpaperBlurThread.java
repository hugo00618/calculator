package info.hugoyu.calculator.android.thread;

import info.hugoyu.calculator.android.exception.BlurringException;
import info.hugoyu.calculator.android.util.BitmapManager;
import info.hugoyu.calculator.android.util.SystemUtil;
import android.graphics.Bitmap;
import android.graphics.Point;

public class WallpaperBlurThread extends Thread {

	public static final int BLUR_STATE_COMPLETED = 1;
	public static final int BLUR_STATE_OUT_OF_MEMORY = 2;
	public static final int BLUR_STATE_ERROR = 3;

	private BlurTask bt;

	public WallpaperBlurThread(BlurTask bt) {
		this.bt = bt;
	}

	@Override
	public void run() {
		Bitmap blurredBM = null;
		try {
			Point resolution = SystemUtil.getResolution();
			Bitmap croppedWPBM = BitmapManager.crop(bt.getWallpaperBitmap(),
					resolution.x, resolution.y);

			long startTime = System.currentTimeMillis();
			blurredBM = BitmapManager
					.fastblur(bt.getContext(), croppedWPBM, 25);

			if ((System.currentTimeMillis() - startTime) < 1000) {
				blurredBM = BitmapManager.fastblur(bt.getContext(), blurredBM,
						25);
				if ((System.currentTimeMillis() - startTime) < 1500) {
					blurredBM = BitmapManager.fastblur(bt.getContext(),
							blurredBM, 25);
					if ((System.currentTimeMillis() - startTime) < 1750) {
						blurredBM = BitmapManager.fastblur(bt.getContext(),
								blurredBM, 25);
					}
				}
			}
			bt.setWallpaper(blurredBM);
			bt.reportState(BLUR_STATE_COMPLETED);
		} catch (OutOfMemoryError e) {
			if (blurredBM == null) {
				bt.reportState(BLUR_STATE_OUT_OF_MEMORY);
			} else {
				bt.setWallpaper(blurredBM);
				bt.reportState(BLUR_STATE_COMPLETED);
			}
		} catch (BlurringException e) {
			switch (e.getErrorCode()) {
			case BlurringException.CROP_SOURCE_NOT_FOUND:
				bt.reportState(BLUR_STATE_ERROR);
				break;
			}
		}
	}

}
