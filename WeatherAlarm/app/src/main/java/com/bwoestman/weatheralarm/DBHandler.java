package com.bwoestman.weatheralarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian Woestman on 3/6/16.
 * Android Programming
 * We 5:30p - 9:20p
 */

/**
 * this class is used to make all the calls to the sqlite database
 */

public class DBHandler extends SQLiteOpenHelper implements AppInfo
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "test.db";
    private static final String TABLE_ALARMS = "alarms";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_RAIN = "rain";
    public static final String COLUMN_ADJUSTMENT = "adjustment";
    public static final String COLUMN_ENABLED = "enabled";

    /**
     * this is the constructor for the database
     * @param context Context
     * @param name String
     * @param factory SQLiteDatabase
     * @param version int
     */

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                     int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db SQLiteDatabase
     */
    @Override

    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_HOUR
                + " INTEGER," + COLUMN_MINUTE + " INTEGER," + COLUMN_RAIN + " INTEGER,"
                + COLUMN_ADJUSTMENT + " INTEGER," + COLUMN_ENABLED + " INTEGER)";

        db.execSQL(CREATE_ALARMS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    /**
     * this method enters an alarm into the database and returns its id
     * @param alarm Alarm
     * @return Long
     */

    public Long addAlarm(Alarm alarm)
    {
        ContentValues values = new ContentValues();

        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_RAIN, alarm.getRain());
        values.put(COLUMN_ADJUSTMENT, alarm.getAdjustment());
        values.put(COLUMN_ENABLED, alarm.getEnabled());

        SQLiteDatabase db = this.getWritableDatabase();
        Long id = db.insert(TABLE_ALARMS, null, values);

        alarm.set_id(id);

        return id;
    }

    /**
     * this method returns a list of all the alarms in the database
     * @return List<Alarm>
     */

    public List<Alarm> getAlarms()
    {
        ArrayList<Alarm> alarms = new ArrayList<>();
        String query = "SELECT _id, hour, minute, rain, adjustment, enabled FROM " +
                TABLE_ALARMS;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Long _id = cursor.getLong(0);
            Integer hour = cursor.getInt(1);
            Integer minute = cursor.getInt(2);
            Integer rain = cursor.getInt(3);
            Integer adjustment = cursor.getInt(4);
            Integer enabled = cursor.getInt(5);

            alarms.add(new Alarm(_id, hour, minute, rain, adjustment, enabled));
            cursor.moveToNext();
        }

        db.close();

        return alarms;
    }

    /**
     * this method returns one alarm from the database for the specified id
     * @param id Long
     * @return Alarm
     */

    public Alarm getAlarm(Long id)
    {
        Cursor cursor;
        SQLiteDatabase db;
        String query;

        db = this.getReadableDatabase();
        query = "SELECT _id, hour, minute, rain, adjustment, enabled FROM " +
                TABLE_ALARMS + " WHERE _id = " + id;

        cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 1)
        {
            cursor.moveToFirst();

            Long _id = cursor.getLong(0);
            Integer hour = cursor.getInt(1);
            Integer minute = cursor.getInt(2);
            Integer rain = cursor.getInt(3);
            Integer adjustment = cursor.getInt(4);
            Integer enabled = cursor.getInt(5);

            return new Alarm(_id, hour, minute, rain, adjustment,
                    enabled);
        }
        cursor.close();
        return null;
    }

    /**
     * this method updates all the fields for a specified alarm
     * @param alarm Alarm
     */

    public void updateAlarm(Alarm alarm)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Long id = alarm.get_id();
        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_RAIN, alarm.getRain());
        values.put(COLUMN_ADJUSTMENT, alarm.getAdjustment());
        values.put(COLUMN_ENABLED, alarm.getEnabled());

        db.update(TABLE_ALARMS, values, "_id=" + id, null);

        db.close();
    }

    /**
     * this method removes a specified alarm from the database
     * @param alarm Alarm
     */

    public void deleteAlarm(Alarm alarm)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_ALARMS + " WHERE _id = " + alarm.get_id();

        db.execSQL(sql);

        db.close();
    }
}
