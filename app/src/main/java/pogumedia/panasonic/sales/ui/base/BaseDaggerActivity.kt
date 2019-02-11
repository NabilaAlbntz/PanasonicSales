package pogumedia.panasonic.sales.ui.base

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import pogumedia.panasonic.sales.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*

@SuppressLint("Registered")
open class BaseDaggerActivity : DaggerAppCompatActivity() {


    private var ab: ActionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT



    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        setupToolbar()
    }


    private fun setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            ab = supportActionBar
            if (ab != null) {
                ab?.setDisplayShowTitleEnabled(false)
                ab?.setDisplayHomeAsUpEnabled(true)
                ab?.setHomeButtonEnabled(true)
            }
        }
    }

    fun setToolbarTitle(ivLogo: Drawable?) {
        ivToolbarIcon.visibility = View.VISIBLE
        ivToolbarIcon.setImageDrawable(ivLogo)
        tvToolbarTitle.visibility = View.GONE
        tvSubtitleToolbar.visibility = View.GONE
    }

    fun setToolbarTitle(str_title: String, subtitle: String?) {
        ivToolbarIcon.visibility = View.GONE
        tvToolbarTitle.visibility = View.VISIBLE
        tvToolbarTitle.text = str_title

        if (subtitle != null && !subtitle.isEmpty()) {
            tvSubtitleToolbar.visibility = View.VISIBLE
            tvSubtitleToolbar.text = subtitle
        } else {
            tvSubtitleToolbar.visibility = View.GONE
        }
    }



    fun getToolbars(): Toolbar {
        return toolbar
    }

    fun getAb(): ActionBar {
        return ab!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}