package pogumedia.panasonic.sales.helper.image

import android.content.Context
import android.widget.ImageView
import pogumedia.panasonic.sales.R
import com.squareup.picasso.Picasso
import java.io.File

class PicassoHelper {
    companion object {
        fun loadImageFromUrl(context: Context, url: String, target: ImageView) {
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(target)
        }


        fun loadImageFromFile(context: Context, file: File, target: ImageView) {
            Picasso.get()
                    .load(file)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(target)
        }
    }
}