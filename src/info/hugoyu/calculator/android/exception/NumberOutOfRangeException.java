package info.hugoyu.calculator.android.exception;

public class NumberOutOfRangeException extends Exception {

	public static final int TOO_BIG = 1;

	private int errorCode;

	public NumberOutOfRangeException(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
