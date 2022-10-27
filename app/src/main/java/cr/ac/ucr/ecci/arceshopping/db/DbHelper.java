package cr.ac.ucr.ecci.arceshopping.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "arceshopping.db";
    protected static final String TABLE_USERS= "t_users";
    protected static final String TABLE_SHOPPINGCART= "t_shoppingCart";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_USERS + "(" +
                "email VARCHAR(50) PRIMARY KEY, " +
                "id VARCHAR(30) NOT NULL, " +
                "name VARCHAR(50) NOT NULL, " +
                "path VARCHAR(50) NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "province VARCHAR(15) NOT NULL, " +
                "password VARCHAR(50) NOT NULL, " +
                "passwordIsChanged BOOL DEFAULT 0, " +
                "isLoggedIn BOOL DEFAULT 0)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SHOPPINGCART + "(" +
                "userEmail VARCHAR(50), " +
                "productId INTEGER, " +
                "quantity INTEGER NOT NULL, " +
                "price INTEGER NOT NULL, " +
                "PRIMARY KEY(userEmail, productId), " +
                "FOREIGN KEY(userEmail) REFERENCES " + TABLE_USERS + "(email))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_SHOPPINGCART);
        onCreate(sqLiteDatabase);
    }
}
