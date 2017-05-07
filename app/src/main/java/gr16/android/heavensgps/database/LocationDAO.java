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

    public void saveLocation(float latitude, float longitude)
    {
        int locationId = getLatestLocationId();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("locationid", locationId);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", locationId);
        Date current = new Date();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        contentValues.put("date", formatter.format(current));
        db.insert("TBLLOCATION", null, contentValues);
    }

    private int getLatestLocationId() {
        int locationId = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT MAX(locationid) FROM TBLLOCATION", null);
        if(res.getCount() > 0) {
            res.moveToFirst();
            locationId = res.getInt(res.getColumnIndex("MAX(locationid)"));
        }
        return locationId;
    }
}
