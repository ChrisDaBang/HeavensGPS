package gr16.android.heavensgps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ChrisPCBeast on 07-05-2017.
 */

public class LocationDAO
{
    private DBHelper dbHelper;

    public LocationDAO(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public void saveLocation(double latitude, double longitude)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        Date current = new Date();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        contentValues.put("date", formatter.format(current));
        db.insert("TBLLOCATION", null, contentValues);
    }
}
