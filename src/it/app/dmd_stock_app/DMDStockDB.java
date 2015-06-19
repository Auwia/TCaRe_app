package it.app.dmd_stock_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DMDStockDB extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "DMDStockDB.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_QUEUE = "QUEUE";
	public static final String TABLE_MOVIMENTI = "MOVIMENTI";
	public static final String TABLE_LETTURA = "LETTURA";
	public static final String TABLE_MAGAZZINIERE = "MAGAZZINIERE";

	public static final String COLUMN_ID_FILE = "ID_FILE";
	public static final String COLUMN_FILE = "FILE";
	public static final String COLUMN_ID_MOVIMENTO = "ID_MOVIMENTO";
	public static final String COLUMN_DATA_MOVIMENTO = "DATA_MOVIMENTO";
	public static final String COLUMN_ID_MAGAZZINIERE = "ID_MAGAZZINIERE";
	public static final String COLUMN_ID_LETTURA = "ID_LETTURA";
	public static final String COLUMN_ETICHETTA = "ETICHETTA";
	public static final String COLUMN_DATA_LETTURA = "DATA_LETTURA";
	public static final String COLUMN_QUANTITA_PRELEVATA = "COLUMN_QUANTITA_PRELEVATA";
	public static final String COLUMN_NOME = "NOME";
	public static final String COLUMN_COGNOME = "COGNOME";

	private static final String CREATE_TABLE_QUEUE = "create table "
			+ TABLE_QUEUE + "(" + COLUMN_ID_FILE
			+ " integer primary key autoincrement NOT NULL, " + COLUMN_FILE
			+ " VARCHAR(50));";

	private static final String CREATE_TABLE_MOVIMENTI = "create table "
			+ TABLE_MOVIMENTI + "(" + COLUMN_ID_MOVIMENTO
			+ " integer primary key autoincrement NOT NULL, "
			+ COLUMN_DATA_MOVIMENTO + " DATA  NOT NULL, "
			+ COLUMN_ID_MAGAZZINIERE + " integer);";

	private static final String CREATE_TABLE_LETTURA = "create table "
			+ TABLE_LETTURA + "(" + COLUMN_ID_LETTURA
			+ " integer primary key autoincrement NOT NULL, "
			+ COLUMN_ETICHETTA + " VARCHAR(100), " + COLUMN_DATA_LETTURA
			+ " DATA NOT NULL, " + COLUMN_QUANTITA_PRELEVATA + " numeric, "
			+ COLUMN_ID_MOVIMENTO + " integer);";

	private static final String CREATE_TABLE_MAGAZZINIERE = "create table "
			+ TABLE_MAGAZZINIERE + "(" + COLUMN_ID_MAGAZZINIERE
			+ " integer primary key autoincrement NOT NULL, " + COLUMN_NOME
			+ " VARCHAR(50), " + COLUMN_COGNOME + " VARCHAR(50));";

	public DMDStockDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		database.execSQL(CREATE_TABLE_QUEUE);
		database.execSQL(CREATE_TABLE_MOVIMENTI);
		database.execSQL(CREATE_TABLE_LETTURA);
		database.execSQL(CREATE_TABLE_MAGAZZINIERE);

		ContentValues row = new ContentValues();
		database.beginTransaction();
		row.put(COLUMN_ID_MAGAZZINIERE, 1);
		row.put(COLUMN_NOME, "MASSIMO");
		row.put(COLUMN_COGNOME, "MANGANIELLO");

		database.insert(TABLE_MAGAZZINIERE, null, row);
		database.setTransactionSuccessful();
		database.endTransaction();

		row.clear();

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
