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

    public String getLoginUser(){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String email = "";
        Cursor userCursor = db.rawQuery("SELECT email FROM " + TABLE_USERS + " WHERE isLoggedIn = 1 LIMIT 1", null);

        if(userCursor.moveToFirst()) {
            email = userCursor.getString(0);
        }

        userCursor.close();

        return email;

    }

    public boolean loginUser(String email) {
        boolean logged = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_USERS + " SET isLoggedIn = 1 WHERE email = \"" + email + "\"");
            logged = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return logged;
    }

    public boolean logoutUser(String email) {
        boolean logout = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_USERS + " SET isLoggedIn = 0 WHERE email = \"" + email + "\"");
            logout = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return logout;
    }

    public boolean updateUserPassword(String email, String newPassword, int bool) {
        if (bool > 1 || bool < 0) {
            return false;
        }
        boolean updateSuccess = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_USERS + " SET password = \"" + newPassword + "\", passwordIsChanged = " + bool + " WHERE email = \"" + email + "\"");
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
