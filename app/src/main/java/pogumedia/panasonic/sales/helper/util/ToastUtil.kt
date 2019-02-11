package pogumedia.panasonic.sales.helper.util

import android.content.Context
import android.widget.Toast
import javax.inject.Inject

class ToastUtil constructor(val context: Context){

    private lateinit var toast: Toast

     fun showAToast(st: String, duration: Int) {
        try {
            toast.getView().isShown()
            toast.setText(st)
        } catch (e: Exception) {
            toast = Toast.makeText(context, st, duration)
        }

        toast.show()
    }

     fun showAToast(st: String) {
        try {
            toast.view.isShown
            toast.setText(st)
        } catch (e: Exception) {
            toast = Toast.makeText(context, st, Toast.LENGTH_SHORT)
        }

        toast.show()
    }

    fun cancelToast(){
        if (toast.view.isShown){
            toast.cancel()
        }
    }

}