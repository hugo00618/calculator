package info.hugoyu.calculator.android.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.widget.Toast;

public class SystemUtil {

	private static long time;
	private static Point resolution;

	public static void test(Context context, String s) {
		makeToast(context, s, Toast.LENGTH_LONG);
	}

	public static void makeToast(Context context, int resId, int duration) {

		if (time == 0) {
			time = System.currentTimeMillis();
		}

		if (time <= System.currentTimeMillis()
				&& duration == Toast.LENGTH_SHORT) {
			Toast.makeText(context, resId, duration).show();
			time = System.currentTimeMillis() + 2000;
		}

		if (time <= System.currentTimeMillis() && duration == Toast.LENGTH_LONG) {
			Toast.makeText(context, resId, duration).show();
			time = System.currentTimeMillis() + 4000;
		}
	}

	public static void makeToast(Context context, String s, int duration) {

		if (time == 0) {
			time = System.currentTimeMillis();
		}

		if (time <= System.currentTimeMillis()
				&& duration == Toast.LENGTH_SHORT) {
			Toast.makeText(context, s, duration).show();
			time = System.currentTimeMillis() + 2000;
		}

		if (time <= System.currentTimeMillis() && duration == Toast.LENGTH_LONG) {
			Toast.makeText(context, s, duration).show();
			time = System.currentTimeMillis() + 4000;
		}
	}

	public static void setResolution(Point mResolution) {
		resolution = mResolution;
	}

	public static Point getResolution() {
		return resolution;
	}
	
	public static boolean isPackageInstalled(String packagename, Context context) {
	    PackageManager pkgMgr = context.getPackageManager();
	    try {
	        pkgMgr.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        return true;
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	}
}
