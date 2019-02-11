package pogumedia.panasonic.sales.service.api

import co.metalab.asyncawait.async
import com.google.gson.Gson
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import com.google.gson.reflect.TypeToken
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.*
import pogumedia.panasonic.sales.db.entity.*


class MasterApi @Inject constructor(
        val provinceDao: ProvinceDao,
        val cityDao: CityDao,
        val gson: Gson,
        val productDao: ProductDao,
        val projectBannerDao: ProjectBannerDao,
        val signBoardDao: SignBoardDao,
        val typeRequestDao: TypeRequestDao,
        val apiInterface: ApiInterface,
        val sessionManager: SessionManager
) {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var isAreaCompleted = false
    private var isProductCompleted = false
    private var isProjectBannerCompleted = false
    private var isSignBoardCompleted = false
    private var isTypeRequestCompleted = false

    fun syncDB() {
        deleteAll()
        getAreas(sessionManager)
        getProducts()
        getProjectBanner()
        getSignBoard()
        getTypeRequest()
    }

    fun initFirstCheckDB() {
        async {
            await {
                if (provinceDao.getAll().isEmpty() || cityDao.getAll().isEmpty())
                    getAreas(sessionManager)
                if (productDao.getAll().isEmpty())
                    getProducts()
                if (projectBannerDao.getAll().isEmpty())
                    getProjectBanner()
                if (signBoardDao.getAll().isEmpty())
                    getSignBoard()
                if (typeRequestDao.getAll().isEmpty())
                    getTypeRequest()
            }
        }
    }

    fun deleteAll() {
        async {
            await {
                cityDao.deleteAll()
                productDao.deleteAll()
                projectBannerDao.deleteAll()
                provinceDao.deleteAll()
                signBoardDao.deleteAll()
                typeRequestDao.deleteAll()
            }
        }

    }


    private fun getAreas(sessionManager: SessionManager) {
        apiInterface.areas(sessionManager.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {

                    }

                    override fun onSuccess(data: ResponseBody) {
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        if (apiStatus == Constants.INT_API_STATUS) {

                            async {
                                await {
                                    val jsonArrayProvinsi = json.getJSONArray("provinsi")
                                    val jsonArrayCity = json.getJSONArray("kabupaten")
                                    for (i in 0 until jsonArrayProvinsi.length()) {
                                        val jsonObject = jsonArrayProvinsi.getJSONObject(i)
                                        val dataModel = gson.fromJson<Province>(jsonObject.toString(), Province::class.java)
                                        provinceDao.insert(dataModel)
                                    }
                                    for (i in 0 until jsonArrayCity.length()) {
                                        val jsonObject = jsonArrayCity.getJSONObject(i)
                                        val dataModel = gson.fromJson<City>(jsonObject.toString(), City::class.java)
                                        cityDao.insert(dataModel)
                                    }
                                }
                                isAreaCompleted = true
                            }

                        }
                    }
                })
    }

    private fun getProducts() {
        apiInterface.products()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {
                    }

                    override fun onSuccess(data: ResponseBody) {
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        if (apiStatus == Constants.INT_API_STATUS) {

                            async {
                                await {
                                    val jsonArray = json.getJSONArray(Constants.ITEMS)
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dataModel = gson.fromJson<Product>(jsonObject.toString(), Product::class.java)
                                        productDao.insert(dataModel)
                                    }
                                    val item = Product()
                                    item.id = productDao.getLastData().id + 1
                                    item.name = Constants.ANOTHER_OPTION
                                    productDao.insert(item)
                                }
                                isProductCompleted = true
                            }

                        }
                    }
                })
    }

    private fun getProjectBanner() {
        apiInterface.projectBanner()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {
                    }

                    override fun onSuccess(data: ResponseBody) {
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        if (apiStatus == Constants.INT_API_STATUS) {

                            async {
                                await {
                                    val jsonArray = json.getJSONArray(Constants.ITEMS)
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dataModel = gson.fromJson(jsonObject.toString(), ProjectBanner::class.java)
                                        projectBannerDao.insert(dataModel)
                                    }
                                    val item = ProjectBanner()
                                    item.id = projectBannerDao.getLastData().id + 1
                                    item.namaProject = Constants.ANOTHER_OPTION
                                    projectBannerDao.insert(item)
                                }
                                isProjectBannerCompleted = true
                            }

                        }
                    }
                })
    }

    private fun getSignBoard() {
        apiInterface.signBoard()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {
                    }

                    override fun onSuccess(data: ResponseBody) {
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        if (apiStatus == Constants.INT_API_STATUS) {

                            async {
                                await {
                                    val jsonArray = json.getJSONArray(Constants.ITEMS)
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dataModel = gson.fromJson(jsonObject.toString(), SignBoard::class.java)
                                        signBoardDao.insert(dataModel)
                                    }
                                    val item = SignBoard()
                                    item.id = signBoardDao.getLastData().id + 1
                                    item.namaSignBoard = Constants.ANOTHER_OPTION
                                    signBoardDao.insert(item)
                                }
                                isSignBoardCompleted = true
                            }

                        }
                    }
                })
    }


    private fun getTypeRequest() {
        apiInterface.typeRequest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ApiObserver<ResponseBody>(compositeDisposable) {
                    override fun onError(e: ErrorData) {
                    }

                    override fun onSuccess(data: ResponseBody) {
                        val respon = data.string()
                        val json = JSONObject(respon)
                        val apiStatus = json.getInt(Constants.API_STATUS)
                        if (apiStatus == Constants.INT_API_STATUS) {

                            async {
                                await {
                                    val jsonArray = json.getJSONArray(Constants.ITEMS)
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
                                        val dataModel = gson.fromJson(jsonObject.toString(), TypeRequest::class.java)
                                        typeRequestDao.insert(dataModel)
                                    }
                                    val item = TypeRequest()
                                    item.id = typeRequestDao.getLastData().id + 1
                                    item.namaRequest = Constants.ANOTHER_OPTION
                                    typeRequestDao.insert(item)
                                }
                            }
                            isTypeRequestCompleted = true
                        }
                    }
                })
    }

    fun isSyncCompleted(): Boolean {
        return isAreaCompleted && isProductCompleted && isProjectBannerCompleted &&
                isSignBoardCompleted && isTypeRequestCompleted
    }


}
