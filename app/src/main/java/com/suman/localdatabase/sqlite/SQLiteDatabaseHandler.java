package com.suman.localdatabase.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.suman.localdatabase.sqlite.model.Country;

import java.util.ArrayList;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2; //Database Version
    private static final String DATABASE_NAME = "Counters.db"; //Database Name

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating Table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Country" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT, code TEXT, date TEXT, salary DOUBLE, image BLOG, fav INTEGER, imageURL TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Country");
        onCreate(sqLiteDatabase);
    }

    //count Data
    public int count() {
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from Country";
        cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //load all Data
    public ArrayList<Country> fetchData() {
        ArrayList<Country> countries = new ArrayList<Country>();
        String query = "SELECT * FROM Country";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                countries.add(new Country(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getBlob(5),
                        cursor.getInt(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return countries;
    }

    //Insert new data
    public boolean addEmployee(String name, String department, String join, String salary, byte[] images, int fav, String imageURL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("code", department);
        contentValues.put("date", join);
        contentValues.put("salary", salary);
        contentValues.put("image", images);
        contentValues.put("fav", fav);
        contentValues.put("imageURL", imageURL);
//        contentValues.put(COUNTRY_IMAGE, img);
        long res = db.insert("Country", null, contentValues);
        if (res == -1) return false;
        else return true;
    }

    //get filtered Data
    public ArrayList<Country> getFavData(){
        ArrayList<Country> countries = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM Country where fav = 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                countries.add(new Country(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getBlob(5),
                        cursor.getInt(6),
                        cursor.getString(7)
                ));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return countries;
    }
//    public ArrayList<Country> getSingleData(int id) {
    public Country getSingleData(int id) {
//        ArrayList<Country> countries = new ArrayList<>();
        Country country = null;
//        String query = "SELECT * FROM Country WHERE id = " + id;
        SQLiteDatabase database = this.getReadableDatabase();
//        Cursor cursor = database.rawQuery(query, null);
        Cursor cursor = database.query("Country", new String[]{"id, name, code, date, salary, image, fav, imageURL"}, "id =?", new String[]{String.valueOf(id)}, null, null, null,null);
//        if (cursor.moveToFirst()) {
//            do {
//                Country country = new Country();
//                country.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//                country.cName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                country.cCode = cursor.getString(cursor.getColumnIndexOrThrow("code"));
//                country.cDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//                country.cSalary = cursor.getDouble(cursor.getColumnIndexOrThrow("salary"));
//                countries.add(country);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return countries;
        if (cursor != null){
            cursor.moveToFirst();
            country = new Country(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getBlob(5),
                    cursor.getInt(6),
                    cursor.getString(7)
            );
        }
        return country;
    }

    //update Data
    public boolean updateEmployee(String name, String code, String salary, String id, byte[] images, int fav, String imageURL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("code", code);
        values.put("salary", salary);
        values.put("image", images);
        values.put("fav", fav);
        values.put("imageURL", imageURL);
        db.update("Country", values, "id =?", new String[]{id});
//        String sql = "UPDATE Country " +
//                "SET name = ?," +
//                "code = ?," +
//                "salary = ?" +
//                "WHERE id = ?;";
//        db.execSQL(sql, new String[]{name, code, salary, id});
        db.close();
        return true;
    }

    //delete data
    public int deleteData(int id){
      SQLiteDatabase db = this.getWritableDatabase();
      return db.delete("Country", "id =?", new String[]{String.valueOf(id)});
    }
}
