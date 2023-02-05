package com.example.flibuster

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flibuster.adapters.FindedListAdapter
import com.example.flibuster.api.FlibustaFetch
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*


class MainActivity : AppCompatActivity() {
    val api = FlibustaFetch()
    lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        recyclerView = findViewById(R.id.list_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        recyclerView.adapter = FindedListAdapter(mutableListOf<Map<String, String>>(), this)

        findViewById<Button>(R.id.button_find).setOnClickListener {
            val textFromInput = findViewById<EditText>(R.id.book_name_placeholder).text.toString()
            if(!textFromInput.isEmpty()) {
                this.findBook(textFromInput)
            } else {
                Toast.makeText(this, "Cannot be empty!", Toast.LENGTH_LONG).show()
            }
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0)
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE),
                0)
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0)
        }

        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
            return
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun findBook(book: String) {
        GlobalScope.launch {
            this@MainActivity.runOnUiThread {
                recyclerView.adapter = FindedListAdapter(api.findBook(book), this@MainActivity)
            }
        }
    }
}