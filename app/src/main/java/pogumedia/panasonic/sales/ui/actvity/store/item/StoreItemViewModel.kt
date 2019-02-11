package pogumedia.panasonic.sales.ui.actvity.store.item

import android.arch.lifecycle.MutableLiveData
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.SessionManager
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
import com.google.android.gms.maps.model.LatLng
import pogumedia.panasonic.sales.db.dao.*
import pogumedia.panasonic.sales.db.entity.*
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection


class StoreItemViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val provinceDao: ProvinceDao,
        private val cityDao: CityDao,
        private val productDao: ProductDao,
        private val storeDao: StoreDao,
        private val sessionManager: SessionManager,
        private val checkNetworkConnection: CheckNetworkConnection) : BaseViewModel() {


    var loadingDialogVisibility: MutableLiveData<Boolean> = MutableLiveData()
    var provinces: MutableLiveData<List<Province>> = MutableLiveData()
    var citys: MutableLiveData<List<City>> = MutableLiveData()
    var products: MutableLiveData<List<Product>> = MutableLiveData()
    var store: MutableLiveData<Store> = MutableLiveData()

    fun isValidationValid(store: Store): Boolean {
        if (store.nama.isNullOrEmpty() || store.address.isNullOrEmpty() || store.pic.isNullOrEmpty()
                || store.telp.isNullOrEmpty() ||
                store.idProvinsi == 0 || store.namaProduct.isNullOrEmpty() || store.idKabupaten == 0
                || store.latitude == "0" || store.longitude == "0") return false
        return true
    }

    fun loadProvinces() {
        async {
            await {
                provinces.postValue(provinceDao.getAll())
            }
        }
    }

    fun loadCitys(provinceId: Int) {
        async {
            await {
                citys.postValue(cityDao.getAllByProvinceId(provinceId))
            }
        }
    }

    fun loadProducts() {
        async {
            await {
                products.postValue(productDao.getAll())
            }
        }
    }

    fun submitStore(store: Store) {
        store.idUsers = sessionManager.getId()
        if (checkNetworkConnection.isConnectingToInternet) {
            store.status = 0
            submitStoreToApi(store)
        } else {
            store.status = 1
            submitStoreToDB(store)
        }
    }

    private fun submitStoreToApi(store: Store) {
        loadingDialogVisibility.value = true
        apiInterface.submitStore(
                store.idUsers,
                store.nama, store.address, store.pic, store.telp, store.latitude,
                store.longitude, store.idKabupaten, store.namaProduct,store.createdAt
        )
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
                            val id = json.getInt("id_outlet")
                            store.idOnline = id
                            submitStoreToDB(store)
                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }

                    }
                })
    }

    private fun submitStoreToDB(store: Store) {
        async {
            await {
                storeDao.insert(store)
            }
            this@StoreItemViewModel.store.value = store
        }

    }

}