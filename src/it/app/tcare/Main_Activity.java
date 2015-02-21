package it.app.tcare;

import java.sql.Timestamp;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Main_Activity extends Activity {

	private SeekBar seek_bar_percentage;
	private TextView time, revision, tv, label_start, label_pause, label_stop,
			percentage;

	private Button play, stop, pause, cap, res, body, face, reset, energy,
			set_value, menu, continuos;
	private ImageButton frequency;

	public FT311UARTInterface uartInterface;
	public handler_thread handlerThread;

	private int[] actualNumBytes;
	private char[] readBufferToChar;
	private byte[] writeBuffer, readBuffer;

	private StringBuffer readSB = new StringBuffer();

	public boolean leggi;

	private Utility utility;

	ProgressDialog barProgressDialog;
	Handler updateBarHandler;

	@Override
	protected void onResume() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		uartInterface.ResumeAccessory();
		leggi = true;
	}

	@Override
	protected void onPause() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		uartInterface.DestroyAccessory(true);
		uartInterface.finish();
		leggi = false;
		finish();
		System.exit(0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		uartInterface.DestroyAccessory(true);
		uartInterface.finish();
		leggi = false;
		finish();
		System.exit(0);
	}

	@Override
	protected void onDestroy() {
		uartInterface.DestroyAccessory(true);
		uartInterface.finish();
		super.onDestroy();
		leggi = false;
		finish();
		System.exit(0);
	}

	// @Override
	public void onHomePressed() {
		onBackPressed();
	}

	public void onBackPressed() {
		super.onBackPressed();
		finish();
		System.exit(0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);

		leggi = true;

		frequency = (ImageButton) findViewById(R.id.frequency);
		frequency.setTag(R.drawable.button_457);
		frequency.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				switch ((Integer) frequency.getTag()) {
				case R.drawable.button_457:
					frequency.setTag(R.drawable.button_571);
					frequency.setImageResource(R.drawable.button_571);
					break;
				case R.drawable.button_571:
					frequency.setTag(R.drawable.button_714);
					frequency.setImageResource(R.drawable.button_714);
					break;
				case R.drawable.button_714:
					frequency.setTag(R.drawable.button_145);
					frequency.setImageResource(R.drawable.button_145);
					break;
				case R.drawable.button_145:
					frequency.setTag(R.drawable.button_457);
					frequency.setImageResource(R.drawable.button_457);
					break;

				}
			}

		});

		menu = (Button) findViewById(R.id.menu);
		menu.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					return true;
				}

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {

					return false;
				}

				if (menu.isPressed())
					menu.setPressed(false);
				else
					menu.setPressed(true);

				return true;
			}
		});

		continuos = (Button) findViewById(R.id.button_continuos);
		continuos.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					return true;
				}

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {

					return false;
				}

				if (continuos.isPressed())
					continuos.setPressed(false);
				else
					continuos.setPressed(true);

				return true;
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

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {

					return false;
				}

				if (energy.isPressed())
					energy.setPressed(false);
				else
					energy.setPressed(true);

				return true;
			}
		});

		set_value = (Button) findViewById(R.id.set_value);
		set_value.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					return true;
				}

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {

					return false;
				}

				if (set_value.isPressed())
					set_value.setPressed(false);
				else
					set_value.setPressed(true);

				return true;
			}
		});

		percentage = (TextView) findViewById(R.id.percentage);
		percentage.setText("0");

		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			System.out.println("ERRORE STRANO QUI!");
			e.printStackTrace();
		}
		revision = (TextView) findViewById(R.id.revision);
		revision.setText(pInfo.versionName);

		tv = (TextView) findViewById(R.id.tv);

		seek_bar_percentage = (SeekBar) findViewById(R.id.seek_bar_percentage);
		seek_bar_percentage.setMax(100);
		seek_bar_percentage
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						percentage.setText(Integer.toString(progress));

					}

					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});

		uartInterface = new FT311UARTInterface(this, null);
		updateBarHandler = new Handler();
		launchBarDialog(null);
		uartInterface.SetConfig(19200, (byte) 8, (byte) 1, (byte) 0, (byte) 0);

		writeBuffer = new byte[64];
		readBuffer = new byte[4096];
		readBufferToChar = new char[4096];
		actualNumBytes = new int[1];

		handlerThread = new handler_thread(handler);
		handlerThread.start();

		label_start = (TextView) findViewById(R.id.label_start);
		label_start.setTextSize(18);

		label_stop = (TextView) findViewById(R.id.label_stop);
		label_stop.setTextSize(18);

		label_pause = (TextView) findViewById(R.id.label_pause);
		label_pause.setTextSize(18);

		time = (TextView) findViewById(R.id.time);

		cap = (Button) findViewById(R.id.cap);
		cap.setPressed(true);

		reset = (Button) findViewById(R.id.button_reset);
		reset.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					for (int i = 0; i < 20000; i += 100) {
						uartInterface.SetConfig(i, (byte) 8, (byte) 1,
								(byte) 0, (byte) 0);
						Log.d("TCARE", "SetConfig: " + i);
						writeData("S");
					}
					return true;
				}

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				;

				return true;
			}
		});

		play = (Button) findViewById(R.id.button_play);
		play.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					label_start.setTextColor(Color.parseColor("#015c5f"));
					label_stop.setTextColor(Color.WHITE);
					label_pause.setTextColor(Color.WHITE);
					writeData("S");
					return true;
				}

				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				play.setPressed(true);
				play.setTextColor(Color.parseColor("#015c5f"));
				pause.setPressed(false);
				stop.setPressed(false);
				return true;
			}
		});

		stop = (Button) findViewById(R.id.button_stop);
		stop.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					label_stop.setTextColor(Color.parseColor("#015c5f"));
					label_start.setTextColor(Color.WHITE);
					label_pause.setTextColor(Color.WHITE);
					writeData("T");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}

				stop.setPressed(true);
				stop.setTextColor(Color.parseColor("#015c5f"));
				play.setPressed(false);
				pause.setPressed(false);
				return true;
			}
		});

		pause = (Button) findViewById(R.id.button_pause);
		pause.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					label_pause.setTextColor(Color.parseColor("#015c5f"));
					label_start.setTextColor(Color.WHITE);
					label_stop.setTextColor(Color.WHITE);
					writeData("P");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				pause.setPressed(true);
				play.setPressed(false);
				stop.setPressed(false);
				return true;
			}
		});

		cap = (Button) findViewById(R.id.cap);
		cap.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					writeData("C");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				;
				cap.setPressed(true);
				res.setPressed(false);
				body.setPressed(false);
				face.setPressed(false);
				return true;
			}
		});

		res = (Button) findViewById(R.id.res);
		res.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					writeData("R");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				;
				res.setPressed(true);
				cap.setPressed(false);
				body.setPressed(false);
				face.setPressed(false);
				return true;
			}
		});

		body = (Button) findViewById(R.id.body);
		body.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					writeData("B");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				;
				body.setPressed(true);
				cap.setPressed(false);
				res.setPressed(false);
				face.setPressed(false);
				return true;
			}
		});

		face = (Button) findViewById(R.id.face);
		face.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// show interest in events resulting from ACTION_DOWN
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					writeData("F");
					return true;
				}
				// don't handle event unless its ACTION_UP so "doSomething()"
				// only runs once.
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				;
				face.setPressed(true);
				cap.setPressed(false);
				res.setPressed(false);
				body.setPressed(false);
				return true;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void writeData(String commandString) {

		int numBytes = commandString.length();
		writeBuffer = new byte[64];

		for (int i = 0; i < numBytes; i++) {
			writeBuffer[i] = (byte) commandString.charAt(i);
			Log.d("TCARE", "writeData: scrivo: " + commandString.charAt(i)
					+ " tradotto: " + (byte) commandString.charAt(i));
		}

		uartInterface.SendData(numBytes, writeBuffer);
		Calendar calendar = Calendar.getInstance();
		Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime()
				.getTime());
		Log.d("TCARE", currentTimestamp + ": writeData: scritto(" + numBytes
				+ "): " + writeBuffer.toString());

	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			synchronized (this) {

				for (int i = 0; i < actualNumBytes[0]; i++) {
					readBufferToChar[i] = (char) readBuffer[i];
				}
				appendData(readBufferToChar, actualNumBytes[0]);
			}
		}
	};

	public void appendData(char[] data, int len) {
		if (len >= 1) {
			synchronized (readSB) {

				readSB.append(String.copyValueOf(data, 0, len));
				tv.setText(readSB);

				if (readSB.toString().trim() != ""
						&& readSB.toString().contains("\r")) {
					String[] comandi = readSB.toString().trim().split(" ");
					if (comandi.length == 2) {
						for (int i = 0; i < comandi.length; i++) {
							Log.d("TCARE", "COMANDI[" + i + "]: " + comandi[i]);

							if (comandi[1].equals("<")
									|| comandi[1].equals(">"))
								seek_bar_percentage.setProgress(Integer
										.parseInt(comandi[0], 16));

							readSB.delete(0, readSB.length());
						}
					} else {
						Log.d("TCARE", "Comando errato");
						readSB.delete(0, readSB.length());

					}
				}

			}

			utility = new Utility(this);

			// if (readSB.toString().trim() != ""
			// && readSB.toString().substring(len - 1, 1) == "S") {
			// utility.setC_START(readSB.toString().substring(len - 4 - 1, 2));
			// Log.d("TCARE",
			// "RICEVUTO 1: "
			// + readSB.toString().substring(len - 1 - 1, 1));
			// Log.d("TCARE",
			// "RICEVUTO 2: "
			// + readSB.toString().substring(len - 4 - 1, 2));
			// }

		} else {
			Log.d("TCARE", "RICEVUTO: buffer vuoto");
		}

	}

	private class handler_thread extends Thread {
		Handler mHandler;

		/* constructor */
		handler_thread(Handler h) {
			mHandler = h;
		}

		public void run() {
			Message msg;

			while (leggi) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Log.d("TCARE",
							"handler_thread: lettura thread lettura generico");
					e.printStackTrace();
				}

				byte status = uartInterface.ReadData(4096, readBuffer,
						actualNumBytes);

				if (status == 0x00 && actualNumBytes[0] > 0) {
					msg = mHandler.obtainMessage();
					mHandler.sendMessage(msg);

				}

			}

			Log.d("TCARE", "handler_thread: fine lettura");
		}
	}

	public void launchBarDialog(View view) {

		barProgressDialog = new ProgressDialog(Main_Activity.this);
		barProgressDialog.setTitle("Loading driver...");
		barProgressDialog.setMessage("work in progress ...");
		barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setProgress(0);
		barProgressDialog.setMax(20);
		barProgressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (barProgressDialog.getProgress() <= barProgressDialog
							.getMax()) {
						Thread.sleep(200);
						updateBarHandler.post(new Runnable() {
							public void run() {
								barProgressDialog.incrementProgressBy(2);
							}
						});
						if (barProgressDialog.getProgress() == barProgressDialog
								.getMax()) {
							barProgressDialog.dismiss();
						}
					}
				} catch (Exception e) {
					System.out.println("ERRORE STRANO QUI!");
					e.printStackTrace();
				}
			}
		}).start();
	}
}
