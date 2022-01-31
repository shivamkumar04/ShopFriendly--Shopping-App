package android.example.shopfriendly.ui.activities

import android.content.Context
import android.example.shopfriendly.R
import android.example.shopfriendly.utils.Constants
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences=getSharedPreferences(Constants.SHOPFRIENDLY_PREFERENCES,Context.MODE_PRIVATE)
        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        tv_main.text="The logged in user is $username."
        

    }
}