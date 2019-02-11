package pogumedia.panasonic.sales.ui.actvity.store.visit

import android.arch.lifecycle.MutableLiveData
import android.location.Location
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
import android.location.Location.distanceBetween
import com.google.android.gms.maps.model.LatLng


class StoreVisitViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val userDao: UserDao,
        private val sessionManager: SessionManager) : BaseViewModel() {

    var storeItem: MutableLiveData<Store> = MutableLiveData()


    fun showItemToView(strItemJson: String) {
        val item = gson.fromJson<Store>(strItemJson, Store::class.java)
        storeItem.value = item
    }


    fun calculateDistance(startLocation: LatLng, endLocation: LatLng): Double {
        val results = FloatArray(3)
        Location.distanceBetween(startLocation.latitude, startLocation.longitude,
                endLocation.latitude, endLocation.longitude, results)
        return results[0].toDouble()
    }


}