package com.example.androidexercise

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class UrlEdit : AppCompatActivity() {
    private val URL_NAME:String = "UrlName"

    private lateinit var confirmButton:Button
    private lateinit var editText:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.url_edit_layout)
        confirmButton = findViewById(R.id.btn_submit)
        editText = findViewById(R.id.et_comment)
        confirmButton.setOnClickListener(){
            var intent = Intent()
            var bundle = Bundle()
            bundle.putString(URL_NAME, editText.text.toString())
            bundle.putInt("id",getIntent().getIntExtra("id",-1))
            intent.putExtras(bundle)
            this.setResult(Activity.RESULT_OK, intent)
            this.finish()
        }
    }
}
