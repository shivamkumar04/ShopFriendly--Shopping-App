package android.example.shopfriendly.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.example.shopfriendly.R
import android.example.shopfriendly.databinding.FragmentProductsBinding
import android.example.shopfriendly.firestore.FirestoreClass
import android.example.shopfriendly.models.Product
import android.example.shopfriendly.ui.activities.AddProductActivity
import android.example.shopfriendly.ui.adapters.MyProductsListAdapter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {

    //private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun deleteProduct(productId: String){
        showAlertDialogToDeleteProduct(productId)
    }

    fun productDeleteSuccess(){
        hideProgressDialog()

        Toast.makeText(requireActivity(),resources.getString(R.string.product_delete_success_message),Toast.LENGTH_SHORT).show()

        getProductListFromFirestore()
    }


    fun successProductsListFromFirestore(productsList: ArrayList<Product>) {
        hideProgressDialog()

        if(productsList.size>0){
            rv_my_product_items.visibility=View.VISIBLE
            tv_no_products_found.visibility=View.GONE

            rv_my_product_items.layoutManager=GridLayoutManager(activity,2)
            val adapterProducts = MyProductsListAdapter(requireActivity(),productsList,this)
            rv_my_product_items.adapter=adapterProducts
        }
        else
        {
            rv_my_product_items.visibility=View.GONE
            tv_no_products_found.visibility=View.VISIBLE
        }
    }

    private fun getProductListFromFirestore(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFirestore()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel =
           // ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_product) {

            startActivity(Intent(activity, AddProductActivity::class.java))

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAlertDialogToDeleteProduct(productId: String){
        val builder =AlertDialog.Builder(requireActivity())

        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface,_ ->

            FirestoreClass().deleteProduct(this,productId)

            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface,_ ->

            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog=builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}