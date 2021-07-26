package com.example.cryptonator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.loading.*
import java.lang.Exception


class HelperClass(c:Context,m_address:String){
    private val context: Context
    private val address:String
    private lateinit var dbHelper:DBHelper
        init {
            this.context=c;
            this.address=m_address
        }
        fun HelperClass(){

             dbHelper= DBHelper(this.context)

     }
   public fun dataParser(message: String,m_address: String){
        val header =  message.substring(0,1);
        val body =  message.substring(1,message.length);
       println("Thread Running")
        println(body+ ":" +header)
        when(header){
            "S"-> stateDefinition(body)  //State DEFINITION
            "T"-> print(body)
            "K"->Acknowledgement(body,m_address)

        }
    }
    private  fun Acknowledgement(data: String,m_address: String){
        val header =  data.substring(0,1);
        val body =  data.substring(0,data.length);
              try {
                  dbHelper= DBHelper(this.context)
                        dbHelper.Update(body)
                  dbHelper.UpdateDevice(body,m_address)
//                        var data = dbHelper.GetDB();
//                        println(data + "DAtabase")
                      //Switch Tabs Decryption
                } catch (e: Exception) {
                    println(e.stackTrace)
                }

    }
   private fun stateDefinition(data:String){
       val header =  data.substring(0,1);
       val body =  data.substring(1,data.length);
       when(header){
           "D"->
               try{
                   val intent = Intent(context, VerifiedActivity::class.java)
                   intent.putExtra("Address", address)
                   context.startActivity(intent)  //Switch Tabs Decryption
               }
               catch (e:Exception){
                   println(e.stackTrace)
               }

           "E"->
               try{
                   val intent = Intent(context, EncryptedPage::class.java)
                   intent.putExtra("Device_address", address)
                   context.startActivity(intent)  //Switch Tabs Decryption


               }
               catch (e:Exception){
                   println(e.stackTrace)
               }   //Switch Encryption Body Only Here


           "U"->
               try{
                   val intent = Intent(context, Loading::class.java)

                   intent.putExtra("Device_address", "Files are Decrypting")
                   context.startActivity(intent)  //Switch Tabs Decryption


               }
               catch (e:Exception){
                   println(e.stackTrace)
               }   //Switch Encryption Body Only Here


           "L"->
               try{
                    println("Here")
//                     val intent = Intent(context, Loading::class.java)
//                     context.startActivity(intent)  //Switch Tabs Decryption
                   val intent = Intent(context, Loading::class.java)
                   intent.putExtra("Device_address", "Files are Encrypting")

                   context.startActivity(intent)  //Switch Tabs Decryption



               }
           catch (e:Exception){
               println(e.stackTrace)
           }   //Switch Encryption Body Only Here


       }

   }
}