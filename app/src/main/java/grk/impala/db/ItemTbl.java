package grk.impala.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import grk.impala.util.ConstantUtil;
import grk.impala.model.ItemModel;

/**
 * Created by Samsung on 6/13/2015.
 */
public class ItemTbl {

    public static final String CREATE_ITEM_TBL =
            " CREATE TABLE IF NOT EXISTS item_tbl(menuId INTEGER, itemId INTEGER, itemName TEXT, itemRate TEXT, itemQty INTEGER) ";

    private SQLiteDatabase db = null;
    private Context context = null;
    private DataBaseHandler dataBaseHandler = null;

    public ItemTbl(Context context) {
        this.context = context;
        this.dataBaseHandler = new DataBaseHandler(context, ConstantUtil.DB_NAME, null, ConstantUtil.DB_VERSION);
    }

    public int insertItem(ItemModel itemModel) {
        db = dataBaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("menuId", itemModel.getId());
        values.put("itemId", itemModel.getItemId());
        values.put("itemName", itemModel.getItemName());
        values.put("itemRate", itemModel.getItemRate());
        values.put("itemQty", itemModel.getTotalQty());

        int id = (int) db.insert("item_tbl", null, values);
        db.close();
        return id;
    }

    public void updateItem(ItemModel itemModel) {
        db = dataBaseHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("menuId", itemModel.getId());
        values.put("itemId", itemModel.getItemId());
        values.put("itemRate", itemModel.getItemRate());
        values.put("itemQty", itemModel.getTotalQty());
        db.update("item_tbl", values, "itemId=?", new String[]{String.valueOf(itemModel.getItemId())});
        db.close();
    }

    public void deleteItem(String itemName){
        db = dataBaseHandler.getWritableDatabase();
        db.delete("item_tbl", "itemName=?", new String[]{itemName});
        db.close();
    }

    public void deleteAllItem(){
        db = dataBaseHandler.getWritableDatabase();
        db.delete("item_tbl", null, null);
        db.close();
    }

    public int itemCount() {
        int count = 0;
        String selectQuery = " SELECT itemId FROM item_tbl ";
        db = dataBaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor != null) {
            count = cursor.getCount();
        }
        db.close();
        return count;
    }

    public int itemPresent(int itemId) {
        int count = 0;
        String selectQuery = " SELECT itemId FROM item_tbl WHERE itemId = "+itemId+" ";
        db = dataBaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor != null) {
            count = cursor.getCount();
        }
        db.close();
        return count;
    }

    public ArrayList<ItemModel> selectAllItem() {
        ArrayList<ItemModel> itemList = new ArrayList<ItemModel>();
        db = dataBaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(" SELECT * FROM item_tbl ", null);

        if(cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    ItemModel itemModel = new ItemModel();
                    itemModel.setId(cursor.getInt(cursor.getColumnIndex("menuId")));
                    itemModel.setItemId(cursor.getInt(cursor.getColumnIndex("itemId")));
                    itemModel.setItemName(cursor.getString(cursor.getColumnIndex("itemName")));
                    itemModel.setItemRate(cursor.getString(cursor.getColumnIndex("itemRate")));
                    itemModel.setTotalQty(cursor.getInt(cursor.getColumnIndex("itemQty")));
                    itemList.add(itemModel);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return itemList;
    }
}
