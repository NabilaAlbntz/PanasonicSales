package pogumedia.panasonic.sales.ui.actvity.home

import android.arch.lifecycle.MutableLiveData
import co.metalab.asyncawait.async
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.ApiObserver
import pogumedia.panasonic.sales.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import pogumedia.panasonic.sales.db.dao.StoreDao
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection
import io.reactivex.Observable


class HomeViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val storeDao: StoreDao,
        private val sessionManager: SessionManager,
        private var checkNetworkConnection: CheckNetworkConnection) : BaseViewModel() {


    var itemResults: MutableLiveData<List<Store>> = MutableLiveData()


    fun getStoresRepo(currentOffset: Int) {
        if (checkNetworkConnection.isConnectingToInternet) {
            getStoresFromApi(currentOffset)
        } else {
            getStoresFromDB()
        }
    }

    private fun getStoresFromApi(currentOffset: Int) {
        apiInterface.stores(sessionManager.getId(),currentOffset, Constants.LIMIT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {
                        error.value = e
                    }

                    override fun onSuccess(data: ResponseBody) {
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        val apiMessage = json.getString(Constants.API_MESSAGE)
                        if (apiStatus == Constants.INT_API_STATUS) {
                            val items = ArrayList<Store>()
                            async {
                                await {
                                    if (currentOffset == 0)
                                    storeDao.deleteAllByStatus()
                                    val jsonArray = json.getJSONArray(Constants.ITEMS)
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dataModel = gson.fromJson<Store>(jsonObject.toString(), Store::class.java)
                                        items.add(dataModel)

                                        if (storeDao.isStoreExistByIdOnline(dataModel.idOnline) == 0) {
                                            dataModel.idOffline = getNextKey()
                                        } else {
                                            dataModel.idOffline = storeDao.getStoreByIdOnline(dataModel.idOnline).idOffline
                                        }
                                        storeDao.insert(dataModel)
                                    }
                                    if (items.isEmpty()) {
                                        itemResults.postValue(items)
                                    } else {
                                        getStoresFromDB()
                                    }
                                }
                            }

                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }

                    }
                })
    }

    fun getStoresFromDB() {
        async {
            await {
                itemResults.postValue(storeDao.getAllHome())
            }
        }

    }

    fun getNextKey(): Int {
        var maxId = 1
        try {
            maxId = storeDao.getMaxIdValue()
        } catch (e: NullPointerException) {

        }
        var tempId = storeDao.getAll().size + 1

        while (tempId <= maxId) {
            tempId += 1
        }
        return tempId
    }

}