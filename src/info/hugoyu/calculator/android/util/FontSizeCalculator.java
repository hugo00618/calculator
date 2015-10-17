package info.hugoyu.calculator.android.util;

public class FontSizeCalculator {

	public static int calcFontSize(int originalPxSize, int widthPx, int heightPx) {
		double adjustedPxSize1 = originalPxSize * widthPx / 320.0;
		double adjustedPxSize2 = originalPxSize * heightPx / 480.0;
		double pxSizeD = adjustedPxSize1 > adjustedPxSize2 ? adjustedPxSize2
				: adjustedPxSize1;
		int pxSizeI = (int) pxSizeD;
		return pxSizeI;
	}
}
