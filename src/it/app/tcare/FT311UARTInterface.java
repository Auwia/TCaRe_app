//User must modify the below package with their package name
package it.app.tcare;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

/****************************** FT311 GPIO interface class ******************************************/
public class FT311UARTInterface extends Activity {

	private static final String ACTION_USB_PERMISSION = "it.app.tcare.USB_PERMISSION";
	public UsbManager usbmanager;
	public UsbAccessory usbaccessory;
	public PendingIntent mPermissionIntent;
	public ParcelFileDescriptor filedescriptor = null;
	public FileInputStream inputstream = null;
	public FileOutputStream outputstream = null;
	public boolean mPermissionRequestPending = false;
	public read_thread readThread;

	private byte[] usbdata;
	private byte[] writeusbdata;
	private byte[] readBuffer; /* circular buffer */
	private int readcount;
	private int totalBytes;
	private int writeIndex;
	private int readIndex;
	private byte status;
	final int maxnumbytes = 65536;

	public boolean datareceived = false;

	public boolean accessory_attached = false;

	public Context global_context;

	public static String ManufacturerString = "mManufacturer=FTDI";
	public static String ModelString1 = "mModel=FTDIUARTDemo";
	public static String ModelString2 = "mModel=Android Accessory FT312D";
	public static String VersionString = "mVersion=1.0";

	public SharedPreferences intsharePrefSettings;

	/* constructor */
	public FT311UARTInterface(Context context,
			SharedPreferences sharePrefSettings) {
		super();
		global_context = context;
		intsharePrefSettings = sharePrefSettings;
		/* shall we start a thread here or what */
		usbdata = new byte[256];
		writeusbdata = new byte[256];

		/* 128(make it 256, but looks like bytes should be enough) */
		readBuffer = new byte[maxnumbytes];

		readIndex = 0;
		writeIndex = 0;
		/*********************** USB handling ******************************************/

		usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		Log.d("TCARE", "usbmanager" + usbmanager);
		mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		context.registerReceiver(mUsbReceiver, filter);

		inputstream = null;
		outputstream = null;

	}

