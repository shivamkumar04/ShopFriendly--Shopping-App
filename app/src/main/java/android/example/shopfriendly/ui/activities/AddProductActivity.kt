package android.example.shopfriendly.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.example.shopfriendly.R
import android.example.shopfriendly.firestore.FirestoreClass
import android.example.shopfriendly.models.Product
import android.example.shopfriendly.utils.Constants
import android.example.shopfriendly.utils.GlideLoader
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class AddProductActivity : BaseActivity() ,View.OnClickListener{

    private var mSelectedImageFileUri: Uri?=null
    private var mProductImageUrl:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionBar()

        iv_add_update_product.setOnClickListener(this)

        btn_submit_product.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_add_product_activity.setNavigationOnClickListener{ onBackPressed() }
    }

    override fun onClick(v: View?) {
        if(v!=null) {
            when(v.id){
                R.id.iv_add_update_product -> {
                    if(ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this@AddProductActivity)
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit_product ->{
                    if(validateRegisterDetails()){
                        uploadProductImage()
                    }
                }
            }
        }
    }

    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.PRODUCT_IMAGE)
    }

    private fun uploadProductDetails(){
        val username=this.getSharedPreferences(Constants.SHOPFRIENDLY_PREFERENCES,Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME,"")!!

        val product=Product(
            FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim{it<=' '},
            et_product_price.text.toString().trim{it<=' '},
            et_product_description.text.toString().trim{it<=' '},
            et_product_quantity.text.toString().trim{it<=' '},
            mProductImageUrl
        )

        FirestoreClass().uploadProductDetails(this,product)
    }


    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this@AddProductActivity,
            resources.getString(R.string.product_upload_success_message),Toast.LENGTH_SHORT).show()
        finish()
    }

    fun imageUploadSuccess(imageURL:String){
        //hideProgressDialog()
        //showErrorSnackBar("success: $imageURL",false)

        mProductImageUrl=imageURL

        uploadProductDetails()




    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
                // showErrorSnackBar("the storage permission is granted.",false)
            }
            else
            {
                Toast.makeText(this,resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode== Activity.RESULT_OK) {
            if(requestCode==Constants.PICK_IMAGE_REQUEST_CODE){
                if(data!=null){
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))

                     mSelectedImageFileUri=data.data!!

                   try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_product_image)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddProductActivity,
                            resources.getString(R.string.image_selection_failed),Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        else if(resultCode== Activity.RESULT_CANCELED){
            Log.e("Request Cancelled","Image selection Cancelled")
        }

    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            mSelectedImageFileUri==null ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image),true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title),true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price),true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description),true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity),true)
                false
            }
            else -> {

                true
            }
        }
    }


}