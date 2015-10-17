package info.hugoyu.calculator.android.exception;

public class BlurringException extends Exception {
	
	public static final int CROP_SOURCE_NOT_FOUND = 1;
	
	private int errorCode;
	
	public BlurringException(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}

}
