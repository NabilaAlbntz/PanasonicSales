package pogumedia.panasonic.sales.ui.actvity.main

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pogumedia.panasonic.sales.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_main_menu.*
import android.support.v4.widget.TextViewCompat.setTextAppearance
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v4.widget.TextViewCompat.setTextAppearance
import android.util.TypedValue
import android.widget.TextView


class MainAdapter(private val items: List<String>, private val listener: (Int) -> Unit)
    : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var posSelected = 0


    var setSelectedPosition: Int = 0
        set(value) {
            posSelected = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main_menu, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener, posSelected)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(override val containerView: View, val context: Context) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bindItem(item: String, listener: (Int) -> Unit, posSelected: Int) {
            tvTitle.text = item
            tvTitle.setOnClickListener {
                listener(adapterPosition)
            }
            if (adapterPosition == posSelected) {
                llBgSelected.visibility = View.VISIBLE
                setTextAppearances(tvTitle, context, R.style.TextAppearance_Bold)
            } else {
                llBgSelected.visibility = View.GONE
                setTextAppearances(tvTitle, context, R.style.TextAppearance_Regular)
            }

        }

        private fun setTextAppearances(textView: TextView, context: Context, resId: Int) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                textView.setTypeface(textView.typeface, Typeface.BOLD)
            } else {
                textView.setTypeface(textView.typeface, Typeface.NORMAL)
            }
            textView.setTextColor(ContextCompat.getColor(context, R.color.color_white))
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)

        }
    }


}