	public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity,
			byte flowControl) {

		/* prepare the baud rate buffer */
		writeusbdata[0] = (byte) baud;
		writeusbdata[1] = (byte) (baud >> 8);
		writeusbdata[2] = (byte) (baud >> 16);
		writeusbdata[3] = (byte) (baud >> 24);

		/* data bits */
		writeusbdata[4] = dataBits;
		/* stop bits */
		writeusbdata[5] = stopBits;
		/* parity */
		writeusbdata[6] = parity;
		/* flow control */
		writeusbdata[7] = flowControl;

		/* send the UART configuration packet */
		SendPacket((int) 8);
	}

	/* write data */
	public byte SendData(int numBytes, byte[] buffer) {
		status = 0x00; /* success by default */
		/*
		 * if num bytes are more than maximum limit
		 */
		if (numBytes < 1) {
			/* return the status with the error in the command */
			Log.e("TCARE", "SendData: numero di byte nullo o negativo");
			return status;
		}

		/* check for maximum limit */
		if (numBytes > 256) {
			numBytes = 256;
			Log.e("TCARE", "SendData: numero di byte superiore a 256byte");
		}

		/* prepare the packet to be sent */
		for (int count = 0; count < numBytes; count++) {
			writeusbdata[count] = buffer[count];
			Log.d("TCARE", "SendData: preparo array con(" + numBytes + "): "
					+ buffer[count]);

		}

		if (numBytes != 64) {
			SendPacket(numBytes);
		} else {
			byte temp = writeusbdata[63];
			SendPacket(63);
			writeusbdata[0] = temp;
			SendPacket(1);
		}

		return status;
	}

	/* read data */
	public byte ReadData(int numBytes, byte[] buffer, int[] actualNumBytes) {
		status = 0x00; /* success by default */

		/* should be at least one byte to read */
		if ((numBytes < 1) || (totalBytes == 0)) {
			actualNumBytes[0] = 0;
			status = 0x01;
			return status;
		}

		/* check for max limit */
		if (numBytes > totalBytes)
			numBytes = totalBytes;

		/* update the number of bytes available */
		totalBytes -= numBytes;

		actualNumBytes[0] = numBytes;

		/* copy to the user buffer */
		for (int count = 0; count < numBytes; count++) {
			buffer[count] = readBuffer[readIndex];
			readIndex++;
			/*
			 * shouldnt read more than what is there in the buffer, so no need
			 * to check the overflow
			 */
			readIndex %= maxnumbytes;
		}
		return status;
	}

	/* method to send on USB */
	private void SendPacket(int numBytes) {
		try {
			if (outputstream != null) {
				outputstream.write(writeusbdata, 0, numBytes);
				outputstream.flush();
				for (int i = 0; i < numBytes; i++)
					Log.d("TCARE", "SendPacket: scrittura eseguita(" + numBytes
							+ "): " + writeusbdata[i]);
			} else {
				Log.i("TCARE", "SendPacket: stream di scrittura chiuso");
			}
		} catch (IOException e) {
			Log.e("TCARE", "SendPacket: errore generico");
			DestroyAccessory(true);
			CloseAccessory();
			e.printStackTrace();
		}
	}

	/* resume accessory */
	public int ResumeAccessory() {
		// Intent intent = getIntent();
		if (inputstream != null && outputstream != null) {
			Log.i("TCARE", "ResumeAccessory: stream I/O già aperti");
			return 1;
		}

		UsbAccessory[] accessories = usbmanager.getAccessoryList();
		if (accessories != null) {
			Log.d("TCARE", "ResumeAccessory: accessori trovati");
		} else {
			// return 2 for accessory detached case
			// Log.e(">>@@","ResumeAccessory RETURN 2 (accessories == null)");
			Log.e("TCARE", "ResumeAccessory: accessori non trovati");
			accessory_attached = false;
			return 2;
		}

		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (-1 == accessory.toString().indexOf(ManufacturerString)) {
				Log.e("TCARE", "ResumeAccessory: Manufacturer is not matched!");
				return 1;
			}

			if (-1 == accessory.toString().indexOf(ModelString1)
					&& -1 == accessory.toString().indexOf(ModelString2)) {
				Log.e("TCARE", "ResumeAccessory: Model is not matched!");
				return 1;
			}

			if (-1 == accessory.toString().indexOf(VersionString)) {
				Log.e("TCARE", "ResumeAccessory: Version is not matched!");
				return 1;
			}

			accessory_attached = true;

			if (usbmanager.hasPermission(accessory)) {
				Log.d("TCARE",
						"ResumeAccessory: permessi trovati, apro l'accessorio!");
				OpenAccessory(accessory);
			} else {
				Log.i("TCARE",
						"ResumeAccessory: permessi non trovati, apro il pop-up per conferma permessi");
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						Toast.makeText(global_context,
								"Request USB Permission", Toast.LENGTH_SHORT)
								.show();
						usbmanager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					} else {
						Log.e("TCARE",
								"ResumeAccessory: permessi non trovati, il pop-up è già aperto");
					}
				}
			}
		} else {
			Log.e("TCARE", "ResumeAccessory: nessun accessorio trovato");
		}

		return 0;
	}

	/* destroy accessory */
	public void DestroyAccessory(boolean bConfiged) {

		if (true == bConfiged) {
			writeusbdata[0] = 0; // send dummy data for instream.read going
			SendPacket(1);
		} else {
			SetConfig(19200, (byte) 1, (byte) 8, (byte) 0, (byte) 0); // send
																		// default
																		// setting
																		// data
																		// for
																		// config
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				Log.e("TCARE", "DestroyAccessory: sleep (else) errore generico");
				CloseAccessory();
				e.printStackTrace();
			}

			writeusbdata[0] = 0; // send dummy data for instream.read going
			SendPacket(1);
			if (true == accessory_attached) {
				saveDefaultPreference();
			}
		}

		try {
			Thread.sleep(10);
		} catch (Exception e) {
			Log.e("TCARE", "DestroyAccessory: sleep (finale): errore generico");
			CloseAccessory();
			e.printStackTrace();
		}
		CloseAccessory();
	}

	/********************* helper routines *************************************************/

	public void OpenAccessory(UsbAccessory accessory) {
		filedescriptor = usbmanager.openAccessory(accessory);
		if (filedescriptor != null) {
			usbaccessory = accessory;

			FileDescriptor fd = filedescriptor.getFileDescriptor();

			inputstream = new FileInputStream(fd);
			outputstream = new FileOutputStream(fd);
			/* check if any of them are null */
			if (inputstream == null || outputstream == null) {
				Log.e("TCARE", "I/O nullo ");
				return;
			}

			readThread = new read_thread(inputstream);
			readThread.start();
			Log.d("TCARE", "OpenAccessory: stream di lettura avviato");

			Log.d("TCARE", "OpenAccessory: accessorio aperto");
		} else {
			Log.e("TCARE", "OpenAccessory: nessun accessorio trovato");
		}
	}

	private void CloseAccessory() {
		try {
			if (filedescriptor != null)
				filedescriptor.close();

		} catch (IOException e) {
			Log.e("TCARE", "CloseAccessory: I/O exception: errore generico");
			CloseAccessory();
			e.printStackTrace();
		}

		try {
			if (inputstream != null)
				inputstream.close();
		} catch (IOException e) {
			Log.e("TCARE", "CloseAccessory: input stream: errore generico");
			CloseAccessory();
			e.printStackTrace();
		}

		try {
			if (outputstream != null) {
				outputstream.flush();
				outputstream.close();
			}

		} catch (IOException e) {
			Log.e("TCARE", "CloseAccessory: output stream: errore generico");
			CloseAccessory();
			e.printStackTrace();
		}
		/* FIXME, add the notfication also to close the application */

		filedescriptor = null;
		inputstream = null;
		outputstream = null;

		System.exit(0);
	}

	protected void saveDetachPreference() {
		if (intsharePrefSettings != null) {
			intsharePrefSettings.edit().putString("configed", "FALSE").commit();
		}
	}

	protected void saveDefaultPreference() {
		if (intsharePrefSettings != null) {
			intsharePrefSettings.edit().putString("configed", "TRUE").commit();
			intsharePrefSettings.edit().putInt("baudRate", 9600).commit();
			intsharePrefSettings.edit().putInt("stopBit", 1).commit();
			intsharePrefSettings.edit().putInt("dataBit", 8).commit();
			intsharePrefSettings.edit().putInt("parity", 0).commit();
			intsharePrefSettings.edit().putInt("flowControl", 0).commit();
		}
	}

	/*********** USB broadcast receiver *******************************************/
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = (UsbAccessory) intent
							.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						OpenAccessory(accessory);
					} else {
						Toast.makeText(global_context, "Deny USB Permission",
								Toast.LENGTH_SHORT).show();
						Log.e("TCARE", "permission denied for accessory "
								+ accessory);

					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				Log.i("TCARE", "cavo USB disinserito");

				saveDetachPreference();
				DestroyAccessory(true);
				CloseAccessory();
			} else {
				Log.e("TCARE", "USB: errore generico");
				CloseAccessory();
			}
		}
	};

	/* usb input data handler */
	private class read_thread extends Thread {
		FileInputStream instream;

		read_thread(FileInputStream stream) {
			instream = stream;
			this.setPriority(Thread.MAX_PRIORITY);
		}

		public void run() {
			while (true) {
				Log.d("TCARE", "read_thread: lettura in corso");
				while (totalBytes > (maxnumbytes - 256)) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Log.e("TCARE", "read_thread: errore generico");
						DestroyAccessory(true);
						CloseAccessory();
						e.printStackTrace();
					}
				}

				try {
					if (instream != null) {
						readcount = instream.read(usbdata, 0, 256);
						if (readcount > 0) {
							for (int count = 0; count < readcount; count++) {
								readBuffer[writeIndex] = usbdata[count];
								writeIndex++;
								writeIndex %= maxnumbytes;
							}

							if (writeIndex >= readIndex)
								totalBytes = writeIndex - readIndex;
							else
								totalBytes = (maxnumbytes - readIndex)
										+ writeIndex;

							// Log.e(">>@@","totalBytes:"+totalBytes);
						}
					}
				} catch (IOException e) {
					Log.e("TCARE", "read_thread I/O exception: errore generico");
					e.printStackTrace();
					DestroyAccessory(true);
					CloseAccessory();
				}
			}
		}
	}

	public boolean thread_lettura_is_alive() {
		return readThread.isAlive();
	}
}