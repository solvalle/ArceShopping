package cr.ac.ucr.ecci.arceshopping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.HashMap;


public class DbShoppingCart extends DbHelper {
    Context context;
    static final int LIMIT = 10;

    public DbShoppingCart(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Insert a new product in the user's shopping cart. If that products already exists, call the method increaseItemQuantity()
     * @Param userEmail The user's email
     * @Param productId The id of the new product
     * @Param quantity The quantity of products to add
     * @Param price The product's price
     * @Param stock The product's stock
     */
    public long insertProduct(String userEmail, int productId, int quantity, int price, int stock){
        long insertId = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            HashMap<Integer, Integer> shoppingCart = selectUserShoppingCart(userEmail);
            if(shoppingCart.get(productId) == null ) {
                ContentValues values = new ContentValues();
                values.put("userEmail", userEmail);
                values.put("productId", productId);
                values.put("quantity", quantity);
                values.put("price", price);

                db.insert(TABLE_SHOPPINGCART, null, values);
                insertId = 1;

            } else {
                insertId = increaseItemQuantity(userEmail, productId, quantity, stock);
            }
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return insertId;
    }

    /**
     * Search and return the user shopping cart from the database
     * @Param userEmail The user's email
     */
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

    /**
     * Delete the user shopping cart from the database
     * @Param userEmail The user's email
     */
    public boolean deleteUserCart(String userEmail) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean deleted = db.delete(TABLE_SHOPPINGCART, "userEmail = \"" + userEmail + "\"", null) > 0;
        db.close();

        return deleted;
    }

    /**
     * Delete the user shopping cart from the database
     * @Param userEmail The user's email
     * @Param id The product to be deleted
     */
    public boolean deleteItem(String userEmail, int id) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean deleted = db.delete(TABLE_SHOPPINGCART, "userEmail = \"" + userEmail + "\" " +
                "and productId = " + id , null) > 0;
        db.close();
        return deleted;
    }

    /**
     * Get the total price of the user shopping cart
     * @Param userEmail The user's email
     */
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

    /**
     * Get the quantity of a product in the a user's shopping cart
     * @Param userEmail The user's email
     * @Param productId The id of the product
     */
    public int getItemQuantity(String email, int productId) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int quantity = 0;
        Cursor itemCursor = db.rawQuery("SELECT quantity FROM " + TABLE_SHOPPINGCART +
                        " WHERE userEmail = \"" + email + "\" and productId = " + productId + " LIMIT 1",
                null);

        if(itemCursor.moveToFirst()) {
            quantity = itemCursor.getInt(0);
        }
        itemCursor.close();
        return quantity;
    }

    /**
     * Increase the quantity of a product in the a user's shopping cart
     * @Param userEmail The user's email
     * @Param productId The id of the product
     * @Param productId The quantity to add
     * @Param stock The total sock of the product, to validate availability
     */
    public long increaseItemQuantity(String userEmail, int productId, int quantity, int stock) {
        long updateSuccess = 0;

        if (quantity > 0) {
            int total = getItemQuantity(userEmail, productId) + quantity;
            if (total > stock || total > LIMIT) {
                return 2;
            }
        }

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_SHOPPINGCART + " SET quantity = (quantity+" + quantity + ") " +
                    "WHERE userEmail = \"" + userEmail + "\" AND productId = " + productId);
            updateSuccess = 1;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
        return updateSuccess;
    }
}
