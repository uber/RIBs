package com.badoo.ribs.example.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.badoo.ribs.example.R
import kotlinx.android.synthetic.main.activity_other.fab
import kotlinx.android.synthetic.main.activity_other.toolbar

class OtherActivity : AppCompatActivity() {

    companion object {
        const val KEY_INCOMING = "incoming"
        const val KEY_RETURNED_DATA = "foo"
        private const val RETURNED_DATA = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        setSupportActionBar(toolbar)


        findViewById<TextView>(R.id.incoming_data).text = intent.extras?.getString(KEY_INCOMING)

        fab.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply { putExtra(KEY_RETURNED_DATA, RETURNED_DATA) })
            finish()
        }
    }
}
