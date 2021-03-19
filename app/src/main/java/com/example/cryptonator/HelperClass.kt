package com.example.cryptonator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.loading.*
import java.lang.Exception


class HelperClass(c:Context){
    private val context: Context
    private lateinit var dbHelper:DBHelper
        init {
            this.context=c;
        }
    private fun HelperClass(){
             dbHelper= DBHelper(context)

     }
   public fun dataParser(message: String){
        val header =  message.substring(0,1);
        val body =  message.substring(1,message.length);
       println("Thread Running")
        println(body+ ":" +header)
        when(header){
            "S"-> stateDefinition(body)  //State DEFINITION
            "T"-> print(body)
            "A"->Acknowledgement(body)

        }
    }
    private  fun Acknowledgement(data: String){
        val header =  data.substring(0,1);
        val body =  data.substring(1,data.length);
        when(header) {
            "K" ->
                try {

                        dbHelper.Update(body)

                      //Switch Tabs Decryption
                } catch (e: Exception) {
                    println(e.stackTrace)
                }
        }
    }
   public fun stateDefinition(data:String){
       val header =  data.substring(0,1);
       val body =  data.substring(1,data.length);
       when(header){
           "D"->
               try{
                   val intent = Intent(context, VerifiedActivity::class.java)
                   intent.putExtra(ControlActivity.m_address, ControlActivity.m_address)
                   context.startActivity(intent)  //Switch Tabs Decryption
               }
               catch (e:Exception){
                   println(e.stackTrace)
               }

           "E"->
               try{
                   val intent = Intent(context, EncryptedPage::class.java)
                   intent.putExtra(MainActivity.EXTRA_ADDRESS, ControlActivity.m_address)
                   context.startActivity(intent)  //Switch Tabs Decryption


               }
               catch (e:Exception){
                   println(e.stackTrace)
               }   //Switch Encryption Body Only Here


           "U"->
               try{
                   val intent = Intent(context, Loading::class.java)
                   Loading.instance.mainId.text="Your Files are being Decrypted";

                   context.startActivity(intent)  //Switch Tabs Decryption


               }
               catch (e:Exception){
                   println(e.stackTrace)
               }   //Switch Encryption Body Only Here


           "L"->
               try{
                     val intent = Intent(context, Loading::class.java)
                   Loading.instance.mainId.text="Your Files are being Encrypted";
                     context.startActivity(intent)  //Switch Tabs Decryption


           }
           catch (e:Exception){
               println(e.stackTrace)
           }   //Switch Encryption Body Only Here


       }

   }
}