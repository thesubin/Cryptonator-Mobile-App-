package com.example.cryptonator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_TABLE = "CREATE TABLE KeyTable ( KeyID TEXT, ID INTEGER PRIMARY KEY )"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop TABLE IF EXISTS KeyTable")
            onCreate(db!!)
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
        val selectQuery = "SELECT * FROM KeyTable"
        val cursor=db!!.rawQuery(selectQuery,null)
        val data:String?=null;

        if(cursor!=null && cursor.moveToFirst()){
            value.put("KeyId",keyId)
            db.update("KeyTable",value,"ID=?", arrayOf("1"));
        }
        else{
            value.put("KeyId",keyId)
            value.put("ID","1")
            db.insert("KeyTable",null,value)
        }



    }
    fun GetDB(){
            val selectQuery = "SELECT * FROM KeyTable"
            val db = this.writableDatabase;
            val cursor=db!!.rawQuery(selectQuery,null)
            println(cursor)
    }
}