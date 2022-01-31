package android.example.shopfriendly.ui.fragments

import android.content.Intent
import android.example.shopfriendly.R
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.example.shopfriendly.databinding.FragmentDashboardBinding
import android.example.shopfriendly.firestore.FirestoreClass
import android.example.shopfriendly.models.Product
import android.example.shopfriendly.ui.activities.CartListActivity
import android.example.shopfriendly.ui.activities.ProductDetailsActivity
import android.example.shopfriendly.ui.activities.SettingsActivity
import android.example.shopfriendly.ui.adapters.MyDashboardListAdapter
import android.example.shopfriendly.ui.adapters.MyProductsListAdapter
import android.example.shopfriendly.utils.Constants
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_products.*

class DashboardFragment : BaseFragment() {

   // private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // dashboardViewModel =
           // ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId
        when(id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList : ArrayList<Product>){
        hideProgressDialog()

        if (dashboardItemsList.size>0){
            rv_dashboard_items.visibility=View.VISIBLE
            tv_no_dashboard_items_found.visibility=View.GONE

            rv_dashboard_items.layoutManager= GridLayoutManager(activity,2)
            rv_dashboard_items.setHasFixedSize(true)
            val adapterDashboard = MyDashboardListAdapter(requireActivity(),dashboardItemsList)
            rv_dashboard_items.adapter=adapterDashboard

            adapterDashboard.setOnClickListener(object :
                MyDashboardListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {

                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                    startActivity(intent)

                }
            })
        }
        else
        {
            rv_dashboard_items.visibility=View.GONE
            tv_no_dashboard_items_found.visibility=View.VISIBLE
        }
    }


    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashBoardItemList(this@DashboardFragment)
    }
}