package com.quac.money;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import okhttp3.internal.Version;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Имя и версия базы данных
    private static final String DB_NAME="bank";
    private  static final  int DB_VERSION=1;

    //Конструктур
    DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase( db, 0,DB_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //updateMyDatabase( db, oldVersion,newVersion);
    }
    public static void insertFinance(SQLiteDatabase db,String name,String price,String type){
        ContentValues financeValues=new ContentValues();
        financeValues.put("NAME",name);
        financeValues.put("PRICE",price);
        financeValues.put("TYPE",type);
        db.insert("Finance",null,financeValues);


    }

    private void updateMyDatabase(SQLiteDatabase db,int oldVersion,int newVersion){
        if(oldVersion<1){

            db.execSQL("CREATE TABLE Finance(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +"NAME TEXT, "
                    +"PRICE TEXT," +"TYPE TEXT);"
            );
            //Инициализация по умолчанию
            insertFinance(db,"Bread","100","expenses");
            insertFinance(db,"Cofee","400","incomes");
            insertFinance(db,"Milk","120","expenses");
            insertFinance(db,"Tea","40","incomes");

        }
    }
}
