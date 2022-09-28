package cr.ac.ucr.ecci.arceshopping.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import cr.ac.ucr.ecci.arceshopping.model.User;

public class DbUsers extends DbHelper {

    Context context;

    public DbUsers(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertUser(String email, String id, String name,int age, String province, String password){
        long insertId = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("id", id);
            values.put("name", name);
            values.put("age", age);
            values.put("province", province);
            values.put("password", password);

            insertId = db.insert(TABLE_USERS, null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return insertId;
    }

    public User selectUser(String email) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        User user= null;
        Cursor userCursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = \"" + email + "\" LIMIT 1", null);

        if(userCursor.moveToFirst()) {
            user = new User(userCursor.getString(0), userCursor.getString(1), userCursor.getString(2), userCursor.getInt(3), userCursor.getString(4), userCursor.getString(5));
        }

        userCursor.close();

        return user;
    }
}
