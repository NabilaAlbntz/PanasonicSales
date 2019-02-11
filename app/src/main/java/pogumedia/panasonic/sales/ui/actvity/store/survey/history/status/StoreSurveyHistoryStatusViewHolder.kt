package pogumedia.panasonic.sales.ui.actvity.store.survey.history.status

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.db.entity.StoreSurvey
import pogumedia.panasonic.sales.helper.util.DateUtil
import pogumedia.panasonic.sales.helper.util.setColorDrawable2

/**
 * Created by crocodicstudio on 4/11/18.
 */
class StoreSurveyHistoryStatusViewHolder(parent: ViewGroup)
    : BaseViewHolder<StoreSurvey>(parent, R.layout.item_history_survey) {


    var tvStoreName: TextView = `$`(R.id.tvStoreName)
    var tvDateSurvey: TextView = `$`(R.id.tvDateSurvey)
    var tvTimeSurvey: TextView = `$`(R.id.tvTimeSurvey)
    var tvStoreCustomer: TextView = `$`(R.id.tvStoreCustomer)
    var tvStatus: TextView = `$`(R.id.tvStatus)


    override fun setData(data: StoreSurvey) {
        tvStoreName.text = data.store.nama
        val date = DateUtil.stringtoDate(data.createdAt, DateUtil.FORMAT_ONE)
        tvDateSurvey.text = DateUtil.dateToString(date, "dd-MM-yyyy")

        tvTimeSurvey.text = DateUtil.dateToString(date, "HH:mm")
        setViewSpannable(data.store)


        if (data.statusSurvey.isNullOrEmpty())
            tvStatus.visibility = View.GONE
        else tvStatus.text = data.statusSurvey
        val background = setColorDrawable2(
                context,
                ContextCompat.getDrawable(context, R.drawable.rounded_label)!!,
                Color.parseColor(data.warna))
        tvStatus.background = background


    }

    private fun setViewSpannable(data: Store) {
        val ssBuilder = SpannableStringBuilder(data.pic + "   " + data.telp)
        val myIcon = context.resources.getDrawable(R.drawable.bg_circle)
        myIcon.setBounds(0, 0, myIcon.intrinsicWidth, myIcon.intrinsicHeight)
        val imgSpan = ImageSpan(myIcon, ImageSpan.ALIGN_BASELINE)
        data.pic?.length?.plus(1)?.let {
            ssBuilder.setSpan(
                    imgSpan,
                    it,
                    data.pic?.length?.plus(2)!!,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tvStoreCustomer.text = ssBuilder
    }

}

