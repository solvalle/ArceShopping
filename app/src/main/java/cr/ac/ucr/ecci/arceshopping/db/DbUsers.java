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

    public long insertUser(String email, String id, String name, String path,int age, String province, String password){
        long insertId = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("email", email);
            values.put("id", id);
            values.put("name", name);
            values.put("path",path);
            values.put("age", age);
            values.put("province", province);
            values.put("password", password);

            insertId = db.insert(TABLE_USERS, null, values);

            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return insertId;
    }

    public User selectUser(String email) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        User user = null;
        Cursor userCursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = \"" + email + "\" LIMIT 1", null);

        if(userCursor.moveToFirst()) {
            user = new User(userCursor.getString(0), userCursor.getString(1), userCursor.getString(2),
                    userCursor.getString(3),userCursor.getInt(4), userCursor.getString(5), userCursor.getString(6),
                        userCursor.getInt(7) != 0);
        }

        userCursor.close();

        return user;
    }

    public boolean updateUserPassword(String email, String newPassword) {
        boolean updateSuccess = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_USERS + " SET password = \"" + newPassword + "\", passwordIsChanged = 1 WHERE email = \"" + email + "\"");
            updateSuccess = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
        return updateSuccess;
    }

    public boolean updateUserDetails(String userUpdate) {
        boolean updateSuccess = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        System.out.println(userUpdate);
        try {
            //When an activity or fragment calls this method, it must provide a compatible string, containing all data updated by user.
            db.execSQL("UPDATE " + TABLE_USERS + userUpdate);
            updateSuccess = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
        return updateSuccess;
    }
}
