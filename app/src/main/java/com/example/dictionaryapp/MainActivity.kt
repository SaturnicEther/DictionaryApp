package com.example.dictionaryapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    data class DictionaryWord(
        val id: Int = 0,
        val word: String,
        val translation: String
    )

    val DB_NAME = "dictionary.db"
    val INIT_DB_DDL = """
        CREATE TABLE IF NOT EXISTS dictionary (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            word TEXT NOT NULL,
            translation TEXT NOT NULL
        );
    """
    val CLEAR_DB_DATA = "DELETE FROM dictionary"
    val INSERT_DB_DATA = """
        INSERT INTO dictionary (word, translation)
        VALUES
            ('cat', 'кошка'),
            ('dog', 'собака'),
            ('life', 'жизнь'),
            ('intention', 'намерение'),
            ('goal', 'цель'),
            ('dream', 'мечта'),
            ('sleep', 'сон'),
            ('time', 'время'),
            ('date', 'дата'),
            ('data', 'данные');
    """

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun initDb(view: View) {
        var db: SQLiteDatabase? = null
        try{
            db = baseContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null)
            db.execSQL(INIT_DB_DDL)
            db.execSQL(CLEAR_DB_DATA)
            db.execSQL(INSERT_DB_DATA)
            Toast.makeText(this, "DB initialization done", Toast.LENGTH_SHORT).show()
            Log.d("initDB debug", "DB initialization done")
        }
        catch(ex: Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            Log.e("initDb exception", ex.message ?: "")
        }
        finally{
            db?.close()
            Log.d("initDB debug", "finished")
        }
    }

    fun showDictionary(view: View) {
        var db: SQLiteDatabase? = null
        try{
            db = baseContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null)
            val words = ArrayList<DictionaryWord>()
            db.rawQuery("SELECT * FROM dictionary", null).use { cursor ->
                while(cursor.moveToNext()){
                    words.add(
                        DictionaryWord(
                        id = cursor.getInt(0),
                        word = cursor.getString(1),
                        translation = cursor.getString(2)
                    ))
                }
            }
            val dictionaryListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, words.map {
                "${it.id}. ${it.word} - ${it.translation}"
            })
            findViewById<ListView>(R.id.dictionaryListView).adapter = dictionaryListAdapter
        }
        catch(ex: Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            Log.e("showDictionary exception", ex.message ?: "")
        }
        finally{
            db?.close()
            Log.d("initDB debug", "finished")
        }
    }
    @SuppressLint("Recycle")
    fun addWord(view: View) {
        var db: SQLiteDatabase? = null
        try {
            db = baseContext.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null)
            val dictionaryWord = DictionaryWord(
                word = findViewById<EditText>(R.id.english_word).text.toString(),
                translation = findViewById<EditText>(R.id.russian_translation).text.toString()
            )
            val values = ContentValues()
            values.put("word", dictionaryWord.word)
            values.put("translation", dictionaryWord.translation)
            val cursor = db.rawQuery("SELECT * from dictionary where word = " + "'${dictionaryWord.word}'" + " and translation = " + "'${dictionaryWord.translation}'", null)
            if(cursor.count > 0){
                Toast.makeText(this, "this translation already exists", Toast.LENGTH_SHORT).show()
                Log.e("addWord exception", "this translation already exists")
                cursor.close()
            }
            else{
                db.insert("dictionary", null, values)
                Toast.makeText(this, "word adding done", Toast.LENGTH_SHORT).show()
                Log.d("addWord debug", "word adding done")
                cursor.close()
            }
        }
        catch(ex: Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            Log.e("addWord exception", ex.message ?: "")
        }
        finally{
            db?.close()
            Log.d("initDB debug", "finished")
        }
    }
}