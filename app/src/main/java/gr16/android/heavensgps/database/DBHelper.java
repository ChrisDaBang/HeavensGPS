package gr16.android.heavensgps.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ChrisPCBeast on 07-05-2017.
 */

public class DBHelper extends SQLiteOpenHelper
{
    private static String DB_NAME = "HEAVENSGPS.db";
    private static int DB_VERSION = 2;
    // Make a String per table, named TBL[NAME] as done under here.
    private static String DB_TABLELOCATION_CREATION = "CREATE TABLE IF NOT EXISTS `TBLLOCATION` (\n" +
            "  `locationid` INTEGER PRIMARY KEY,\n" +
            "  `latitude` REAL,\n" +
            "  `longitude` REAL,\n" +
            "  `date` TEXT\n" +
            ");";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_TABLELOCATION_CREATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //todo
    }
}
