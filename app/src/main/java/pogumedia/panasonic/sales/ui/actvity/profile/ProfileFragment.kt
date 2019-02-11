package pogumedia.panasonic.sales.ui.actvity.profile


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.helper.dialog.BasicTitleDialog
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.ToastUtil
import pogumedia.panasonic.sales.helper.util.setColorDrawable
import pogumedia.panasonic.sales.helper.util.setupEtPassword
import pogumedia.panasonic.sales.ui.base.BaseDaggerFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject


class ProfileFragment : BaseDaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var loadingDialog: LoadingDialog
    @Inject
    lateinit var toastUtil: ToastUtil
    private lateinit var viewModel: ProfileViewModel
    var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEtPassword(etPasswordCurrent)
        setupEtPassword(etPasswordNew)
        setupEtPassword(etPasswordRetype)
        setupButtonBg()
        if (!isLoaded)
            observeData()

        btnSubmit.setOnClickListener {
            val strPassCurrent = etPasswordCurrent.text.toString()
            val strPassNew = etPasswordNew.text.toString()
            val strPassReType = etPasswordRetype.text.toString()
            if (viewModel.isValidationValid(strPassCurrent, strPassNew, strPassReType)) {
                if (viewModel.isPassNewEqualRePass(strPassNew, strPassReType)) {
                    viewModel.updatePassService(strPassCurrent, strPassNew)
                } else {
                    toastUtil.showAToast(getString(R.string.label_alert_profile_password_retype))
                }
            } else {
                toastUtil.showAToast(getString(R.string.label_empty_form))
            }

        }
        btnSubmit.background = setupButtonBg()

    }


    private fun observeData() {
        viewModel.error.observe(this, Observer {
            if (it != null) {
                toastUtil.showAToast(it.message)
            }
            isLoaded = true
        })
        viewModel.loadingDialogVisibility.observe(this, Observer {
            if (it == true) {
                loadingDialog.show(activity?.supportFragmentManager)
            } else {
                loadingDialog.dismiss()
            }
            isLoaded = true
        })
        viewModel.success.observe(this, Observer {
            if (it == true) {
                showDialogSuccess()
            }
            isLoaded = true
        })
    }

    private fun setupButtonBg(): Drawable? {
        val background = ContextCompat.getDrawable(activity!!, R.drawable.bg_btn_store)?.let {
            setColorDrawable(activity!!,
                    it, R.color.color_blue)
        }
        return background
    }

    private fun showDialogSuccess() {
        BasicTitleDialog(activity, R.style.Dialog)
                .setTitle(R.string.label_dialog_title)
                .setOnlyText()
                .setMessage(R.string.label_alert_profile_password_dialog)
                .setMessageGravity(Gravity.CENTER)
                .setSingleButton()
                .setPositiveButtonBackground(setupButtonBg())
                .setPositiveButton(R.string.label_dialog_ok)
                .setButtonPositiveClickListener { dialog ->
                    dialog.dismiss()
                    resetForm()
                }
                .show()
    }

    private fun resetForm() {
        etPasswordCurrent.setText("")
        etPasswordNew.setText("")
        etPasswordRetype.setText("")
    }


}
