package com.example.se2_einzelphase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.Socket
import java.util.*

class MainActivity : AppCompatActivity() {
    private var active : Boolean = false;
    private var mulamessage : String = "";
    val adress : String = "se2-isys.aau.at"
    val port : Int = 53212;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var but = findViewById<Button>(R.id.button);
        var txt = findViewById<TextView>(R.id.text2);
        var ctx = findViewById<EditText>(R.id.editTextTextPersonName);
        var bso = findViewById<Button>(R.id.button2);

        but.setOnClickListener {
            active = true;
            CoroutineScope(IO).launch {
                client(adress, port, ctx.text.toString(), txt);
            }
        }

        bso.setOnClickListener {
            var ar = ctx.text.toString().toCharArray();
            var fin : String = "Sortiert: ";
            var iar : IntArray = IntArray(10);
            for(i in ar.indices){
                when(ar[i]){
                    '0' -> iar[0]++;
                    '1' -> iar[1]++;
                    '2' -> iar[2]++;
                    '3' -> iar[3]++;
                    '4' -> iar[4]++;
                    '5' -> iar[5]++;
                    '6' -> iar[6]++;
                    '7' -> iar[7]++;
                    '8' -> iar[8]++;
                    '9' -> iar[9]++;
                }
            }
            for(i in 1..8){
                if(iar[0]>0){
                    fin += "0";
                    iar[0]--;
                }else if(iar[2]>0){
                    fin += "2";
                    iar[2]--;
                } else if(iar[4]>0){
                    fin += "4";
                    iar[4]--;
                }else if(iar[6]>0){
                    fin += "6";
                    iar[6]--;
                }else if(iar[8]>0){
                    fin += "8";
                    iar[8]--;
                }else if(iar[1]>0){
                    fin += "1";
                    iar[1]--;
                }else if(iar[3]>0){
                    fin += "3";
                    iar[3]--;
                }else if(iar[5]>0){
                    fin += "5";
                    iar[5]--;
                }else if(iar[7]>0){
                    fin += "7";
                    iar[7]--;
                }else if(iar[9]>0){
                    fin += "9";
                    iar[9]--;
                }
            }
            txt.text = fin;
        }

    }

    private suspend fun client(adress:String,port:Int, message:String, txt:TextView){
        var connection = Socket(adress,port);
        var writer = connection.getOutputStream();
        writer.write(message.toByteArray());
        var reader = Scanner(connection.getInputStream());
        while(active){
            mulamessage = reader.next();
            txt.text = mulamessage;
            if(mulamessage!=""){
                active = false;
                mulamessage = "";
            }
        }
        reader.close();
        writer.close();
        connection.close();
    }
}