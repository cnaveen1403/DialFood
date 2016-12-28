package grk.impala.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Samsung on 6/13/2015.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String TABLE_ITEM = "item_tbl";
    private static final String TABLE_ORDER = "order_tbl";

    public DataBaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ItemTbl.CREATE_ITEM_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("DB Update", "Upgrading database from version " + oldVersion + " to " + newVersion);
    }
}
