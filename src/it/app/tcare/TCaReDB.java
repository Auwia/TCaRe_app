package it.app.tcare;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TCaReDB extends SQLiteOpenHelper {

	private Utility utility;

	private Activity activity;

	private static final String DATABASE_NAME = "TCaReDB.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_WORK_TIME = "WORK_TIME";
	public static final String TABLE_SETTINGS = "SETTINGS";

	public static final String COLUMN_WORK_FROM = "WORK_FROM";
	public static final String COLUMN_PASSWORD = "PWD";
	public static final String COLUMN_SMART = "SMART";
	public static final String COLUMN_PHYSIO = "PHYSIO";
	public static final String COLUMN_SERIAL_NUMBER = "SERIAL_NUMBER";
	public static final String COLUMN_LANGUAGE = "LANGUAGE";

	private static final String CREATE_TABLE_TABLE_WORK_TIME = "create table "
			+ TABLE_WORK_TIME + "(" + COLUMN_WORK_FROM
			+ " integer primary key NOT NULL DEFAULT 1 " + " );";

	private static final String CREATE_TABLE_TABLE_SETTINGS = "create table "
			+ TABLE_SETTINGS + "(" + COLUMN_SMART + " bit, " + COLUMN_PHYSIO
			+ " bit, " + COLUMN_SERIAL_NUMBER + " VARCHAR(20), "
			+ COLUMN_LANGUAGE + " varchar(2), " + COLUMN_PASSWORD
			+ " VARCHAR(20));";

	public TCaReDB(Activity activity) {
		super(activity, DATABASE_NAME, null, DATABASE_VERSION);

		this.activity = activity;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		utility = new Utility(activity);

		database.execSQL(CREATE_TABLE_TABLE_WORK_TIME);
		database.execSQL(CREATE_TABLE_TABLE_SETTINGS);

		ContentValues row = new ContentValues();
		row.put(COLUMN_WORK_FROM, 1);
		database.beginTransaction();
		database.insert(TABLE_WORK_TIME, null, row);

		row.clear();
		row.put(COLUMN_SMART, 1);
		row.put(COLUMN_PHYSIO, 0);
		row.put(COLUMN_SERIAL_NUMBER, "SN");
		row.put(COLUMN_LANGUAGE, "en");
		row.put(COLUMN_PASSWORD, utility.crypt("260776"));
		database.insert(TABLE_SETTINGS, null, row);
		row.clear();

		database.setTransactionSuccessful();
		database.endTransaction();

	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
	}

	@Override
	public void onDowngrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
	}

}
