package info.hugoyu.calculator.android.util;

import android.util.Log;
import info.hugoyu.calculator.android.exception.NumberOutOfRangeException;

/**
 * 
 * @author Hugo Yu
 * @version 1.0.4.1
 */

public class ResultManager {

	/**
	 * 
	 * @param input
	 *            A String that has not been tested
	 * @param maxDigit
	 *            The maximum number of char allowed to be exported
	 * @return Converted string that is shortened to the requested number of
	 *         digits
	 * @throws NumberOutOfRangeException
	 *             The exception would be thrown if the imported variable has
	 *             exceeded its RAM range
	 */
	public static String testResult(String input, int maxDigit)
			throws NumberOutOfRangeException {
		// System.out.println("testResult(" + input + ", " + maxDigit);
		double inputD = Double.parseDouble(input);
		if (Math.abs(inputD) > Double.MAX_VALUE) {
			throw new NumberOutOfRangeException(
					NumberOutOfRangeException.TOO_BIG);
		}
		input = String.valueOf(inputD);
		if (input.length() <= maxDigit) {
			if (input.contains("E")) {
				String[] radixNExponent = input.split("E");
				return removeZeros(radixNExponent[0]) + "E" + radixNExponent[1];
			} else {
				return removeZeros(input);
			}
		} else {
			if (input.contains("E")) {
				input = deSciNotation(input);
			}
			if (input.length() <= maxDigit) {
				return removeZeros(String.valueOf(inputD));
			} else {
				return shortenResult(String.valueOf(inputD), maxDigit);
			}
		}

	}

	public static String testResult(double input, int maxDigit)
			throws NumberOutOfRangeException {
		// System.out.println("testResult(" + input + ", " + maxDigit);
		return testResult(String.valueOf(input), maxDigit);
	}

	public static String shortenResult(String input, int digit) {
		// System.out.println("shortenResult(" + input + ", " + digit);
		if (input.contains("E")) {
			return sciNotation(deSciNotation(input), digit);
		} else if (input.contains(".")) {
			int indexOfDot = input.indexOf(".");

			String intPart = input.substring(0, indexOfDot);

			if (intPart.length() > digit) {
				return shortenResult(String.valueOf(intPart), digit);
			} else {
				return round(input, digit - intPart.length() - 1);
			}
		} else {
			return sciNotation(input, digit);
		}
	}

	/**
	 * 
	 * @param input
	 *            A regular notation number that is expected to convert into
	 *            scientific notation.
	 * @param places
	 *            Expected total number of char.
	 * @return Converted input in scientific notation.
	 */
	public static String sciNotation(String input, int places) {
		// System.out.println("sciNotation(" + input + ", " + places);
		input = removeZeros(input);
		boolean negative = false;
		String radix;
		int exponent;

		if (input.startsWith("-")) {
			negative = true;
			input = input.substring(1);
		}

		StringBuffer inputSB = new StringBuffer(input);
		if (!input.contains(".")) {
			exponent = inputSB.length() - 1;
			radix = inputSB.insert(1, ".").toString();
		} else if (input.startsWith("0.")) {
			radix = input.substring(2);
			exponent = -1;
			while (radix.startsWith("0")) {
				radix = radix.substring(1);
				exponent--;
			}
			if (radix.length() > 1) {
				radix = radix.charAt(0) + "." + radix.substring(1);
			}
		} else {
			int indexOfDot = input.indexOf(".");
			exponent = indexOfDot - 1;
			inputSB = inputSB.deleteCharAt(indexOfDot);
			inputSB = inputSB.insert(1, ".");
			radix = inputSB.toString();
		}

		int targetRadixLength = places - String.valueOf(exponent).length() - 1;
		if (targetRadixLength < 3) {
			radix = round(radix, 0);
		} else {
			radix = round(radix, targetRadixLength - 2);
		}

		double radixD = Double.parseDouble(radix);

		if (radixD >= 10) {
			while (radixD >= 10) {
				radixD /= 10.0;
				exponent++;
			}

			radix = String.valueOf(radixD);
			radix = removeZeros(radix);
		}

		if (negative) {
			radix = "-" + radix;
		}

		return radix + "E" + exponent;
	}

