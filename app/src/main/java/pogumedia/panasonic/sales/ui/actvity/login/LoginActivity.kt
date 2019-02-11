package pogumedia.panasonic.sales.ui.actvity.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.ToastUtil
import pogumedia.panasonic.sales.ui.actvity.forgot.ForgotActivity
import pogumedia.panasonic.sales.ui.actvity.main.MainActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import javax.inject.Inject

class LoginActivity : BaseDaggerActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var loadingDialog: LoadingDialog


    private lateinit var viewModel: LoginViewModel
    private var strToken: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        toolbar.background = null
        appBarLayout.visibility = View.INVISIBLE

        setToolbarTitle("", "")
        getAb().setDisplayHomeAsUpEnabled(false)

        etPassword.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        etPassword.transformationMethod = PasswordTransformationMethod.getInstance()


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        tvLogin.setOnClickListener {
            val strEmail = etEmail.text.toString()
            val strPass = etPassword.text.toString()
            if (viewModel.isValidationValid(strEmail, strPass)) {
                viewModel.loginService(strEmail, strPass, strToken)
            } else {
                toastUtil.showAToast(getString(R.string.label_empty_form))
            }
        }
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotActivity::class.java))
        }

        observeData()
        getTokenFCM()


    }


    private fun observeData() {
        viewModel.user.observe(this, Observer {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
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

    }

    private fun getTokenFCM() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            strToken = it.result.token
        }
    }


}



