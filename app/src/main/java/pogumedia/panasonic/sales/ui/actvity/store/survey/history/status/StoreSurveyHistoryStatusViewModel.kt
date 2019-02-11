package pogumedia.panasonic.sales.ui.actvity.store.survey.history.status

import android.arch.lifecycle.MutableLiveData
import co.metalab.asyncawait.async
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.StoreDao
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.service.api.ApiObserver
import pogumedia.panasonic.sales.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import pogumedia.panasonic.sales.db.dao.StoreSurveyDao
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.db.entity.StoreSurvey
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection
import pogumedia.panasonic.sales.helper.util.Log


class StoreSurveyHistoryStatusViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val storeDao: StoreDao,
        private val storeSurveyDao: StoreSurveyDao,
        private var checkNetworkConnection: CheckNetworkConnection,
        private val sessionManager: SessionManager) : BaseViewModel() {


    var itemResults: MutableLiveData<List<StoreSurvey>> = MutableLiveData()
    var isDraft: Boolean = false

    fun getSurveysRepo(currentOffset: Int, isDraft: Boolean) {
        this@StoreSurveyHistoryStatusViewModel.isDraft = isDraft
        if (checkNetworkConnection.isConnectingToInternet && !isDraft) {
            getStoresFromApi(currentOffset)
        } else {
            getStoresFromDB(isDraft)
        }
    }

    private fun getStoresFromApi(currentOffset: Int) {
        apiInterface.historySurvey(sessionManager.getId(), currentOffset, Constants.LIMIT)
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
                            val items = ArrayList<StoreSurvey>()

                            async {
                                await {
                                    if (currentOffset == 0)
                                        storeSurveyDao.deleteAllByStatus()
                                    val jsonArray = json.getJSONArray("riwayat")
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dataModel = gson.fromJson<StoreSurvey>(jsonObject.toString(), StoreSurvey::class.java)
                                        items.add(dataModel)

                                        dataModel.idStoreOnline = dataModel.store.idOnline

                                        if (storeDao.isStoreExistByIdOnline(dataModel.idStoreOnline) == 0) {
                                            dataModel.store.idOffline = getNextKey()
                                            dataModel.idStoreOffline = dataModel.store.idOffline

                                            if (dataModel.store.isReject || dataModel.store.isDelete) {
                                                dataModel.store.status = Constants.KEY_STATUS_DELETE
                                            } else {
                                                dataModel.store.status = Constants.KEY_STATUS_SERVER
                                            }

                                        } else {
                                            dataModel.idStoreOffline = storeDao.getStoreByIdOnline(dataModel.idStoreOnline).idOffline
                                            dataModel.store.idOffline = dataModel.idStoreOffline


                                            if (dataModel.store.isReject || dataModel.store.isDelete) {
                                                dataModel.store.status = Constants.KEY_STATUS_DELETE
                                            } else {
                                                dataModel.store.status = Constants.KEY_STATUS_SERVER
                                            }


                                        }
                                        storeDao.insert(dataModel.store)

                                        if (storeSurveyDao.isSurveyExistByIdOnline(dataModel.idOnline) == 0) {
                                            dataModel.idOffline = getNextKeyStoreSurvey()
                                            storeSurveyDao.insert(dataModel)
                                        }
                                    }
                                }

                                if (items.isEmpty()) {
                                    itemResults.postValue(items)
                                } else {
                                    getStoresFromDB(isDraft)
                                }

                            }

                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }

                    }
                })
    }

    private fun getStoresFromDB(isDraft: Boolean) {
        var items: List<StoreSurvey>
        async {
            await {
                if (isDraft) {
                    items = storeSurveyDao.getAllByStatusOffline(1)
                    items.forEach {
                        if (storeDao.isStoreByIdOfflineExist(it.idStoreOffline) != 0) {
                            val store = storeDao.getStoreByIdOffline(it.idStoreOffline)
                            it.store = store
                        }
                    }
                } else {
                    items = storeSurveyDao.getAllByStatus(0)
                    items.forEach {
                        if (storeDao.isStoreExistByIdOnline(it.idStoreOnline) != 0) {
                            val store = storeDao.getStoreByIdOnline(it.idStoreOnline)
                            it.store = store
                        }
                    }
                }
                itemResults.postValue(items)
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


    fun getNextKeyStoreSurvey(): Int {
        var maxId = 1
        try {
            maxId = storeSurveyDao.getMaxIdValue()
        } catch (e: NullPointerException) {

        }
        var tempId = storeSurveyDao.getAll().size + 1

        while (tempId <= maxId) {
            tempId += 1
        }
        return tempId
    }

}