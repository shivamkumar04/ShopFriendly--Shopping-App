package android.example.shopfriendly.ui.adapters

import android.content.Context
import android.example.shopfriendly.R
import android.example.shopfriendly.models.Product
import android.example.shopfriendly.utils.GlideLoader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dashboard_list_layout.view.*
import kotlinx.android.synthetic.main.item_list_layout.view.*


open class MyDashboardListAdapter (
    private val context: Context,
    private var list: ArrayList<Product>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyProductsListAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.dashboard_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]

        if(holder is MyProductsListAdapter.MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.db_iv_item_image)
            holder.itemView.db_tv_item_name.text=model.title
            holder.itemView.db_tv_item_price.text= "Rs${model.price}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {

        fun onClick(position: Int, product: Product)

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}