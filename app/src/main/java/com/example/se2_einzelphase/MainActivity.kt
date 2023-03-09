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

        val but = findViewById<Button>(R.id.button);
        val txt = findViewById<TextView>(R.id.text2);
        val ctx = findViewById<EditText>(R.id.editTextTextPersonName);

        but.setOnClickListener {
            active = true;
            CoroutineScope(IO).launch {
                client(adress, port, ctx.toString(), txt);
            }
        }

    }

    private suspend fun client(adress:String,port:Int, message:String, txt:TextView){
        val connection = Socket(adress,port);
        val writer = connection.getOutputStream();
        writer.write(message.toByteArray());
        val reader = Scanner(connection.getInputStream());
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