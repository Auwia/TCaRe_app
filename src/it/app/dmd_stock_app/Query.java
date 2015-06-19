package it.app.dmd_stock_app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Query {

	Context context;

	// VARIABILI DATA BASE
	private static final String DATABASE_NAME = "DMDStockDB.db";
	private static SQLiteDatabase database;
	private DMDStockDataSource datasource;
	private Cursor cur;

	public Query(Context context) {
		this.context = context;

	}

	public String[] select(String table, String[] param_read) {

		database = context.openOrCreateDatabase(DATABASE_NAME,
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		datasource = new DMDStockDataSource(context.getApplicationContext());
		datasource.open();

		cur = database.query(table, param_read, null, null, null, null, null);

		cur.moveToFirst();

		String[] result = new String[param_read.length];

		while (cur.getCount() > 0 && !cur.isAfterLast()) {
			for (int i = 0; i < param_read.length; i++) {
				System.out.println(cur.getString(i));
				result[i] = cur.getString(i);
			}
			cur.moveToNext();
		}
		cur.close();

		return result;
	}

}
