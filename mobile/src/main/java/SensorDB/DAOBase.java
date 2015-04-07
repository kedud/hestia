package SensorDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DAOBase
{
    protected final static int VERSION = 1;
    // File name:
    protected final static String NOM = "database_sensors.db";

    protected SQLiteDatabase mDb = null;
    protected MyDataBase mHandler = null;

    public DAOBase(Context pContext) {
        this.mHandler = new MyDataBase(pContext);
    }

    public SQLiteDatabase open() {
        //getWritableDatabase will close it
        mDb = mHandler.getWritableDatabase();
        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }
}