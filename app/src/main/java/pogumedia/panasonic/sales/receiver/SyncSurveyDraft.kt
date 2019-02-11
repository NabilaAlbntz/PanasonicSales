package pogumedia.panasonic.sales.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import co.metalab.asyncawait.async
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.StoreDao
import pogumedia.panasonic.sales.db.dao.StoreSurveyDao
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.db.entity.StoreSurvey
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.helper.util.Log
import pogumedia.panasonic.sales.service.api.ApiInterface
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.ArrayList

open class SyncSurveyDraft constructor(
        private val apiInterface: ApiInterface,
        private val storeDao: StoreDao,
        private val storeSurveyDao: StoreSurveyDao,
        private val sessionManager: SessionManager
) : BroadcastReceiver() {

    lateinit var context: Context
    var isSyncRunning = false
    var isSyncStore: Boolean = false
    var isSyncSurvey: Boolean = false
    lateinit var storesOffline: ArrayList<Store>
    lateinit var storeSurveysOffline: ArrayList<StoreSurvey>

    var TAG_STORE = "Store"
    var TAG_SURVEY = "Survey"

    var TAG_SENDING = "Sending Data"
    var TAG_SUCCESS = "Success"
    var TAG_ERROR = "Error"
    var TAG_ERROR_INTERNAL_SERVER = "Error Server"
    var TAG_ERROR_PARSING_JSON = "Error JSON"
    var TAG_ERROR_CONNECTION = "Error Connection"


    override fun onReceive(context: Context?, intent: Intent?) {
        val offlineSync = intent?.getBooleanExtra(Constants.KEY_SYNC, false)
        this.context = context!!
        if (offlineSync!!) {
            if (!isSyncRunning) {
                checkOfflineData()
            }
        }
    }


    fun checkOfflineData() {

        async {
            await {
                isSyncRunning = true
                if (!isSyncStore) {
                    storesOffline = ArrayList()
                    storesOffline.addAll(storeDao.getAllOffline())


                    if (storesOffline.size > 0) {
                        loopingUploadCustomer(0)
                    } else {
                        if (!isSyncSurvey) {
                            storeSurveysOffline = ArrayList()
                            storeSurveysOffline.addAll(storeSurveyDao.getAllOffline())

                            if (storeSurveysOffline.size > 0) {
                                loopingAddOrder(0)
                            }

                        }
                    }
                }
                Log.e("syncronize store", "finish")
                isSyncRunning = false
                flagSync("", true)
            }
        }
    }

    private fun loopingUploadCustomer(position: Int) {
        if (position < storesOffline.size) {
            createNotification(TAG_STORE, TAG_SENDING + " " + (position + 1) + "/" + storesOffline.size)
            syncCustomer(position, storesOffline[position])
        }
    }

    private fun loopingAddOrder(position: Int) {
        if (position < storeSurveysOffline.size) {
            createNotification(TAG_SURVEY, TAG_SENDING + " " + (position + 1) + "/" + storeSurveysOffline.size)
            syncSurvey(position, storeSurveysOffline[position])
        }
    }


    private fun syncCustomer(position: Int, store: Store) {
        isSyncStore = true
        flagSync(Constants.KEY_SYNC_CUSTOMER, true)
        val call: Call<ResponseBody> = apiInterface.submitStoreOffline(
                store.idUsers, store.nama, store.address, store.pic, store.telp, store.latitude,
                store.longitude, store.idKabupaten, store.namaProduct, store.createdAt
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()

                        val json = JSONObject(respon)

                        val status = json.getInt(Constants.API_STATUS)
                        val message = json.getString(Constants.API_MESSAGE)


                        if (status == 1) {

                            createNotification(TAG_STORE, TAG_SUCCESS + " " + (position + 1) + "/" + storesOffline.size)

                            val id = json.getInt("id_outlet")
                            store.idOnline = id
                            store.status = 0
                            submitStoreToDB(store)


                            if (position == storesOffline.size - 1) {
                                isSyncStore = false
                                flagSync(Constants.KEY_SYNC_CUSTOMER, false)
                                checkOfflineData()
                            } else {
                                loopingUploadCustomer(position + 1)
                            }


                        } else {
                            if (position == storesOffline.size - 1) {
                                isSyncStore = false
                                flagSync(Constants.KEY_SYNC_CUSTOMER, false)
                            } else {
                                loopingUploadCustomer(position + 1)
                            }

                            createNotification(TAG_STORE, TAG_ERROR + " " + (position + 1) + "/" + storesOffline.size)
                        }

                    } catch (e: IOException) {
                        createNotification(TAG_STORE, TAG_ERROR_PARSING_JSON + " " + (position + 1) + "/" + storesOffline.size)
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        createNotification(TAG_STORE, TAG_ERROR_PARSING_JSON + " " + (position + 1) + "/" + storesOffline.size)
                        e.printStackTrace()
                    }


                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (position == storesOffline.size - 1) {
                    isSyncStore = false
                    flagSync(Constants.KEY_SYNC_CUSTOMER, false)
                } else {
                    loopingUploadCustomer(position + 1)
                }

                createNotification(TAG_STORE, TAG_ERROR_CONNECTION + " " + (position + 1) + "/" + storesOffline.size)
            }
        })
    }

    private fun syncSurvey(position: Int, storeSurvey: StoreSurvey) {
        async {
            await {
                val store = storeDao.getStoreByIdOffline(storeSurvey.idStoreOffline)
                storeSurvey.idStoreOnline = store.idOnline
            }


            isSyncSurvey = true
            flagSync(Constants.KEY_SYNC_ORDER, true)
            val file = File(Uri.parse(storeSurvey.fotoAktual).path)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val multipartBody = MultipartBody.Part.createFormData("foto_aktual", file.name, requestFile)

            val call = apiInterface.submitStoreSurveyOffline(
                    storeSurvey.idStoreOnline, sessionManager.getId(), storeSurvey.locLatitude, storeSurvey.locLongitude,
                    storeSurvey.product, storeSurvey.banner, storeSurvey.ukuranBanner,
                    storeSurvey.signBoard, storeSurvey.typeRequest, storeSurvey.note, storeSurvey.inRadius.toString(),
                    storeSurvey.createdAt, multipartBody
            )

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val respon = response.body()!!.string()

                            val json = JSONObject(respon)

                            val status = json.getInt(Constants.API_STATUS)
                            val message = json.getString(Constants.API_MESSAGE)

                            if (status == 1) {
                                deleteSurveyHistory(storeSurvey)
                                createNotification(TAG_SURVEY, TAG_SUCCESS + " " + (position + 1) + "/" + storeSurveysOffline.size)
                                if (position == storeSurveysOffline.size - 1) {
                                    isSyncSurvey = false
                                    flagSync(Constants.KEY_SYNC_ORDER, false)
                                } else {
                                    loopingAddOrder(position + 1)
                                }

                            } else {
                                createNotification(TAG_SURVEY, TAG_ERROR + " " + (position + 1) + "/" + storeSurveysOffline.size)
                                if (position == storeSurveysOffline.size - 1) {
                                    isSyncSurvey = false
                                    flagSync(Constants.KEY_SYNC_ORDER, false)
                                } else {
                                    loopingAddOrder(position + 1)
                                }
                            }

                        } catch (e: IOException) {
                            e.printStackTrace()
                            createNotification(TAG_SURVEY, TAG_ERROR_PARSING_JSON + " " + (position + 1) + "/" + storeSurveysOffline.size)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            createNotification(TAG_SURVEY, TAG_ERROR_PARSING_JSON + " " + (position + 1) + "/" + storeSurveysOffline.size)
                        }


                    } else {
                        if (position == storeSurveysOffline.size - 1) {
                            isSyncSurvey = false
                            flagSync(Constants.KEY_SYNC_ORDER, false)
                        } else {
                            loopingAddOrder(position + 1)
                        }

                        createNotification(TAG_SURVEY, TAG_ERROR_INTERNAL_SERVER + " " + (position + 1) + "/" + storeSurveysOffline.size)
                    }


                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (position == storeSurveysOffline.size - 1) {
                        isSyncSurvey = false
                        flagSync(Constants.KEY_SYNC_ORDER, false)
                    } else {
                        loopingAddOrder(position + 1)
                    }

                    createNotification(TAG_SURVEY, TAG_ERROR_CONNECTION + " " + (position + 1) + "/" + storeSurveysOffline.size)
                }
            })
        }
    }


    private fun submitStoreToDB(store: Store) {
        async {
            await {
                storeDao.insert(store)
            }
        }

    }

    private fun deleteSurveyHistory(item: StoreSurvey) {
        async {
            await {
                storeSurveyDao.deleteSurveyHistory(item)
            }
        }
    }


    private fun createNotification(title: String, message: String) {
        val b = NotificationCompat.Builder(context)
        b.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("Info")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, b.build())
    }

    private fun flagSync(type: String, sync: Boolean) {
        val intent = Intent("Change")
        intent.putExtra(type, sync)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.sendBroadcast(intent)
    }


}