package cr.ac.ucr.ecci.arceshopping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.Dictionary;


public class DbShoppingCart extends DbHelper {
    Context context;

    public DbShoppingCart(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertProduct(String userEmail, int productId, int quantity){
        long insertId = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("userEmail", userEmail);
            values.put("productId", productId);
            values.put("quantity", quantity);

            insertId = db.insert(TABLE_SHOPPINGCART, null, values);
            if (insertId == 0 )
                insertId = updateUserShoppingCart(userEmail, productId, quantity) ? 1 : 0 ;

            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return insertId;
    }

    public Dictionary<Integer, Integer> selectUserShoppingCart(String userEmail) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Dictionary<Integer, Integer> shoppingCart = null;
        Cursor shoppingCartCursor = db.rawQuery("SELECT * FROM " + TABLE_SHOPPINGCART + " WHERE userEmail = \"" + userEmail + "\" ", null);

        if(shoppingCartCursor.moveToFirst()) {
            do {
                shoppingCart.put(shoppingCartCursor.getInt(1), shoppingCartCursor.getInt(2));
            } while (shoppingCartCursor.moveToNext());
        }

        shoppingCartCursor.close();

        return shoppingCart;
    }

    public boolean updateUserShoppingCart(String userEmail, int productId, int quantity) {
        boolean updateSuccess = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_SHOPPINGCART + " SET quantity = " + quantity + " WHERE userEmail = \"" + userEmail + "\" AND productId = " + productId);
            updateSuccess = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
        return updateSuccess;
    }
}
