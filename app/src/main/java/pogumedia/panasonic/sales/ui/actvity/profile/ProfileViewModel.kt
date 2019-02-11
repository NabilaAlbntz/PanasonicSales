package pogumedia.panasonic.sales.ui.actvity.profile

import android.arch.lifecycle.MutableLiveData
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.UserDao
import pogumedia.panasonic.sales.db.entity.User
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.ApiObserver
import pogumedia.panasonic.sales.ui.base.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import android.support.design.widget.Snackbar
import co.metalab.asyncawait.async


class ProfileViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val userDao: UserDao,
        private val sessionManager: SessionManager) : BaseViewModel() {

    var user: MutableLiveData<User> = MutableLiveData()
    var loadingDialogVisibility: MutableLiveData<Boolean> = MutableLiveData()

    fun isValidationValid(passCurrent: String,passNew : String,passReType : String): Boolean {
        if (passCurrent.isEmpty() || passNew.isEmpty() || passReType.isEmpty()) return false
        return true
    }

    fun isPassNewEqualRePass(passCurrent: String,passReType: String) : Boolean{
        if (passCurrent == passReType) return true
        return false
    }

    fun updatePassService(passCurrent: String,passNew: String) {
        loadingDialogVisibility.value = true
        apiInterface.updatePassword(sessionManager.getId(), passCurrent,passNew)
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
                            success.value = true
                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }

                    }
                })
    }

}