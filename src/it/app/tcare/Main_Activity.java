package it.app.tcare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Main_Activity extends Activity {

	private TextView label_start, label_pause, label_stop, title, title2,
			percentage, percentuale_simbolo, duty, time, zero, dieci, venti,
			trenta, quaranta, cinquanta, sessanta, settanta, ottanta, novanta,
			cento, label_continuos;
	private Button play, stop, pause, cap, res, body, face, menu, energy,
			frequency, continuos;
	private SeekBar seek_bar_percentage;

	public static Utility utility;

	private SharedPreferences preferences;

	public static Activity activity;

	private static final int REQUEST_CODE_TEST = 0;

	public static boolean start_in_progress = false;
	private boolean bConfiged = false;

	public String act_string;

	public static Thread thread = new Thread() {
		@Override
		public void run() {
			while (true) {
				while (start_in_progress) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						start_in_progress = false;
					}

					utility.writeData("W");

				}
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (preferences.getBoolean("exit", false))
			finish();

		utility.ResumeAccessory(bConfiged);

		if (requestCode == REQUEST_CODE_TEST) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.hasExtra("comandi_da_eseguire")) {

					Bundle b = data.getExtras();
					String[] array = b.getStringArray("comandi_da_eseguire");

					for (int i = 0; i < array.length; i++) {
						if (array[i].length() > 1) {
							utility.MandaDati(Integer.valueOf(array[i]));
						} else {
							utility.writeData(array[i]);
						}
					}
				}
			}
		}

		utility.writeData("a");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		utility.DestroyAccessory(true);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.exit(0);
		super.onDestroy();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		preferences.edit().putBoolean("isMenu", false).commit();

		activity = this;

		label_continuos = (TextView) findViewById(R.id.label_continuos);

		utility = new Utility(this);

		title = (TextView) findViewById(R.id.title);
		title2 = (TextView) findViewById(R.id.title2);

		percentuale_simbolo = (TextView) findViewById(R.id.percentuale_simbolo);

		duty = (TextView) findViewById(R.id.duty);
		time = (TextView) findViewById(R.id.time);

		continuos = (Button) findViewById(R.id.button_continuos);
		continuos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (preferences.getBoolean("isPlaying", false))
					utility.writeData("P");

				if (label_continuos.getVisibility() == View.VISIBLE) {
					utility.writeData("0");
				} else {
					if (label_continuos.getText().subSequence(1, 2).toString()
							.equals("z")) {
						utility.writeData("1");
					} else {

						utility.writeData(label_continuos.getText()
								.subSequence(1, 2).toString());
					}
				}
			}
		});

		frequency = (Button) findViewById(R.id.frequency);
		frequency.setTag(R.drawable.button_457);
		frequency.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (preferences.getBoolean("isPlaying", false))
					utility.writeData("P");

				switch ((Integer) frequency.getTag()) {
				case R.drawable.button_457:
					utility.writeData("s");
					break;
				case R.drawable.button_571:
					utility.writeData("m");
					break;
				case R.drawable.button_714:
					utility.writeData("q");
					break;
				case R.drawable.button_145:
					utility.writeData("c");
					break;

				}
			}

		});

		energy = (Button) findViewById(R.id.energy);
		energy.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					return true;
				}

				if (event.getAction() != MotionEvent.ACTION_UP) {

					return false;
				}

				return true;
			}
		});

		percentage = (TextView) findViewById(R.id.percentage);
		percentage.setText("0");

		seek_bar_percentage = (SeekBar) findViewById(R.id.seek_bar_percentage);
		seek_bar_percentage.setMax(100);
		seek_bar_percentage
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						percentage.setText(Integer.toString(progress));

					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onStopTrackingTouch(SeekBar seekBar) {

						utility.MandaDati(Integer.parseInt(percentage.getText()
								.toString()) + 150);

					}
				});

		label_start = (TextView) findViewById(R.id.label_start);
		label_start.setTextSize(16);

		label_stop = (TextView) findViewById(R.id.label_stop);
		label_stop.setTextSize(16);

		label_pause = (TextView) findViewById(R.id.label_pause);
		label_pause.setTextSize(16);

		time = (TextView) findViewById(R.id.time);

		cap = (Button) findViewById(R.id.cap);
		cap.setPressed(true);

		play = (Button) findViewById(R.id.button_play);
		play.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					utility.writeData("S");

					return true;
				}

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				return true;
			}
		});

		stop = (Button) findViewById(R.id.button_stop);
		stop.setPressed(true);
		label_stop.setTextColor(Color.parseColor("#78d0d2"));
		stop.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					utility.writeData("T");

					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				return true;
			}
		});

		pause = (Button) findViewById(R.id.button_pause);
		pause.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					utility.writeData("P");

					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				return true;
			}
		});

		cap = (Button) findViewById(R.id.cap);
		cap.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (preferences.getBoolean("isPlaying", false))
						utility.writeData("P");

					utility.writeData("C");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				return true;
			}
		});

		res = (Button) findViewById(R.id.res);
		res.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (preferences.getBoolean("isPlaying", false))
						utility.writeData("P");

					utility.writeData("R");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				return true;
			}
		});

		body = (Button) findViewById(R.id.body);
		body.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (preferences.getBoolean("isPlaying", false))
						utility.writeData("P");

					utility.writeData("B");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				return true;
			}
		});

		face = (Button) findViewById(R.id.face);
		face.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (preferences.getBoolean("isPlaying", false))
						utility.writeData("P");

					utility.writeData("F");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				return true;
			}
		});

		zero = (TextView) findViewById(R.id.zero);
		dieci = (TextView) findViewById(R.id.dieci);
		venti = (TextView) findViewById(R.id.venti);
		trenta = (TextView) findViewById(R.id.trenta);
		quaranta = (TextView) findViewById(R.id.quaranta);
		cinquanta = (TextView) findViewById(R.id.cinquanta);
		sessanta = (TextView) findViewById(R.id.sessanta);
		settanta = (TextView) findViewById(R.id.settanta);
		ottanta = (TextView) findViewById(R.id.ottanta);
		novanta = (TextView) findViewById(R.id.novanta);
		cento = (TextView) findViewById(R.id.cento);

		Display display = getWindowManager().getDefaultDisplay();

		int width, height;

		width = display.getWidth();
		height = display.getHeight();

		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float density = getResources().getDisplayMetrics().density;

		int moltiplicativo = 0;
		if (density == 1)
			moltiplicativo = 5;

		if (density == 3)
			moltiplicativo = 2;

		if (density == 1.5)
			moltiplicativo = 4;

		title.setTextSize(width * moltiplicativo / 100);
		title2.setTextSize(width * moltiplicativo / 100);

		int blocco1_dim = (int) (width * 30 / 100 / 2.2);
		face.setWidth(blocco1_dim);
		body.setWidth(blocco1_dim);
		res.setWidth(blocco1_dim);
		cap.setWidth(blocco1_dim);
		// per mantenere le proporzioni: altezza = 35% larghezza
		face.setHeight(blocco1_dim * 35 / 100);
		body.setHeight(blocco1_dim * 35 / 100);
		res.setHeight(blocco1_dim * 35 / 100);
		cap.setHeight(blocco1_dim * 35 / 100);

		final int blocco2_dim = (int) (width * 50 / 100 / 5);
		label_start.setWidth(blocco2_dim);
		label_stop.setWidth(blocco2_dim);
		label_pause.setWidth(blocco2_dim);
		// label_start.setTextSize(width * moltiplicativo / 100 / 2);
		// label_stop.setTextSize(width * moltiplicativo / 100 / 2);
		// label_pause.setTextSize(width * moltiplicativo / 100 / 2);

		menu = (Button) findViewById(R.id.menu);
		menu.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (!preferences.getBoolean("isMenu", false)) {

						utility.writeData("a");

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}

						preferences.edit().putBoolean("isMenu", true).commit();
						Intent intent = new Intent(Main_Activity.this,
								Menu.class);
						startActivityForResult(intent, REQUEST_CODE_TEST);
					}
					return true;
				}

				if (event.getAction() != MotionEvent.ACTION_UP) {

					return false;
				}

				return true;
			}
		});

		menu.setWidth(blocco2_dim);
		menu.setHeight(blocco2_dim);

		percentage.setTextSize(height / 2 * 20 / 100 / density);
		percentuale_simbolo.setTextSize(height / 2 * 20 / 100 / density);
		duty.setTextSize(height / 2 * 20 / 100 / density / 2);
		time.setTextSize(height / 2 * 20 / 100 / density);

		android.view.ViewGroup.LayoutParams param = seek_bar_percentage
				.getLayoutParams();
		param.width = width * 70 / 100;

		int padding = (int) (width * 70 / 100 / (11));

		zero.setWidth(padding);
		dieci.setWidth(padding);
		venti.setWidth(padding);
		trenta.setWidth(padding);
		quaranta.setWidth(padding);
		cinquanta.setWidth(padding);
		sessanta.setWidth(padding);
		settanta.setWidth(padding);
		ottanta.setWidth(padding);
		novanta.setWidth(padding);
		cento.setWidth(padding);

		// energy.setWidth((int) (blocco2_dim * moltiplicativo));
		// energy.setHeight((int) (blocco2_dim * moltiplicativo / 0.40));

		utility.config(this);

		act_string = getIntent().getAction();
		if (-1 != act_string.indexOf("android.intent.action.MAIN")) {
			restorePreference();
		} else if (-1 != act_string
				.indexOf("android.hardware.usb.action.USB_ACCESSORY_ATTACHED")) {
			cleanPreference();
		}

		if (false == bConfiged) {
			bConfiged = true;
			utility.SetConfig();
			savePreference();
		}

		utility.ResumeAccessory(bConfiged);
		// utility.SetConfig();

		thread.start();

		utility.writeData("@");
		utility.writeData("^");
		utility.writeData("a");
		utility.writeData("?");

	}

	protected void cleanPreference() {
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("configed");
		editor.commit();
	}

	protected void savePreference() {
		if (true == bConfiged) {
			preferences.edit().putString("configed", "TRUE").commit();
		} else {
			preferences.edit().putString("configed", "FALSE").commit();
		}

		preferences.edit().putBoolean("exit", false).commit();

	}

	protected void restorePreference() {
		String key_name = preferences.getString("configed", "");
		if (true == key_name.contains("TRUE")) {
			bConfiged = true;
		} else {
			bConfiged = false;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	// @Override
	public void onHomePressed() {
		onBackPressed();
	}

	public void onBackPressed() {
		thread.interrupt();

		utility.DestroyAccessory(true);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.exit(0);

		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		if (2 == utility.ResumeAccessory(bConfiged)) {
			cleanPreference();
			restorePreference();
		}
	}

	@Override
	protected void onStop() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onStop();
	}

}
