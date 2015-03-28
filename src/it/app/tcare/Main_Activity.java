package it.app.tcare;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Main_Activity extends Activity {

	private TextView label_start, label_pause, label_stop, percentage, zero,
			dieci, venti, trenta, quaranta, cinquanta, sessanta, settanta,
			ottanta, novanta, cento;
	private Button play, stop, pause, cap, res, body, face, menu, frequency,
			continuos;
	private SeekBar seek_bar_percentage;

	public static Utility utility;

	private static SharedPreferences preferences;

	public static Activity activity;

	private static final int REQUEST_CODE_TEST = 0;

	public static boolean start_in_progress = false;
	private boolean bConfiged = false;

	public String act_string;

	public static int exit = 0;

	// VARIABILI DATA BASE
	private static final String DATABASE_NAME = "TCaReDB.db";
	private static SQLiteDatabase database;
	private TCaReDataSource datasource;

	public static final Handler handler_reset_work_time_db = new Handler() {

		public void handleMessage(Message msg) {

			String query = "update WORK_TIME set WORK_FROM=0;";

			Log.d("TCARE", query);
			database.execSQL(query);
		}
	};

	public static final Handler handler_save_settings_db = new Handler() {

		public void handleMessage(Message msg) {

			if (preferences.getBoolean("isSmart", false)) {
				Log.d("TCARE", "update SETTINGS set SMART=1, PHYSIO=0;");
				database.execSQL("update SETTINGS set SMART=1, PHYSIO=0;");
			}

			if (preferences.getBoolean("isPhysio", false)) {
				Log.d("TCARE", "update SETTINGS set SMART=0, PHYSIO=1;");
				database.execSQL("update SETTINGS set SMART=0, PHYSIO=1;");
			}

			String query = "update SETTINGS set LANGUAGE='"
					+ preferences.getString("language", "en") + "';";
			Log.d("TCARE", query);
			database.execSQL(query);
		}
	};

	public static final Handler handler_save_serial_number_db = new Handler() {

		public void handleMessage(Message msg) {

			Log.d("TCARE",
					"update SETTINGS set SERIAL_NUMBER='"
							+ preferences.getString("serial_number",
									"SN DEFAULT") + "';");
			database.execSQL("update SETTINGS set SERIAL_NUMBER='"
					+ preferences.getString("serial_number", "SN DEFAULT")
					+ "';");
		}
	};

	public static Thread thread = new Thread() {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					start_in_progress = false;
				}
				utility.writeData("W");
				exit += 1;
				if (start_in_progress) {
					database.execSQL("update WORK_TIME set WORK_FROM=WORK_FROM+1;");
				}

				if (exit > 5) {

					Settings.System.putInt(activity.getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 1);
					exit = 0;

				} else {
					Settings.System.putInt(activity.getContentResolver(),
							Settings.System.SCREEN_OFF_TIMEOUT, 1800000);

					// WakeLock screenLock = ((PowerManager) activity
					// .getSystemService(POWER_SERVICE))
					// .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
					// | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
					// screenLock.acquire();
					//
					// // later
					// screenLock.release();
				}
			}

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (preferences.getBoolean("isSmart", false)) {
			cap.setVisibility(View.INVISIBLE);
			res.setVisibility(View.INVISIBLE);
		}

		if (preferences.getBoolean("isPhysio", false)) {
			cap.setVisibility(View.VISIBLE);
			res.setVisibility(View.VISIBLE);
		}

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
		datasource.close();
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		utility.DestroyAccessory(bConfiged);

		System.exit(0);

		super.onDestroy();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		find_objects();

		initialize_variables();

		load_db();

		load_preferences();

		load_language();

		create_button_event();

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

		// utility.ResumeAccessory(bConfiged);
		utility.writeData("@");
		utility.writeData("^");
		utility.writeData("a");
		utility.writeData("?");

		if (!thread.isAlive())
			thread.start();

	}

	private void initialize_variables() {

		activity = this;

		utility = new Utility(this);
		utility.config(this);

	}

	private void create_button_event() {
		continuos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (preferences.getBoolean("isPlaying", false)) {
					utility.writeData("P");
				}

				if (preferences.getBoolean("isContinuos", false)) {
					if (preferences.getInt("hz", 1) == 0) {
						utility.writeData("1");
					} else {
						utility.writeData(String.valueOf(preferences.getInt(
								"hz", 1)));
					}
				}

				if (preferences.getBoolean("isPulsed", false)) {
					utility.writeData("0");
				}
			}
		});

		frequency.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (preferences.getBoolean("isPlaying", false)) {
					utility.writeData("P");
				}

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

		seek_bar_percentage
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						percentage.setText(Integer.toString(progress));

					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onStopTrackingTouch(SeekBar seekBar) {

						if (!utility.MandaDati(Integer.parseInt(percentage
								.getText().toString()) + 150)) {
							// Se la scheda è scollegata porta la barra a zero
							seek_bar_percentage.setProgress(0);
							percentage.setText("0");
						}

					}
				});

		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				utility.writeData("S");
			}
		});

		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				utility.writeData("T");
			}
		});

		pause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				utility.writeData("P");
			}
		});

		cap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (preferences.getBoolean("isPlaying", false))
					utility.writeData("P");

				utility.writeData("C");
			}
		});

		res.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (preferences.getBoolean("isPlaying", false))
					utility.writeData("P");

				utility.writeData("R");
			}
		});

		body.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (preferences.getBoolean("isPlaying", false))
					utility.writeData("P");

				utility.writeData("B");
			}
		});

		face.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (preferences.getBoolean("isPlaying", false))
					utility.writeData("P");

				utility.writeData("F");
			}
		});

		menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				utility.writeData("a");

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}

				preferences.edit().putBoolean("isMenu", true).commit();
				Intent intent = new Intent(Main_Activity.this, Menu.class);
				startActivityForResult(intent, REQUEST_CODE_TEST);

				String[] result = utility.db_select(database, "WORK_TIME",
						new String[] { "WORK_FROM" });

				preferences.edit()
						.putInt("work_time", Integer.parseInt(result[0]))
						.commit();

			}
		});
	}

	private void load_language() {
		Resources resource = getResources();
		DisplayMetrics dm = resource.getDisplayMetrics();
		android.content.res.Configuration conf = resource.getConfiguration();
		conf.locale = new Locale(preferences.getString("language", "en"));
		resource.updateConfiguration(conf, dm);
	}

	private void load_db() {
		database = openOrCreateDatabase(DATABASE_NAME,
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		datasource = new TCaReDataSource(this);
		datasource.open();

	}

	private void load_preferences() {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		preferences.edit().putBoolean("isMenu", false).commit();

		String[] result = utility.db_select(database, "SETTINGS", new String[] {
				"SMART", "PHYSIO", "SERIAL_NUMBER", "LANGUAGE", "PWD" });

		preferences.edit()
				.putBoolean("isSmart", Integer.parseInt(result[0]) > 0)
				.commit();
		preferences.edit()
				.putBoolean("isPhysio", Integer.parseInt(result[1]) > 0)
				.commit();
		preferences.edit().putString("serial_number", result[2]).commit();
		preferences.edit().putString("language", result[3]).commit();
		preferences.edit().putString("password", result[4]).commit();

		if (preferences.getBoolean("isSmart", false)) {
			cap.setVisibility(View.INVISIBLE);
			res.setVisibility(View.INVISIBLE);
		}

		if (preferences.getBoolean("isPhysio", false)) {
			cap.setVisibility(View.VISIBLE);
			res.setVisibility(View.VISIBLE);
		}
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
		super.onResume();

		datasource.open();

		if (2 == utility.ResumeAccessory(bConfiged)) {
			cleanPreference();
			restorePreference();
		}
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	private void find_objects() {
		setContentView(R.layout.main_activity_layout);

		// BOTTONI
		frequency = (Button) findViewById(R.id.frequency);
		cap = (Button) findViewById(R.id.cap);
		play = (Button) findViewById(R.id.button_play);
		stop = (Button) findViewById(R.id.button_stop);
		pause = (Button) findViewById(R.id.button_pause);
		cap = (Button) findViewById(R.id.cap);
		res = (Button) findViewById(R.id.res);
		body = (Button) findViewById(R.id.body);
		face = (Button) findViewById(R.id.face);
		menu = (Button) findViewById(R.id.menu);
		continuos = (Button) findViewById(R.id.button_continuos);

		// TEXTVIEW
		percentage = (TextView) findViewById(R.id.percentage);

		// SEEKBAR
		seek_bar_percentage = (SeekBar) findViewById(R.id.seek_bar_percentage);
	}

}
