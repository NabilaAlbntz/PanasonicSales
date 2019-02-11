package pogumedia.panasonic.sales.ui.actvity.store.location

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
import pogumedia.panasonic.sales.db.entity.Store


class StoreLocationViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val sessionManager: SessionManager) : BaseViewModel() {



}