package com.example.se2_einzelphase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.BufferedInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class MainActivity : AppCompatActivity() {
    private val adress: String = "se2-isys.aau.at"
    private val port: Int = 53212;
    private lateinit var scor: Job;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val but = findViewById<Button>(R.id.button);
        val txt = findViewById<TextView>(R.id.text2);
        val ctx = findViewById<EditText>(R.id.editTextTextPersonName);
        val bso = findViewById<Button>(R.id.button2);

        but.setOnClickListener {
            // launch coroutine on main thread. supend funcitons should switch on their own if required
            // TODO: CoroutineScope should be used differently. This could lead to heavy memory leaks
            scor = CoroutineScope(Dispatchers.Main).launch {
                val response = client(adress, port, ctx.text.toString());

                if (response == null) {
                    txt.text = "Error: Check Internet connection and try again!";
                } else {
                    txt.text = response;
                }
            }
        }

        bso.setOnClickListener {
            val input = ctx.text.toString();
            val sorted = sort(input);

            txt.text = "Sortiert: $sorted";
        }

    }

    override fun onDestroy() {
        super.onDestroy();
        scor.cancel();
    }

    private suspend fun client(
        adress: String,
        port: Int,
        message: String,
        // withContext allows us to switch to IO thread here. Usually most functions with `suspend` use withContext(SomeDispatcher)
    ): String? = withContext(IO) {
        try {
            // .use automatically calls close on all resrouces of Socket
            Socket(adress, port).use { socket ->
                val outputStream = ObjectOutputStream(socket.getOutputStream());
                val inputStream = BufferedInputStream(socket.getInputStream());

                outputStream.writeUTF(message);
                outputStream.flush();

                return@withContext inputStream.bufferedReader().readLine();
            }
        } catch (error: Exception) {
            return@withContext null; // something went wrong
        }
    }

    private fun sort(input: String): String {
        val inputAsIntegers = input.map { it.digitToInt() } // Now we have a List<Int>

        val evenNumbers = inputAsIntegers.filter { it % 2 == 0 }
        val evenNumbersSorted = evenNumbers.sorted(); // uses natural order, which is 1, 2, 3....

        val oddNumbers = inputAsIntegers.filter { it % 2 == 1 }
        val oddNumbersSorted = oddNumbers.sorted();

        return evenNumbersSorted.joinToString("") + oddNumbersSorted.joinToString("")
    }
}