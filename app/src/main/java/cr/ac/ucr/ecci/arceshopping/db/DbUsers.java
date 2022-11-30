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

    /**
     * Insert a new user in the database
     * @Param email The user's email. This will be the primary key
     * @Param id The user's dni
     * @Param name The user's name
     * @Param path The path of the user's profile picture
     * @Param age The age of the user
     * @Param province The province where the user lives
     * @Param password The user's password
     */
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

    /**
     * Search and return a user from the database
     * @Param email The user's email to be searched
     */
    public User selectUser(String email) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        User user = null;
        Cursor userCursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = \"" + email + "\" LIMIT 1", null);

        if(userCursor.moveToFirst()) {
            user = new User(userCursor.getString(0), userCursor.getString(1), userCursor.getString(2),
                    userCursor.getString(3),userCursor.getInt(4), userCursor.getString(5),
                        userCursor.getInt(7) != 0);
        }

        userCursor.close();

        return user;
    }

    /**
     * Get the logged in user
     */
    public String getLoggedInUser(){
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

    /**
     * Log in to a user
     * @Param email The user's email to be logged
     */
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

    /**
     * Log out a user
     * @Param email The user's email to log out
     */
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

    /**
     * Update the user's password
     * @Param email The user's email
     * @Param newPassword The new password
     * @Param bool New value for the passwordIsChanged flag
     */
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

    /**
     * Update user info
     * @Param userUpdate The query that contains the values to update
     */
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
