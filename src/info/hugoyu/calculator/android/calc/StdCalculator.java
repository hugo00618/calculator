package info.hugoyu.calculator.android.calc;

import info.hugoyu.calculator.android.exception.NumberOutOfRangeException;

public class StdCalculator {

	public static double doDiv(double num1, double num2)
			throws NumberOutOfRangeException {
		double result = num1 / num2;
		return testRange(result);
	}

	public static double doMulti(double num1, double num2)
			throws NumberOutOfRangeException {
		double result = num1 * num2;
		return testRange(result);
	}

	public static double doSub(double num1, double num2)
			throws NumberOutOfRangeException {
		double result = num1 - num2;
		return testRange(result);
	}

	public static double doAdd(double num1, double num2)
			throws NumberOutOfRangeException {
		double result = num1 + num2;
		return testRange(result);
	}

	private static double testRange(double input)
			throws NumberOutOfRangeException {
		double absResult = Math.abs(input);

		if (absResult > Double.MAX_VALUE) {
			throw new NumberOutOfRangeException(
					NumberOutOfRangeException.TOO_BIG);
		} else if (absResult < Double.MIN_VALUE) {
			return 0;
		} else {
			return input;
		}
	}

}
