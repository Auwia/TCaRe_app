package it.app.tcare;

import android.app.Activity;
import android.database.SQLException;

public class TCaReDataSource {

	private TCaReDB dbHelper;

	public TCaReDataSource(Activity activity) {
		dbHelper = new TCaReDB(activity);
	}

	public void open() throws SQLException {
		dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
}
