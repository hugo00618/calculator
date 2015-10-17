package info.hugoyu.calculator.android.activity;

import info.hugoyu.calculator.android.R;
import info.hugoyu.calculator.android.calc.StdCalculator;
import info.hugoyu.calculator.android.exception.NumberOutOfRangeException;
import info.hugoyu.calculator.android.thread.BlurTask;
import info.hugoyu.calculator.android.thread.WbtBooterThread;
import info.hugoyu.calculator.android.util.FileManager;
import info.hugoyu.calculator.android.util.FontSizeCalculator;
import info.hugoyu.calculator.android.util.MyGestureDetector;
import info.hugoyu.calculator.android.util.ResultManager;
import info.hugoyu.calculator.android.util.SystemUtil;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Views
	static ImageView bg1, bg2;
	LinearLayout blackLayout;
	TextView result;
	Button mc, mp, mm, mr, clr, pm, div, multi, num7, num8, num9, sub, num4,
			num5, num6, add, num1, num2, num3, equal, num0, dot;
	View btnsView;

	// Flags
	private static final int WAIT_NUM_1 = 1;
	private static final int WAIT_NUM_2 = 2;
	private static final int LISTEN_NUM_1 = 3;
	private static final int LISTEN_NUM_2 = 4;
	private static final int CALC_METHOD_ADD = 5;
	private static final int CALC_METHOD_SUB = 6;
	private static final int CALC_METHOD_MULTI = 7;
	private static final int CALC_METHOD_DIV = 8;

	private int inputState;
	private boolean mInput;
	private boolean resultInput;

	// Parameters
	private int approNumOfDigits, maxNumOfDigits;
	private double number1, number2;
	private int calcMethod;
	private int textSize, btnSize, textSizeConded;

	// Resources
	static Drawable background;
	Animation fadeIn;
	private static File cacheDir, cacheM, cacheWP, cacheWPN, cacheResult,
			versionInfo;
	ClipboardManager clipboard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize views
		bg1 = (ImageView) findViewById(R.id.img_background);
		bg2 = (ImageView) findViewById(R.id.img_new_background);
		blackLayout = (LinearLayout) findViewById(R.id.layout_main_blackLayout);
		result = (TextView) findViewById(R.id.txt_display);
		mc = (Button) findViewById(R.id.btn_mc);
		mp = (Button) findViewById(R.id.btn_mp);
		mm = (Button) findViewById(R.id.btn_mm);
		mr = (Button) findViewById(R.id.btn_mr);
		clr = (Button) findViewById(R.id.btn_ac);
		pm = (Button) findViewById(R.id.btn_pm);
		div = (Button) findViewById(R.id.btn_div);
		multi = (Button) findViewById(R.id.btn_multi);
		num7 = (Button) findViewById(R.id.btn_num7);
		num8 = (Button) findViewById(R.id.btn_num8);
		num9 = (Button) findViewById(R.id.btn_num9);
		sub = (Button) findViewById(R.id.btn_sub);
		num4 = (Button) findViewById(R.id.btn_num4);
		num5 = (Button) findViewById(R.id.btn_num5);
		num6 = (Button) findViewById(R.id.btn_num6);
		add = (Button) findViewById(R.id.btn_add);
		num1 = (Button) findViewById(R.id.btn_num1);
		num2 = (Button) findViewById(R.id.btn_num2);
		num3 = (Button) findViewById(R.id.btn_num3);
		equal = (Button) findViewById(R.id.btn_equal);
		num0 = (Button) findViewById(R.id.btn_num0);
		dot = (Button) findViewById(R.id.btn_dot);
		btnsView = findViewById(R.id.view_btns);

		// Get device's resolution
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		SystemUtil.setResolution(size);
		int widthPx = size.x;
		int heightPx = size.y;

		// Initialize resources
		background = new ColorDrawable();
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

		// Get cache file
		cacheDir = getCacheDir();
		cacheM = new File(cacheDir, "m");
		Double m = (Double) FileManager.read(cacheM);
		cacheResult = new File(cacheDir, "cache_result");
		versionInfo = new File(cacheDir, "version_info");

		// Calculate font size
		textSize = FontSizeCalculator.calcFontSize(72, widthPx, heightPx);
		btnSize = FontSizeCalculator.calcFontSize(24, widthPx, heightPx);
		textSizeConded = FontSizeCalculator.calcFontSize(48, widthPx, heightPx);

		// Set font size
		result.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		mc.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		mp.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		mm.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		mr.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		clr.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		pm.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		div.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		multi.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num7.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num8.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num9.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		sub.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num4.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num5.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num6.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		add.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num1.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num2.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num3.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		equal.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		num0.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);
		dot.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnSize);

		Integer VERSION_CODE = -1;
		try {
			VERSION_CODE = Integer.valueOf(getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		Integer originalVersion = (Integer) FileManager.read(versionInfo);
		if (originalVersion == null) {
			if (VERSION_CODE != -1) {
				FileManager.write(versionInfo, VERSION_CODE);
			}
			// TODO: Action for newly installed devices:
		} else {
			FileManager.write(versionInfo, VERSION_CODE);
			if (originalVersion < VERSION_CODE) {
				// TODO: Action for updated devices:
				if (originalVersion <= 94) { // v1.1.1 and earlier
					File oldCacheResult = new File(cacheDir, "result");
					if (oldCacheResult.exists()) {
						oldCacheResult.delete();
					}
				}
				if (originalVersion <= 109) { // v1.1.5 and earlier
					File cacheDir, compressedWP, compressCount, compressTimes, performanceOK;
					cacheDir = getCacheDir();
					compressedWP = new File(cacheDir, "compressedWP");
					compressCount = new File(cacheDir, "compressCount");
					compressTimes = new File(cacheDir, "compressTimes");
					performanceOK = new File(cacheDir, "performanceOK");

					compressedWP.delete();
					compressCount.delete();
					compressTimes.delete();
					performanceOK.delete();
				}
			}
		}

		// Set MR appearance
		if (m == null) {
			mr.setBackgroundResource(R.drawable.button_selector);
		} else {
			mr.setBackgroundResource(R.drawable.button_memory);
		}

		// Set action listener
		// TODO: Set mode changing behavior and set it onto "btnView".
		final GestureDetector swipeToChangeModeDetector = new GestureDetector(
				this, new MyGestureDetector() {

					@Override
					public void onSwipe() {

					}

					@Override
					public void onSwipeLeft() {
						SystemUtil.test(getApplicationContext(), "LEFT");
					}

					@Override
					public void onSwipeRight() {
						SystemUtil.test(getApplicationContext(), "RIGHT");
					}

				});

		OnTouchListener swipeToChangeModeListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return swipeToChangeModeDetector.onTouchEvent(event);
			}
		};

		// btnsView.setOnTouchListener(swipeToChangeModeListener);

		final GestureDetector swipeToDeleteDetector = new GestureDetector(this,
				new MyGestureDetector() {
					@Override
					public void onSwipe() {
						performSwipe();
					}

					@Override
					public void onLongPress(MotionEvent e) {
						CharSequence copyResult = result.getText();

						if (!copyResult.equals(getApplicationContext()
								.getResources().getString(R.string.error))) {
							if (clipboard == null) {
								clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							}
							clipboard.setPrimaryClip(ClipData.newPlainText(
									"result", copyResult));
							SystemUtil.makeToast(getApplicationContext(),
									R.string.copied, Toast.LENGTH_SHORT);
						}
					}

					@Override
					public void onSwipeLeft() {

					}

					@Override
					public void onSwipeRight() {

					}
				});

		OnTouchListener swipeToDeleteListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return swipeToDeleteDetector.onTouchEvent(event);
			}
		};

		result.setOnTouchListener(swipeToDeleteListener);

		Object numInput = new OnClickListener() {
			@Override
			public void onClick(View v) {
				insert(v.getId());
			}

		};

		num7.setOnClickListener((OnClickListener) numInput);
		num8.setOnClickListener((OnClickListener) numInput);
		num9.setOnClickListener((OnClickListener) numInput);
		num4.setOnClickListener((OnClickListener) numInput);
		num5.setOnClickListener((OnClickListener) numInput);
		num6.setOnClickListener((OnClickListener) numInput);
		num1.setOnClickListener((OnClickListener) numInput);
		num2.setOnClickListener((OnClickListener) numInput);
		num3.setOnClickListener((OnClickListener) numInput);
		num0.setOnClickListener((OnClickListener) numInput);

		mc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileManager.clear(cacheM);
				mr.setBackgroundResource(R.drawable.button_selector);
				if (mInput) {
					mInput = false;
					result.setText("0");
				}
			}
		});

		mp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Double m = (Double) FileManager.read(cacheM);

				if (m == null) {
					m = Double.valueOf(0);
				}
				if (inputState == LISTEN_NUM_2) {
					proceedEqual();
				}
				if (inputState == WAIT_NUM_2 || resultInput) {
					try {
						m = StdCalculator.doAdd(m, number1);
					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					}
				} else {
					try {
						double temp = Double.parseDouble(result.getText()
								.toString());
						m = StdCalculator.doAdd(m, temp);
					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					} catch (NumberFormatException e) {
						errorReset();
					}
				}

				FileManager.write(cacheM, m);
				mr.setBackgroundResource(R.drawable.button_memory);
				inputState = WAIT_NUM_1;

				try {
					String display = ResultManager
							.testResult(m, maxNumOfDigits);
					setResultText(display);
					mInput = true;
				} catch (NumberOutOfRangeException e) {
					handleNumberOutOfRangeException(e);
				}

			}
		});

		mm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Double m = (Double) FileManager.read(cacheM);

				if (m == null) {
					m = Double.valueOf(0);
				}
				if (inputState == LISTEN_NUM_2) {
					proceedEqual();
				}
				if (inputState == WAIT_NUM_2 || resultInput) {
					try {
						m = StdCalculator.doSub(m, number1);
					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					}
				} else {
					try {
						double temp = Double.parseDouble(result.getText()
								.toString());
						m = StdCalculator.doSub(m, temp);
					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					} catch (NumberFormatException e) {
						errorReset();
					}
				}

				FileManager.write(cacheM, m);
				mr.setBackgroundResource(R.drawable.button_memory);
				inputState = WAIT_NUM_1;

				try {
					String display = ResultManager
							.testResult(m, maxNumOfDigits);
					setResultText(display);
					mInput = true;
				} catch (NumberOutOfRangeException e) {
					handleNumberOutOfRangeException(e);
				}

			}
		});

		mr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Double m = (Double) FileManager.read(cacheM);

				if (m != null) {
					String display = "";

					try {
						display = ResultManager.testResult(m, maxNumOfDigits);
						setResultText(display);
						mInput = true;
					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					}

				} else {
					setResultText("0");
				}

				switch (inputState) {
				case WAIT_NUM_1:
					inputState = LISTEN_NUM_1;
					break;
				case WAIT_NUM_2:
					inputState = LISTEN_NUM_2;
					break;
				}
			}
		});

		clr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allReset();
			}
		});

		pm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String input = result.getText().toString();
				mInput = false;

				if (input.equals(getApplicationContext().getResources()
						.getString(R.string.error))) {
					input = "0";
					setResultText(input);
				} else if (inputState == WAIT_NUM_1) {
					inputState = LISTEN_NUM_1;
					if (resultInput) {
						number1 = -number1;
						try {
							input = ResultManager.testResult(number1,
									maxNumOfDigits);
							setResultText(input);
							resultInput = true;
						} catch (NumberOutOfRangeException e) {
							handleNumberOutOfRangeException(e);
						}
					} else if (input.startsWith("-")) {
						input = input.substring(1);
					} else {
						try {
							input = ResultManager.testResult("-" + input,
									maxNumOfDigits);
							setResultText(input);
						} catch (NumberOutOfRangeException e) {
							handleNumberOutOfRangeException(e);
						}
					}
				} else if (input.equals("0") || inputState == WAIT_NUM_2) {
					input = "-0";
					setResultText(input);
					if (inputState == WAIT_NUM_2) {
						clearMethodButton();
						inputState = LISTEN_NUM_2;
					}
				} else {
					try {
						double d = Double.parseDouble(input);
						d = -d;
						input = ResultManager.testResult(d, maxNumOfDigits);
						setResultText(input);
					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					}
				}
			}
		});

		div.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonPressed(CALC_METHOD_DIV);
				clickMethodButton(CALC_METHOD_DIV);
			}
		});

		multi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonPressed(CALC_METHOD_MULTI);
				clickMethodButton(CALC_METHOD_MULTI);
			}
		});

		sub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonPressed(CALC_METHOD_SUB);
				clickMethodButton(CALC_METHOD_SUB);
			}
		});

		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonPressed(CALC_METHOD_ADD);
				clickMethodButton(CALC_METHOD_ADD);
			}
		});

		dot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (inputState) {
				case WAIT_NUM_1:
					setResultText("0.");
					inputState = LISTEN_NUM_1;
					break;
				case WAIT_NUM_2:
					clearMethodButton();
					setResultText("0.");
					inputState = LISTEN_NUM_2;
					break;
				default:
					String input = result.getText().toString();

					if (input.length() < 8 && !input.contains(".")
							&& !input.contains("E")) {

						if (mInput) {
							mInput = false;
						}

						String output = input + ".";
						setResultText(output);
					}
					break;
				}
			}
		});

		equal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				proceedEqual();
			}
		});

		allReset();
	}

	@Override
	public void onResume() {
		super.onResume();

		String fileName = "";
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			fileName = "wpLand";
			approNumOfDigits = 11;
			maxNumOfDigits = 11;
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			fileName = "wpPort";
			approNumOfDigits = 7;
			maxNumOfDigits = 9;
			break;
		}

		if (cacheResult.exists()) {
			ArrayList<Object> importingParamsAL;

			try {
				importingParamsAL = (ArrayList<Object>) FileManager
						.read(cacheResult);

				if (importingParamsAL != null) {
					inputState = (Integer) importingParamsAL.get(0);
					mInput = (Boolean) importingParamsAL.get(1);
					number1 = (Double) importingParamsAL.get(2);
					calcMethod = (Integer) importingParamsAL.get(4);
					if (inputState == WAIT_NUM_2) {
						setButtonPressed(calcMethod);
					}
					number2 = (Double) importingParamsAL.get(3);
					resultInput = (Boolean) importingParamsAL.get(6);
					String display = "";
					try {
						if (mInput) {
							String inputS = ((Double) FileManager.read(cacheM))
									.toString();
							if (inputS != null) {
								display = ResultManager.testResult(inputS,
										maxNumOfDigits);
							}
						} else if (inputState == WAIT_NUM_1) {
							try {
								display = ResultManager
										.testResult(String.valueOf(number1),
												maxNumOfDigits);
							} catch (NumberOutOfRangeException e) {
								display = "Error";
							}
						} else {
							try {
								display = ResultManager.testResult(
										((CharSequence) importingParamsAL
												.get(5)).toString(),
										maxNumOfDigits);
							} catch (NumberOutOfRangeException e) {
								display = "Error";
							}
						}
						setResultText(display);
					} catch (NumberOutOfRangeException e) {
						errorReset();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				cacheResult.delete();
			}
		}

		cacheWP = new File(cacheDir, fileName);
		cacheWPN = new File(cacheDir, fileName + "N");

		if (cacheWPN.exists()) {
			cacheWPN.renameTo(cacheWP);
		}
		if (cacheWP.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap oldWP = BitmapFactory.decodeFile(cacheWP.getPath(), options);
			background = new BitmapDrawable(getResources(), oldWP);
		}

		if (background != null) {
			bg1.setImageDrawable(background);
		}

		new WbtBooterThread(new BlurTask(this, bg2, fadeIn)).start();
	}

	@Override
	public void onPause() {
		super.onPause();
		ArrayList<Object> cachingParamsAL = new ArrayList<Object>();
		cachingParamsAL.add(0, inputState);
		cachingParamsAL.add(1, mInput);
		cachingParamsAL.add(2, number1);
		cachingParamsAL.add(3, number2);
		cachingParamsAL.add(4, calcMethod);
		cachingParamsAL.add(5, result.getText());
		cachingParamsAL.add(6, resultInput);
		FileManager.write(cacheResult, cachingParamsAL);
	}

	// Methods
	private void allReset() {
		result.setText("0");
		reset();
	}

	private void errorReset() {
		result.setText((getApplicationContext().getResources()
				.getString(R.string.error)));
		reset();
	}

	private void reset() {
		number1 = 0;
		number2 = 0;
		calcMethod = 0;
		inputState = WAIT_NUM_1;
		mInput = false;
		resultInput = false;
		result.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		clearMethodButton();
	}

	private void insert(int id) {
		String insert = "";

		switch (id) {
		case R.id.btn_num0:
			insert = "0";
			break;
		case R.id.btn_num1:
			insert = "1";
			break;
		case R.id.btn_num2:
			insert = "2";
			break;
		case R.id.btn_num3:
			insert = "3";
			break;
		case R.id.btn_num4:
			insert = "4";
			break;
		case R.id.btn_num5:
			insert = "5";
			break;
		case R.id.btn_num6:
			insert = "6";
			break;
		case R.id.btn_num7:
			insert = "7";
			break;
		case R.id.btn_num8:
			insert = "8";
			break;
		case R.id.btn_num9:
			insert = "9";
			break;
		}

		switch (inputState) {
		case WAIT_NUM_1:
			result.setText("0");
			inputState = LISTEN_NUM_1;
			break;
		case WAIT_NUM_2:
			clearMethodButton();
			result.setText("0");
			inputState = LISTEN_NUM_2;
			break;
		}

		String input = result.getText().toString();
		if (input.contains("E") && !input.endsWith("E")) {
			input = ResultManager.deSciNotation(input);
		}
		String display = input + insert;

		mInput = false;

		if (!display.contains(".")) {
			try {
				display = ResultManager.testResult(display, maxNumOfDigits);
			} catch (NumberOutOfRangeException e) {
				handleNumberOutOfRangeException(e);
			}
			setResultText(display);
		} else if (display.length() < maxNumOfDigits) {
			setResultText(display);
		}
	}

	private void clickMethodButton(int method) {
		switch (inputState) {
		case LISTEN_NUM_2:
			proceedEqual();
			calcMethod = method;
			inputState = WAIT_NUM_2;
			break;
		case WAIT_NUM_2:
			calcMethod = method;
			break;
		default: // WAIT_NUM1 and LISTEN_NUM_1
			calcMethod = method;
			String input = result.getText().toString();
			if (input.equals((getApplicationContext().getResources()
					.getString(R.string.error))) || input.equals("-")) {
				number1 = 0;
				result.setText("0");
			} else if (mInput) {
				Double inputD = (Double) FileManager.read(cacheM);
				if (inputD != null) {
					number1 = inputD;
				}
				mInput = false;
			} else if (!resultInput) {
				try {
					number1 = Double.parseDouble(input);
				} catch (NumberFormatException e) {
					errorReset();
				}
			}
			inputState = WAIT_NUM_2;
			break;
		}
	}

	private void proceedEqual() {
		if (inputState == LISTEN_NUM_1) {
			try {
				Double.parseDouble(result.getText().toString());
			} catch (NumberFormatException e) {
				errorReset();
			}
		} else if (inputState != WAIT_NUM_1) {
			try {
				number2 = Double.parseDouble(result.getText().toString());
			} catch (NumberFormatException e) {
				errorReset();
			}
		}

		if (inputState == WAIT_NUM_1 || inputState == LISTEN_NUM_2) {
			double output;

			switch (calcMethod) {
			case CALC_METHOD_DIV:
				if (number2 != 0) {
					try {
						output = StdCalculator.doDiv(number1, number2);
						number1 = output;
						String display = ResultManager.testResult(output,
								maxNumOfDigits);
						setResultText(display);

					} catch (NumberOutOfRangeException e) {
						handleNumberOutOfRangeException(e);
					}
				} else {
					errorReset();
				}
				break;
			case CALC_METHOD_MULTI:
				try {
					output = StdCalculator.doMulti(number1, number2);
					number1 = output;

					String display = ResultManager.testResult(output,
							maxNumOfDigits);
					setResultText(display);

				} catch (NumberOutOfRangeException e) {
					handleNumberOutOfRangeException(e);
				}
				break;
			case CALC_METHOD_SUB:
				try {
					output = StdCalculator.doSub(number1, number2);
					number1 = output;

					String display = ResultManager.testResult(output,
							maxNumOfDigits);
					setResultText(display);

				} catch (NumberOutOfRangeException e) {
					handleNumberOutOfRangeException(e);
				}
				break;
			case CALC_METHOD_ADD:
				try {
					output = StdCalculator.doAdd(number1, number2);
					number1 = output;

					String display = ResultManager.testResult(output,
							maxNumOfDigits);
					setResultText(display);

				} catch (NumberOutOfRangeException e) {
					handleNumberOutOfRangeException(e);
				}
				break;
			}
			resultInput = true;
			inputState = WAIT_NUM_1;
		}
		clearMethodButton();
	}

	private void clearMethodButton() {
		div.setBackgroundResource(R.drawable.button_selector);
		multi.setBackgroundResource(R.drawable.button_selector);
		sub.setBackgroundResource(R.drawable.button_selector);
		add.setBackgroundResource(R.drawable.button_selector);
	}

	private void handleNumberOutOfRangeException(NumberOutOfRangeException e) {
		int errorCode = e.getErrorCode();

		switch (errorCode) {
		case NumberOutOfRangeException.TOO_BIG:
			errorReset();
			break;
		}
	}

	public static void setBackground(Drawable mBackground) {
		background = mBackground;
	}

	public void setResultText(String input) {
		result.setText(input);
		resultInput = false;
		if (input.length() > approNumOfDigits) {
			result.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConded);
		} else {
			result.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		}
	}

	private void performSwipe() {
		String input = result.getText().toString();
		resultInput = false;
		mInput = false;
		if (input.equals(getApplicationContext().getResources().getString(
				R.string.error))) {
			result.setText("0");
		} else {
			int length = input.length();

			if (!input.equals("0") && length > 1) {
				StringBuffer sb = new StringBuffer(input);
				sb.deleteCharAt(length - 1);
				result.setText(sb);
			} else if (length == 1) {
				result.setText("0");
			}
		}
	}

	private void setButtonPressed(int method) {
		switch (method) {
		case CALC_METHOD_ADD:
			div.setBackgroundResource(R.drawable.button_selector);
			multi.setBackgroundResource(R.drawable.button_selector);
			sub.setBackgroundResource(R.drawable.button_selector);
			add.setBackgroundResource(R.drawable.button_pressed);
			break;
		case CALC_METHOD_SUB:
			System.out.println("SUB");
			div.setBackgroundResource(R.drawable.button_selector);
			multi.setBackgroundResource(R.drawable.button_selector);
			sub.setBackgroundResource(R.drawable.button_pressed);
			add.setBackgroundResource(R.drawable.button_selector);
			break;
		case CALC_METHOD_MULTI:
			div.setBackgroundResource(R.drawable.button_selector);
			multi.setBackgroundResource(R.drawable.button_pressed);
			sub.setBackgroundResource(R.drawable.button_selector);
			add.setBackgroundResource(R.drawable.button_selector);
			break;
		case CALC_METHOD_DIV:
			div.setBackgroundResource(R.drawable.button_pressed);
			multi.setBackgroundResource(R.drawable.button_selector);
			sub.setBackgroundResource(R.drawable.button_selector);
			add.setBackgroundResource(R.drawable.button_selector);
			break;
		}
	}

	private void recommandWear() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.introduceWear);

		builder.setNeutralButton(R.string.dismiss,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});

		builder.setPositiveButton(R.string.learnMore,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent browserIntent = new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("https://play.google.com/store/apps/details?id=info.hugoyu.calculator.android.wear"));
						startActivity(browserIntent);
					}
				});

		AlertDialog dialog = builder.show();

		TextView messageView = (TextView) dialog
				.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}

}
