package com.example.cryptonator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class DBHelper (context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
            val CREATE_TABLE = "CREATE TABLE  IF NOT EXISTS KeyTable ( KeyID TEXT, ID INTEGER PRIMARY KEY )"
        db!!.execSQL(CREATE_TABLE)
        val SECOND = "CREATE TABLE  IF NOT EXISTS deviceTable ( KeyID TEXT,deviceID TEXT, ID INTEGER)"
        db!!.execSQL(SECOND)
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
     val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
     val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    fun encrypt(strToEncrypt: String) :  String?
    {
        try
        {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        }
        catch (e: Exception)
        {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt : String) : String? {
        try
        {

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        }
        catch (e : Exception) {
            println("Error while decrypting: $e");
        }
        return null
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
            value.put("KeyID",encrypt(keyId))
            db.update("KeyTable",value,"ID=?", arrayOf("1"));
        }
        else{
            value.put("KeyID",encrypt(keyId))
            value.put("ID",1)
            db.insert("KeyTable",null,value)
        }



    }
    fun UpdateDevice(keyId: String,deviceId:String){

        val db =this.writableDatabase
        val value= ContentValues()
        val cursor=db!!.query(
            "deviceTable",
            arrayOf("ID", "KeyID","deviceID"),
            "deviceID" + "=?",
            arrayOf(deviceId),
            null,
            null,
            null,
            null
        )
        val data:String?=null;

        if(cursor!=null && cursor.moveToFirst()){
            value.put("KeyID",encrypt(keyId))
            db.update("deviceTable",value,"deviceID=?", arrayOf(deviceId));
        }
        else{
            value.put("KeyID",encrypt(keyId))
            value.put("ID",2)
            value.put("deviceID",deviceId)
            db.insert("deviceTable",null,value)
        }

    }
    fun GetTable(): ArrayList<String>{
        val db = this.writableDatabase;
        val cursor  =db!!.query(
            "deviceTable",
            arrayOf("ID", "KeyId","deviceID"),
            null,
            null,
            null,
            null,
            null,
            null
        )
        val list : ArrayList<String> = ArrayList()
        try{
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    val name: String = cursor.getString(cursor.getColumnIndex("deviceID"))
                    list.add(name)
                    cursor.moveToNext()
                }
            }
        }
        catch (e:Exception){
            println("Purai Khali" )
            return ArrayList()
        }
        return list
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
              return decrypt(data)
                }
            else{
                println("Purai Khali" )
                return ""
            }

        }

        catch (e:Exception){
            println("ERRROR FALYO" + e)
            return ""
        }
    }
    fun getDeviceKey(deviceId: String):String? {
        val selectQuery = "SELECT * FROM KeyTable"
        val db = this.writableDatabase;
        val cursor  =db!!.query(
            "deviceTable",
            arrayOf("ID", "KeyId","deviceID"),
            "deviceID" + "=?",
            arrayOf(deviceId),
            null,
            null,
            null,
            null
        )
        try{
            if(cursor!=null && cursor.moveToFirst()) {
                val data = cursor.getString(cursor.getColumnIndex("KeyID"))
                return decrypt(data)
            }
            else{
                println("Purai Khali" )
                return ""
            }

        }

        catch (e:Exception){
            println("ERRROR FALYO" + e)
            return ""
        }
    }
}