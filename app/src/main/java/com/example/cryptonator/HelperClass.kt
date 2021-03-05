package com.example.cryptonator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

 class HelperClass{
    private fun HelperClass(){

     }
   public fun dataParser(message: String){
        val header =  message.substring(0,1);
        val body =  message.substring(1,message.length);
        println(body+ " " +header)
        when(header){
            "S"-> stateDefinition(body)  //State DEFINITION
            "T"-> print(body)

        }
    }
   public fun stateDefinition(data:String){
       val header =  data.substring(0,1);
       val body =  data.substring(1,data.length);

       when(header){
           "D"-> print(body)  //Switch Tabs Decryption
           "E"-> print(body)    //Switch Encryption Body Only Here
           "U"-> print(body)    //Loading page
           "L"-> print(body)    //Loading Page

       }

   }
}