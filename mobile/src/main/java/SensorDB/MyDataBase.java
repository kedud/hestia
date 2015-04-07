package SensorDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDataBase extends SQLiteOpenHelper
{
    public static final String SENSORS_TABLE_NAME = "Sensors";
    public static final String SENSORS_ID = "id";
    public static final String SENSORS_NAME = "name";
    public static final String SENSORS_VALUE = "value";

    List<Sensor> list = new ArrayList<Sensor>();

    public static final String SENSORS_TABLE_CREATE =
            "CREATE TABLE " + SENSORS_TABLE_NAME + " (" +
                    SENSORS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SENSORS_NAME + " TEXT, " +
                    SENSORS_VALUE + " REAL);";

    public static final String SENSORS_TABLE_DROP = "DROP TABLE IF EXISTS " + SENSORS_TABLE_NAME + " ;";

    protected final static int VERSION = 1;

    public MyDataBase(Context context)
    {
        super(context, SENSORS_TABLE_NAME, null, VERSION);
        Log.d("MySQL operations", "BDD created");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SENSORS_TABLE_CREATE);
        Log.d("MySQL operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SENSORS_TABLE_DROP);
        onCreate(db);
    }

    public void putInformation(MyDataBase mdb, String name, float value)
    {
        SQLiteDatabase SQ = mdb.getWritableDatabase();
        ContentValues CV = new ContentValues();
        CV.put(this.SENSORS_NAME, name);
        CV.put(this.SENSORS_VALUE, value);
        long temp = SQ.insert(this.SENSORS_TABLE_NAME, null, CV);
        Log.d("MySQL operations", "One raw inserted");
    }

    public void deleteInformation(MyDataBase mdb, int id)
    {
        SQLiteDatabase bdd = mdb.getWritableDatabase();
        bdd.delete(SENSORS_TABLE_NAME, 0 + " = " + id, null);
    }

    public List<Sensor> getInformation(MyDataBase mdb)
    {
        SQLiteDatabase SQ = mdb.getReadableDatabase();
        String[] columns = {this.SENSORS_ID, this.SENSORS_NAME, this.SENSORS_VALUE};
        Cursor CR = SQ.query(this.SENSORS_TABLE_NAME, columns, null, null, null, null, null);
        return cursorToList(CR);
    }

    private List<Sensor> cursorToList(Cursor c)
    {
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();

        Sensor sens;

        do {
            sens = new Sensor();

            sens.setId(c.getInt(0));
            sens.setName(c.getString(1));
            //System.out.println(sens.getName());
            sens.setValue(c.getFloat(2));
            System.out.println("value" + sens.getValue());

            list.add(sens);

        }while(c.moveToNext());


        c.close();

        return list;
    }




}
