package com.example.spisokdel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper // встроенная субд
{
    private static final String db_name = "task_manager";
    private static final int db_version = 1;

    private static final String db_table = "task";
    private static final String db_column = "taskName";

    public DataBase(@Nullable Context context)
    {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) // к базе данных уже подключены
    {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s " +
                "TEXT NOT NULL);", db_table, db_column); // подставляется в %s
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        String query = String.format("DELETE TABLE IF EXISTS %s", db_table);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertData(String task)
    {
        SQLiteDatabase db = this.getWritableDatabase(); // получаем базу данных и можем ее изменять
        ContentValues values = new ContentValues();
        values.put(db_column, task);
        db.insertWithOnConflict(db_table, null, values, SQLiteDatabase.CONFLICT_REPLACE); // помещаем в табличку
        // конфликт реплейс, если такая запись есть, переписывает ее
    }

    public void deleteData(String taskname)
    {
        SQLiteDatabase db = this.getWritableDatabase(); // получаем базу данных и можем ее изменять
        db.delete(db_table, db_column + " = ?", new String[] {taskname}); // если оставить "" удалит все записи
        // удаляем то taskName которое такое же как taskname
        db.close();
    }

    public ArrayList<String> getAllTask()
    {
        ArrayList<String> allTask = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // только считываем
        Cursor cursor = db.query(db_table, new String[] {db_column}, null, null, null,
        null, null);
        while (cursor.moveToNext()) // позволяет поочередно брать записи
        {
            int index = cursor.getColumnIndex(db_column);
            allTask.add(cursor.getString(index));
        }
        cursor.close(); // особый класс курсор
        db.close();
        return allTask;
    }

}
