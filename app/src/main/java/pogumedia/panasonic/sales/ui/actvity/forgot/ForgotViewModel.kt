package pogumedia.panasonic.sales.ui.actvity.forgot

import android.arch.lifecycle.MutableLiveData
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.ApiObserver
import pogumedia.panasonic.sales.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class ForgotViewModel @Inject constructor(
        private val apiInterface: ApiInterface) : BaseViewModel() {

    var loadingDialogVisibility: MutableLiveData<Boolean> = MutableLiveData()
    var isSuccess: MutableLiveData<Boolean> = MutableLiveData()

    fun isValidationValid(email: String): Boolean {
        if (email.isEmpty()) return false
        return true
    }

    fun forgotService(pass: String) {
        loadingDialogVisibility.value = true
        apiInterface.forgot(pass)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {
                        loadingDialogVisibility.value = false
                        error.value = e
                    }

                    override fun onSuccess(data: ResponseBody) {
                        loadingDialogVisibility.value = false
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        val apiMessage = json.getString(Constants.API_MESSAGE)
                        if (apiStatus == Constants.INT_API_STATUS) {
                            isSuccess.value = true
                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }

                    }
                })
    }
}