package pogumedia.panasonic.sales.ui.actvity.store.survey.item

import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.ui.base.BaseViewModel
import javax.inject.Inject
import co.metalab.asyncawait.async
import pogumedia.panasonic.sales.db.dao.*
import pogumedia.panasonic.sales.db.entity.*
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.helper.util.Log
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.service.api.ApiObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File


class StoreSurveyItemViewModel @Inject constructor(
        private val apiInterface: ApiInterface,
        private val gson: Gson,
        private val projectBannerDao: ProjectBannerDao,
        private val signBoardDao: SignBoardDao,
        private val typeRequestDao: TypeRequestDao,
        private val productDao: ProductDao,
        private val sessionManager: SessionManager,
        private val storeSurveyDao: StoreSurveyDao,
        private val checkNetworkConnection: CheckNetworkConnection) : BaseViewModel() {


    var projectBanners: MutableLiveData<List<ProjectBanner>> = MutableLiveData()
    var signBoards: MutableLiveData<List<SignBoard>> = MutableLiveData()
    var typeRequests: MutableLiveData<List<TypeRequest>> = MutableLiveData()
    var products: MutableLiveData<List<Product>> = MutableLiveData()
    var loadingDialogVisibility: MutableLiveData<Boolean> = MutableLiveData()
    var storeSurvey: MutableLiveData<StoreSurvey> = MutableLiveData()
    lateinit var store: Store


    fun isValidationValid(storeSurvey: StoreSurvey, strDimenLength: String,
                          strDimenWidth: String, strDimenSide: String): Boolean {
        if (strDimenLength.isEmpty() && strDimenWidth.isEmpty() && strDimenSide.isEmpty()) {
            if (!storeSurvey.fotoAktual.isNullOrEmpty() &&
                    !storeSurvey.signBoard.isNullOrEmpty() && !storeSurvey.product.isNullOrEmpty() &&
                    !storeSurvey.banner.isNullOrEmpty() && !storeSurvey.typeRequest.isNullOrEmpty()
            ) return true
            return false
        } else {
            if (strDimenLength.isEmpty() || strDimenWidth.isEmpty() || strDimenSide.isEmpty())
                return false
            if (!storeSurvey.fotoAktual.isNullOrEmpty() &&
                    !storeSurvey.signBoard.isNullOrEmpty() && !storeSurvey.product.isNullOrEmpty() &&
                    !storeSurvey.banner.isNullOrEmpty() && !storeSurvey.typeRequest.isNullOrEmpty()
            ) return true
            return false
        }
    }

    fun setupStore(strStore: String) {
        store = gson.fromJson(strStore, Store::class.java)
    }

    fun showItemToView(strSurveyItem: String) {
        storeSurvey.value = gson.fromJson(strSurveyItem, StoreSurvey::class.java)
    }


    fun loadProjectBanner() {
        async {
            await {
                projectBanners.postValue(projectBannerDao.getAll())
            }
        }
    }

    fun loadSignBoard() {
        async {
            await {
                signBoards.postValue(signBoardDao.getAll())
            }
        }
    }

    fun loadTypeRequest() {
        async {
            await {
                typeRequests.postValue(typeRequestDao.getAll())
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

    fun submitSurvey(storeSurvey: StoreSurvey, strDimenLength: String,
                     strDimenWidth: String, strDimenSide: String) {

        val sizeBanner =
                if (strDimenLength.isNotEmpty() && strDimenWidth.isNotEmpty() && strDimenSide.isNotEmpty())
                    "$strDimenLength x $strDimenWidth x $strDimenSide"
                else ""

        storeSurvey.ukuranBanner = sizeBanner
        storeSurvey.idStoreOnline = store.idOnline
        storeSurvey.idStoreOffline = store.idOffline

        if (checkNetworkConnection.isConnectingToInternet && storeSurvey.idStoreOnline != 0) {
            storeSurvey.status = Constants.KEY_STATUS_SERVER
            submitStoreToApi(storeSurvey)
        } else {
            async {
                await {
                    storeSurvey.idOffline = getNextKey()
                }
                storeSurvey.statusSurvey = "Draft"
                storeSurvey.status = Constants.KEY_STATUS_NEW
                submitSurveyToDB(storeSurvey)
            }

        }
    }


    private fun submitStoreToApi(storeSurvey: StoreSurvey) {
        loadingDialogVisibility.value = true

        val file = File(Uri.parse(storeSurvey.fotoAktual).path)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val multipartBody = MultipartBody.Part.createFormData("foto_aktual", file.name, requestFile)

        apiInterface.submitStoreSurvey(
                storeSurvey.idStoreOnline, sessionManager.getId(), storeSurvey.locLatitude, storeSurvey.locLongitude,
                storeSurvey.product, storeSurvey.banner, storeSurvey.ukuranBanner,
                storeSurvey.signBoard, storeSurvey.typeRequest, storeSurvey.note, storeSurvey.inRadius.toString(),
                storeSurvey.createdAt, multipartBody
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
                            val id = json.getInt("id_survey")
                            storeSurvey.idOnline = id
                            storeSurvey.statusSurvey = "On Review"
                            submitSurveyToDB(storeSurvey)
                        } else {
                            error.value = ErrorData(message = apiMessage)
                        }
                    }
                })
    }


    private fun submitSurveyToDB(storeSurvey: StoreSurvey) {
        async {
            await {
                storeSurveyDao.insert(storeSurvey)
            }
            success.value = true
        }
    }

    fun getNextKey(): Int {
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