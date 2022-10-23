package cr.ac.ucr.ecci.arceshopping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.HashMap;


public class DbShoppingCart extends DbHelper {
    Context context;

    public DbShoppingCart(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertProduct(String userEmail, int productId, int quantity, int price){
        long insertId = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            HashMap<Integer, Integer> shoppingCart = selectUserShoppingCart(userEmail);
            System.out.println("size: " + shoppingCart.get(productId) );
            if(shoppingCart.get(productId) == null ) {
                System.out.println("Insertando");
                ContentValues values = new ContentValues();
                values.put("userEmail", userEmail);
                values.put("productId", productId);
                values.put("quantity", quantity);
                values.put("price", price);

                insertId = db.insert(TABLE_SHOPPINGCART, null, values);

            } else {
                System.out.println("Actualizando");
                insertId = increaseItemQuantity(userEmail, productId, quantity) ? 1 : 0 ;
            }
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return insertId;
    }

    public HashMap<Integer, Integer> selectUserShoppingCart(String userEmail) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        HashMap<Integer, Integer> shoppingCart = new HashMap<Integer, Integer>();

        Cursor shoppingCartCursor = db.rawQuery("SELECT * FROM " + TABLE_SHOPPINGCART + " WHERE userEmail = \"" + userEmail + "\" ", null);

        if(shoppingCartCursor.moveToFirst()) {
            do {
                shoppingCart.put(shoppingCartCursor.getInt(1), shoppingCartCursor.getInt(2));
            } while (shoppingCartCursor.moveToNext());
        }

        shoppingCartCursor.close();

        return shoppingCart;
    }

    public boolean deleteUserCart(String userEmail) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean deleted = db.delete(TABLE_SHOPPINGCART, "userEmail = \"" + userEmail + "\"", null) > 0;
        db.close();

        return deleted;
    }

    public int getTotalPriceOfUserShoppingCart(String userEmail) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int totalPrice = 0;

        Cursor totalPriceCursor = db.rawQuery("SELECT SUM(quantity * price) FROM " + TABLE_SHOPPINGCART + " WHERE userEmail = \"" + userEmail + "\" ", null);

        if(totalPriceCursor.moveToFirst())
            totalPrice = totalPriceCursor.getInt(0);

        totalPriceCursor.close();

        return totalPrice;
    }

    public boolean increaseItemQuantity(String userEmail, int productId, int quantity) {
        boolean updateSuccess = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_SHOPPINGCART + " SET quantity = (quantity+" + quantity + ") WHERE userEmail = \"" + userEmail + "\" AND productId = " + productId);
            updateSuccess = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
        return updateSuccess;
    }
}
