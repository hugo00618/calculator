package info.hugoyu.calculator.android.thread;

import info.hugoyu.calculator.android.util.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

public class BlurTask {

	private Drawable wallpaperDra;
	private Bitmap wallpaperBmp;
	private Context context;
	private ImageView bg2;
	private Animation fadeIn;
	// private static File cacheDir, compressedWP, compressCount, performanceOK;
	private WallpaperBlurThread wbt;

	private static BlurManager sBlurManager;

	public BlurTask(Context context) {
		this.context = context;
	}

	public BlurTask(Context context, ImageView bg2, Animation fadeIn) {
		this.context = context;
		this.bg2 = bg2;
		this.fadeIn = fadeIn;
		// cacheDir = context.getCacheDir();
		// compressedWP = new File(cacheDir, "compressedWP");
		// compressCount = new File(cacheDir, "compressCount");
		// performanceOK = new File(cacheDir, "performanceOK");

		sBlurManager = BlurManager.getInstance();
	}

	public void launch() {
		WallpaperManager wm = WallpaperManager.getInstance(context);
		try {
			wallpaperBmp = (((BitmapDrawable) wm.getDrawable()).getBitmap());
		} catch (SecurityException se) {
			se.printStackTrace();

			File urlFile = new File(context.getCacheDir(), "currentUrl");
			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());
			HttpPost httppost = new HttpPost(
					"http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US");
			httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			String result = null;
			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				result = sb.toString();
			} catch (Exception e) {
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception squish) {
				}
			}

			try {
				JSONObject jObject = new JSONObject(result);
				String images = jObject.getString("images");
				URL imageUrl = new URL("http://www.bing.com"
						+ new JSONObject(images.substring(1,
								images.length() - 1)).getString("url"));
				if (!urlFile.exists()
						|| !imageUrl.equals(FileManager.read(urlFile))) {
					wallpaperBmp = BitmapFactory.decodeStream(imageUrl
							.openConnection().getInputStream());
					FileManager.write(urlFile, imageUrl);
				}
			} catch (NullPointerException e) {

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// if (performanceOK.exists()) {
		// compressedWP.delete();
		/*
		 * } else if (compressedWP.exists() && !compressTimes.exists()) {
		 * System.out.println("A"); BitmapFactory.Options options = new
		 * BitmapFactory.Options(); options.inPreferredConfig =
		 * Bitmap.Config.ARGB_8888; wallpaperBmp =
		 * BitmapFactory.decodeFile(compressedWP.getPath(), options);
		 * //compressedWP.delete();
		 */
		// } else

		/*
		 * if (compressCount.exists() && !performanceOK.exists()) {
		 * System.out.println("B"); cacheWallpaper(); BitmapFactory.Options
		 * options = new BitmapFactory.Options(); options.inPreferredConfig =
		 * Bitmap.Config.ARGB_8888; wallpaperBmp =
		 * BitmapFactory.decodeFile(compressedWP.getPath(), options); //
		 * compressedWP.delete(); }
		 */

		if (wallpaperBmp != null && wallpaperBmp.getByteCount() > 4) {
			wbt = new WallpaperBlurThread(this);
			wbt.start();
		}
	}

	public void reportState(int state) {
		int outState = 0;

		switch (state) {
		case WallpaperBlurThread.BLUR_STATE_COMPLETED:
			/*
			 * compressedWP.delete(); if (!compressCount.exists()) { try {
			 * performanceOK.createNewFile(); } catch (IOException e) { } }
			 */
			outState = BlurManager.TASK_COMPLETE;
			new WallpaperSavingThread(this).start();
			break;
		case WallpaperBlurThread.BLUR_STATE_OUT_OF_MEMORY:
			// cacheWallpaper();

			/*
			 * int count; if (compressCount.exists()) { count = (Integer)
			 * FileManager.read(compressCount); count++; } else { count = 1; }
			 * FileManager.write(compressCount, count); // launch();
			 */
			break;
		case WallpaperBlurThread.BLUR_STATE_ERROR:
			break;
		}
		sBlurManager.handleState(this, outState);
	}

	public void setWallpaper(Bitmap wallpaperBM) {
		this.wallpaperBmp = wallpaperBM;
		this.wallpaperDra = new BitmapDrawable(context.getResources(),
				wallpaperBM);
	}

	public Drawable getWallpaperDrawable() {
		return wallpaperDra;
	}

	public Bitmap getWallpaperBitmap() {
		return wallpaperBmp;
	}

	public ImageView getBg2() {
		return bg2;
	}

	public Context getContext() {
		return context;
	}

	public Animation getFadeIn() {
		return fadeIn;
	}

	/*
	 * private void cacheWallpaper() { if (wallpaperBmp != null) { int quality =
	 * (int) (100 / Math.pow(2, (Integer) FileManager.read(compressCount))); try
	 * { FileOutputStream fos = new FileOutputStream(compressedWP);
	 * wallpaperBmp.compress(Bitmap.CompressFormat.PNG, quality, fos); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } } }
	 */

	public WallpaperBlurThread getWallpaperBlurThread() {
		return wbt;
	}

}