	/**
	 * 
	 * @param sciNotation
	 *            A scientific notation number that is expected to be converted
	 *            into regular notation.
	 * @return Converted input in regular notation.
	 */
	public static String deSciNotation(String sciNotation) {
		// System.out.println("deSciNotation(" + sciNotation + ")");
		boolean negative = false;
		String[] radixNExponent = sciNotation.split("E");
		String radix = radixNExponent[0];
		int exponent = Integer.parseInt(radixNExponent[1]);

		double radixD = Double.parseDouble(radix);

		if (radixD < 0) {
			radixD = -radixD;
			negative = true;
		}

		while (radixD >= 10) {
			radixD /= 10.0;
			exponent++;
		}

		radix = String.valueOf(radixD);
		StringBuffer radixSB = new StringBuffer(radix);
		radixSB = radixSB.deleteCharAt(1);
		int indexOfInsertion = exponent + 1;
		if (indexOfInsertion > radixSB.length()) {
			while (indexOfInsertion > radixSB.length()) {
				radixSB.append("0");
			}
		} else if (indexOfInsertion <= 0) {
			while (indexOfInsertion <= 0) {
				radixSB = radixSB.insert(0, "0");
				indexOfInsertion++;
			}
			radixSB = radixSB.insert(1, ".");
		} else if (indexOfInsertion < radixSB.length()) {
			radixSB.insert(indexOfInsertion, ".");
		}

		if (negative) {
			radixSB.insert(0, "-");
		}

		return radixSB.toString();
	}

	/**
	 * 
	 * @param input
	 *            A number possibly having unnecessary zeros.
	 * @return Input which has been removed unnecessary zeros.
	 */
	public static String removeZeros(String input) {
		// System.out.println("removeZeros(" + input);
		if (input.contains("E")) {
			int indexOfE = input.indexOf("E");

			String radix = input.substring(0, indexOfE);
			String exponent = input.substring(indexOfE);

			return removeZeros(radix) + exponent;
		}

		if (input.contains(".")) {
			while (input.endsWith("0")) {
				input = input.substring(0, input.length() - 1);
			}
			if (input.endsWith(".")) {
				input = input.substring(0, input.length() - 1);
			}
		}
		return input;
	}

	/**
	 * 
	 * @param input
	 *            A number that is expected to be rounded to certain number of
	 *            decimal.
	 * @param targetDecimalLength
	 *            The expected length of decimal.
	 * @return The number that has been rounded to the requested number of
	 *         decimal.
	 */
	public static String round(String input, int targetDecimalLength) {
		// System.out.println("round(" + input + ", " + targetDecimalLength);
		int indexOfDot = input.indexOf(".");
		if (indexOfDot == -1) {
			return input;
		}

		boolean negative = false;

		if (input.startsWith("-")) {
			input = input.substring(1);
			indexOfDot = input.indexOf(".");
			negative = true;
		}

		int intPart = Integer.parseInt(input.substring(0, indexOfDot));
		int startIndexOfDecimal = indexOfDot + 1;

		// TODO: Debug here
		// String decimalPart = input.substring(startIndexOfDecimal,
		// startIndexOfDecimal + 1 + targetDecimalLength);

		String decimalPart = "";
		try {
			decimalPart = input.substring(startIndexOfDecimal,
					startIndexOfDecimal + 1 + targetDecimalLength);
		} catch (StringIndexOutOfBoundsException e) {
			Log.d("oobException", input);
		}

		if (decimalPart.length() > targetDecimalLength) {
			if (decimalPart.length() == 1) {
				long rounded = Math.round(Double.parseDouble(intPart + "."
						+ decimalPart));
				return String.valueOf(rounded);
			} else {
				StringBuffer decimalPartSB = new StringBuffer(decimalPart);
				decimalPartSB = decimalPartSB.insert(
						decimalPartSB.length() - 1, ".");
				int numberOfLeadingZeros = 0;
				String decimalPartS = decimalPartSB.toString();
				while (decimalPartS.startsWith("0")
						&& decimalPartS.charAt(1) != '.') {
					decimalPartS = decimalPartS.substring(1);
					numberOfLeadingZeros++;
				}
				long roundedDecimalL = Math.round(Double
						.parseDouble(decimalPartS));
				String roundedDecimalS = String.valueOf(roundedDecimalL);
				while (numberOfLeadingZeros > 0) {
					roundedDecimalS = "0" + roundedDecimalS;
					numberOfLeadingZeros--;
				}
				StringBuffer roundedDecimalSB = new StringBuffer(
						roundedDecimalS);

				if (roundedDecimalSB.length() > decimalPartSB.length() - 2) {
					intPart++;
					roundedDecimalSB.deleteCharAt(0);
				}

				String rounded = intPart + "." + roundedDecimalSB;
				rounded = removeZeros(rounded);
				if (negative) {
					return "-" + rounded;
				} else {
					return rounded;
				}
			}
		} else {
			if (negative) {
				return "-" + input;
			} else {
				return input;
			}
		}
	}
}
