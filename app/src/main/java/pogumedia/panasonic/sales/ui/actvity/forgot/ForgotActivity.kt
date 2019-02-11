package pogumedia.panasonic.sales.ui.actvity.forgot

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.view.Gravity
import android.view.View
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.helper.dialog.BasicTitleDialog
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.ToastUtil
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import kotlinx.android.synthetic.main.activity_forgot.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject
import pogumedia.panasonic.sales.R.id.appBarLayout
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import pogumedia.panasonic.sales.helper.util.setColorDrawable


class ForgotActivity : BaseDaggerActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var loadingDialog: LoadingDialog
    private lateinit var viewModel: ForgotViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ForgotViewModel::class.java)
        observeData()

        tvSubmit.setOnClickListener {
            val strEmail = etEmail.text.toString()
            if (viewModel.isValidationValid(strEmail)) {
                viewModel.forgotService(strEmail)
            } else {
                toastUtil.showAToast(getString(R.string.label_empty_form))
            }
        }
    }


    private fun observeData() {
        viewModel.error.observe(this, Observer {
            if (it != null) {
                toastUtil.showAToast(it.message)
            }
        })
        viewModel.loadingDialogVisibility.observe(this, Observer {
            if (it == true) {
                loadingDialog.show(supportFragmentManager)
            } else {
                loadingDialog.dismiss()
            }
        })
        viewModel.isSuccess.observe(this, Observer {
            if (it == true) {
                val bgPositive = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
                    setColorDrawable(this,
                            it, R.color.color_blue)
                }

                BasicTitleDialog(this, R.style.Dialog)
                        .setOnlyText()
                        .setTitle(R.string.label_dialog_forgot_success_title)
                        .setMessage(R.string.label_dialog_forgot_success)
                        .setMessageGravity(Gravity.CENTER)
                        .setSingleButton()
                        .setPositiveButtonBackground(bgPositive)
                        .setPositiveButton(R.string.label_dialog_ok)
                        .setButtonPositiveClickListener { dialog ->
                            dialog.dismiss()
                            finish()
                        }
                        .show()
            }
        })
    }

}
