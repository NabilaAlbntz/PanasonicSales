package pogumedia.panasonic.sales.ui.actvity.login

import android.arch.lifecycle.MutableLiveData
import android.provider.SyncStateContract
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import android.support.design.widget.Snackbar
import co.metalab.asyncawait.async
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.UserDao
import pogumedia.panasonic.sales.db.entity.User
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.ApiObserver
import pogumedia.panasonic.sales.ui.base.BaseViewModel


class LoginViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val userDao: UserDao,
        private val sessionManager: SessionManager) : BaseViewModel() {

    var user: MutableLiveData<User> = MutableLiveData()
    var loadingDialogVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val error: Any

    fun isValidationValid(email: String, pass: String): Boolean {
        if (email.isEmpty() || pass.isEmpty()) return false
        return true
    }

    fun loginService(email: String, pass: String, regId: String?) {
        loadingDialogVisibility.value = true
        apiInterface.login(email, pass, "Sales", regId)
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
                        val apiStatus = json.getInt(SyncStateContract.Constants.API_STATUS)
                        val apiMessage = json.getString(Constants.API_MESSAGE)
                        if (apiStatus == Constants.INT_API_STATUS) {
                            val item = gson.fromJson<User>(json.toString(), User::class.java)
                            async {
                                await {
                                    userDao.insert(item)
                                }
                                item.id?.let { sessionManager.createLoginSession(it, true) }
                                user.value = item
                            }

                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }

                    }
                })
    }

}