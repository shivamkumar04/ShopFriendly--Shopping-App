package android.example.shopfriendly.ui.activities

import android.content.Intent
import android.example.shopfriendly.R
import android.example.shopfriendly.firestore.FirestoreClass
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_splashactivity.*
@Suppress("DEPRECATION")
class Splashactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashactivity)


            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )


        Handler().postDelayed(
            {

                val currentUserID = FirestoreClass().getCurrentUserID()
                if (currentUserID.isNotEmpty()) {
                    // Launch dashboard screen.
                    startActivity(Intent(this@Splashactivity, DashboardActivity::class.java))
                } else {
                    // Launch the Login Activity
                    startActivity(Intent(this@Splashactivity, LoginActivity::class.java))
                }
                finish()
            },
            1500
        )
        //val typeface:Typeface=Typeface.createFromAsset(assets,"Montserrat-Bold.ttf")
        //tv_appname.typeface= typeface
    }
}