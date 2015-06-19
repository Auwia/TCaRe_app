package it.app.dmd_stock_app;

import android.content.Context;
import android.database.SQLException;

public class DMDStockDataSource {

	private DMDStockDB dbHelper;
	
	public DMDStockDataSource(Context context) 
	{
	    dbHelper = new DMDStockDB(context);
	}

	public void open() throws SQLException 
	{
		dbHelper.getWritableDatabase();
	}

	public void close() 
	{
		dbHelper.close();
	}
}
