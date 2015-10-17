package info.hugoyu.calculator.android.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;

public class WallpaperSavingThread extends Thread {

	private Context context;
	private BlurTask bt;

	public WallpaperSavingThread(BlurTask bt) {
		context = bt.getContext();
		this.bt = bt;
	}

	@Override
	public void run() {
		String fileName = "";

		switch (context.getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			fileName = "wpLandN";
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			fileName = "wpPortN";
			break;
		}

		File cacheWallpaper = new File(context.getCacheDir(), fileName);
		try {
			FileOutputStream fos = new FileOutputStream(cacheWallpaper);
			bt.getWallpaperBitmap().compress(Bitmap.CompressFormat.PNG, 100,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
