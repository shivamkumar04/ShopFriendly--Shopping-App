package android.example.shopfriendly.ui.activities

import android.example.shopfriendly.R
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_forgot_password_activity.setNavigationOnClickListener{ onBackPressed()}

        btn_continue.setOnClickListener{

            val email: String=et_Email_forgot.text.toString().trim{ it<=' '}
            if (email.isEmpty())
            {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_Email),true)
            }
            else
            {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{task->
                    hideProgressDialog()

                    if(task.isSuccessful) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            resources.getString(R.string.email_sent_success),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                    else
                    {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            }
        }

    }
}