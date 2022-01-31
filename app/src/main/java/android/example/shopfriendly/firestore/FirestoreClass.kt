package android.example.shopfriendly.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.example.shopfriendly.models.*
import android.example.shopfriendly.ui.activities.*
import android.example.shopfriendly.ui.fragments.DashboardFragment
import android.example.shopfriendly.ui.fragments.OrdersFragment
import android.example.shopfriendly.ui.fragments.ProductsFragment
import android.example.shopfriendly.utils.Constants
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User)
    {
        mFirestore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge()).addOnSuccessListener{

                activity.userRegistrationSuccess()

            }.addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,"Error while registering the user "
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser != null) {
            currentUserID=currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        mFirestore.collection(Constants.USERS).document(getCurrentUserID()).get().addOnSuccessListener { document ->
            Log.i(activity.javaClass.simpleName,document.toString())

            val user=document.toObject(User::class.java)!!


            val sharedPreferences=activity.getSharedPreferences(
                Constants.SHOPFRIENDLY_PREFERENCES,Context.MODE_PRIVATE            )

            val editor: SharedPreferences.Editor=sharedPreferences.edit()
            editor.putString(
                Constants.LOGGED_IN_USERNAME,"${user.firstName} ${user.lastName}"
            )
           editor.apply()

            when(activity) {
                is LoginActivity -> {
                    activity.userLoggedInSuccess(user)
                }
                is SettingsActivity ->{
                    // Call a function of base activity for transferring the result to it.
                    activity.userDetailsSuccess(user)
                    // END
                }
            }
        }
            .addOnFailureListener{ e ->
                when(activity) {
                    is LoginActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while getting user Details.",e)
            }

    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener{_: Void? ->
                when(activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{e->
                when(activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,"error while updating the user details.",e
                )
            }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri? ,imageType: String) {
       val sRef: StorageReference=FirebaseStorage.getInstance().reference.child(
           imageType + System.currentTimeMillis() + "."
                   + Constants.getFileExtension(
               activity,imageFileURI
           )
       )

        sRef.putFile((imageFileURI!!)).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri->
                    Log.e("Downloadable Image URL",uri.toString())
                    when(activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity-> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener { exception ->
                when(activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }

    }

    fun uploadProductDetails(activity: AddProductActivity,productInfo:Product) {
        mFirestore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "error while uploading the product details.",e
                )
            }
    }

    fun getProductList(fragment: Fragment){
        mFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.e("Products List",document.documents.toString())
                val productsList:ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id

                    productsList.add(product)
                }
                when(fragment){
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }

    }

    fun deleteProduct(fragment: ProductsFragment,productId: String){
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener{ e->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product",e
                )
            }
    }




    fun getDashBoardItemList(fragment: DashboardFragment){
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document->
                Log.e(fragment.javaClass.simpleName,document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    val product=i.toObject(Product::class.java)
                    product!!.product_id=i.id

                    productsList.add(product)
                }

                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener{e->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,"error while getting dashboard item list",e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->


                Log.e(activity.javaClass.simpleName, document.toString())


                val product = document.toObject(Product::class.java)!!


                activity.productDetailsSuccess(product)

            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }
    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {

        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }
    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {

        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())


                // If the document size is greater than 1 it means the product is already added to the cart.
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun getAllProductsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFirestore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }

                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }
    fun removeItemFromCart(context: Context, cart_id: String) {

        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {


                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {


        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {


                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->

                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {


        mFirestore.collection(Constants.ADDRESSES)
            .document()

            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }


    fun getAddressesList(activity: AddressListActivity) {

        mFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String){
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo,SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while updating teh address",e)
            }
    }


    fun deleteAddress(activity: AddressListActivity, addressId: String){
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                activity.deleteAddressSuccess()
            }
            .addOnFailureListener{ e->

                Log.e(activity.javaClass.simpleName,"Error while deleting the address",e)

            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFirestore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>) {

        val writeBatch = mFirestore.batch()


        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFirestore.collection(Constants.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFirestore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {


            activity.allDetailsUpdatedSuccessfully()


        }.addOnFailureListener { e ->
            activity.hideProgressDialog()

            Log.e(activity.javaClass.simpleName, "Error while updating all the details after order placed.", e)
        }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                fragment.populateOrdersListInUI(list)

            }
            .addOnFailureListener { e ->

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }



}