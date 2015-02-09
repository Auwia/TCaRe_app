package it.app.tcare;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Main_Activity extends Activity {

	private VerticalSeekBar verticalSeekBar_left, verticalSeekBar_right = null;
	private TextView vsProgress_left, vsProgress_right, title, time,
			revision = null;
	private ImageButton pauseButton, playButton, stopButton = null;
	private Button cap, res, body, face = null;

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

	public void pressBtnStart(View v) {
		writeData("S");

		Log.d("TCARE",
				"Thread lettura attiva su interfaccia: "
						+ uartInterface.thread_lettura_is_alive());

	}

	public void pressBtnStop(View v) {
		writeData("T");

		Log.d("TCARE",
				"Thread lettura attiva su interfaccia: "
						+ uartInterface.thread_lettura_is_alive());

	}

	public void pressBtnPause(View v) {
		writeData("P");

		Log.d("TCARE",
				"Thread lettura attiva su interfaccia: "
						+ uartInterface.thread_lettura_is_alive());

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);

		leggi = true;

		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			System.out.println("ERRORE STRANO QUI!");
			e.printStackTrace();
		}
		revision = (TextView) findViewById(R.id.revision);
		revision.setText(pInfo.versionName);

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

		title = (TextView) findViewById(R.id.title);
		title.setTextSize(30);

		time = (TextView) findViewById(R.id.time);
		time.setTextSize(30);
		time.setTextColor(Color.WHITE);

		cap = (Button) findViewById(R.id.cap);
		cap.setPressed(true);
		cap.setTextColor(Color.parseColor("#015c5f"));

		verticalSeekBar_left = (VerticalSeekBar) findViewById(R.id.vertical_Seekbar_left);
		vsProgress_left = (TextView) findViewById(R.id.vertical_sb_progresstext_left);
		verticalSeekBar_left
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						int MIN = 5, MAX = 100;
						if (progress < MIN) {

							progress = 0;
						}

						if (progress > MAX) {

							progress = 100;
						}

						vsProgress_left.setText(progress + " %");
						vsProgress_left.setTextColor(Color.WHITE);

					}
				});

		verticalSeekBar_right = (VerticalSeekBar) findViewById(R.id.vertical_Seekbar_right);
		vsProgress_right = (TextView) findViewById(R.id.vertical_sb_progresstext_right);
		verticalSeekBar_right
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						int MIN = 5, MAX = 100;
						if (progress < MIN) {

							progress = 0;
						}

						if (progress > MAX) {

							progress = 100;
						}

						vsProgress_right.setText(progress + " KJ");
						vsProgress_right.setTextColor(Color.WHITE);
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
		Log.d("TCARE",
				"writeData: scritto(" + numBytes + "): "
						+ writeBuffer.toString());

	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			readSB.delete(0, readSB.length());

			for (int i = 0; i < actualNumBytes[0]; i++) {
				readBufferToChar[i] = (char) readBuffer[i];
			}
			appendData(readBufferToChar, actualNumBytes[0]);
		}
	};

	public void appendData(char[] data, int len) {
		if (len >= 1) {
			readSB.append(String.copyValueOf(data, 0, len));
			Log.d("TCARE", "RICEVUTO TUTTO(" + len + "): " + readSB.toString());
			utility = new Utility(this);

			if (readSB.toString().trim() != ""
					&& readSB.toString().substring(len - 1, 1) == "S") {
				utility.setC_START(readSB.toString().substring(len - 4 - 1, 2));
				Log.d("TCARE",
						"RICEVUTO 1: "
								+ readSB.toString().substring(len - 1 - 1, 1));
				Log.d("TCARE",
						"RICEVUTO 2: "
								+ readSB.toString().substring(len - 4 - 1, 2));
			}

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
