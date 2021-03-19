package com.example.cryptonator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception

class DBHelper (context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS KeyTable ( KeyID TEXT, ID INTEGER PRIMARY KEY )"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
    companion object{
        private  val DATABASE_NAME="Key.db"
        private val DATABASE_VERSION=1
        private val TABLE_NAME = "Key"
        private  val COL= "Key"
    }

    fun Add(keyId:String){
            val db =this.writableDatabase
            val value= ContentValues()

    }
    fun Update(keyId:String){



        val db =this.writableDatabase
        val value= ContentValues()
        val cursor=db!!.query(
            "KeyTable",
            arrayOf("ID", "KeyID"),
            "ID" + "=?",
            arrayOf("1"),
            null,
            null,
            null,
            null
        )
        val data:String?=null;

        if(cursor!=null && cursor.moveToFirst()){
            value.put("KeyID",keyId)
            db.update("KeyTable",value,"ID=?", arrayOf("1"));
        }
        else{
            value.put("KeyID",keyId)
            value.put("ID",1)
            db.insert("KeyTable",null,value)
        }



    }
    fun GetDB(): String? {
            val selectQuery = "SELECT * FROM KeyTable"
            val db = this.writableDatabase;
            val cursor  =db!!.query(
                "KeyTable",
                arrayOf("ID", "KeyId"),
                "ID" + "=?",
                arrayOf("1"),
                null,
                null,
                null,
                null
            )
        try{
            if(cursor!=null && cursor.moveToFirst()) {
                val data = cursor.getString(cursor.getColumnIndex("KeyID"))
              return data
                }
            else{
                println("Purai Khali" )
                return "Can't Fetch Data"
            }

        }

        catch (e:Exception){
            println("ERRROR FALYO" + e)
            return "Can't Fetch Data"
        }
    }
}