package pogumedia.panasonic.sales.helper.util

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import pogumedia.panasonic.sales.BuildConfig
import kotlinx.android.synthetic.main.activity_login.*


fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


fun setColorDrawable(context: Context, oldDrawable: Drawable, newColor: Int): Drawable {
    oldDrawable.mutate().setColorFilter(context.resources.getColor(newColor), PorterDuff.Mode.SRC_IN)
    return oldDrawable
}

fun setColorDrawable2(context: Context, oldDrawable: Drawable, newColor: Int): Drawable {
    oldDrawable.mutate().setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
    return oldDrawable
}

fun setupEtPassword(editText: EditText){
    editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    editText.transformationMethod = PasswordTransformationMethod.getInstance()
}

fun getVersionName(): String {
    return BuildConfig.VERSION_NAME
}

fun getAndroidSdkVersion(): Int {
    return Build.VERSION.SDK_INT
}

fun getSdkMVersion(): Int {
    return Build.VERSION_CODES.M
}

fun enableDisableViewGroup(viewGroup: ViewGroup, enabled: Boolean) {
    val childCount = viewGroup.childCount
    for (i in 0 until childCount) {
        val view = viewGroup.getChildAt(i)
        view.isLongClickable = enabled
        view.isEnabled = enabled
        view.isFocusable = enabled
        if (view is ViewGroup) {
            enableDisableViewGroup(view, enabled)
        }
    }
}
