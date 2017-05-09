package gr16.android.heavensgps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gr16.android.heavensgps.application.PointInTime;

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

    public List<PointInTime> getAllLocations() throws ParseException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query("TBLLOCATION", new String[]{"latitude", "longitude", "date"}, null, null, null, null, "locationid DESC");
        List<PointInTime> locations = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        while(result.moveToNext())
        {
            Date date = formatter.parse(result.getString(2));
            locations.add(new PointInTime(result.getDouble(0), result.getDouble(1), date));
        }

        return locations;
    }
}
