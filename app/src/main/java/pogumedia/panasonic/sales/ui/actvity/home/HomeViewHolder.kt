package pogumedia.panasonic.sales.ui.actvity.home

import android.view.ViewGroup
import android.widget.TextView
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.Store
import android.text.Spanned
import android.text.style.ImageSpan
import android.text.SpannableStringBuilder


/**
 * Created by crocodicstudio on 4/11/18.
 */
class HomeViewHolder(parent: ViewGroup)
    : BaseViewHolder<Store>(parent, R.layout.item_store) {


    var tvStoreName: TextView = `$`(R.id.tvStoreName)
    var tvStoreAddress: TextView = `$`(R.id.tvStoreAddress)
    var tvStoreCustomer: TextView = `$`(R.id.tvStoreCustomer)


    override fun setData(data: Store) {
        tvStoreName.text = data.nama
        tvStoreAddress.text = data.address
        setViewSpannable(data)
    }

    private fun setViewSpannable(data: Store) {
        val ssBuilder = SpannableStringBuilder(data.pic + "   " + data.telp)
        val myIcon = context.resources.getDrawable(R.drawable.bg_circle)
        myIcon.setBounds(0, 0, myIcon.intrinsicWidth, myIcon.intrinsicHeight)
        val imgSpan = ImageSpan(myIcon, ImageSpan.ALIGN_BASELINE)
        ssBuilder.setSpan(
                imgSpan,
                data.pic?.length!! + 1,
                data.pic?.length!! + 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvStoreCustomer.text = ssBuilder
    }

}

