package pogumedia.panasonic.sales.ui.actvity.store.visit

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentSender
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.MenuItem
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.helper.dialog.BasicTitleDialog
import pogumedia.panasonic.sales.helper.glocation.LocationLiveData
import pogumedia.panasonic.sales.helper.util.*
import pogumedia.panasonic.sales.ui.actvity.store.survey.item.StoreSurveyItemActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import pogumedia.panasonic.sales.ui.base.ViewModelFactory
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import kotlinx.android.synthetic.main.activity_store_visit.*
import javax.inject.Inject

class StoreVisitActivity : BaseDaggerActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var toastUtil: ToastUtil


    private lateinit var viewModel: StoreVisitViewModel
    lateinit var mMap: GoogleMap
    var isChange = false
    var strStoreItem = ""
    var latLngSales: LatLng? = null
    var latLngStore: LatLng? = null
    var mMarker: Marker? = null
    var isInRadius = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_visit)
        setToolbarTitle(getString(R.string.label_store_visit_title), "")

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoreVisitViewModel::class.java)
        observeData()

        isChange = intent.getBooleanExtra("isChange", false)
        strStoreItem = intent.getStringExtra("store")

        setupButtonBg()
        setupMap()
        checkLocationPermission()
        btnSubmit.setOnClickListener {
            if (latLngSales != null && latLngStore != null) {
                if (viewModel.calculateDistance(latLngSales!!, latLngStore!!) < 200) {
                    isInRadius = true
                    gotoStoreSurvey()
                } else {
                    isInRadius = false
                    showDialogForceCheckIn()
                }
            } else {
                toastUtil.showAToast(getString(R.string.label_alert_store_visit_waiting_location))
            }
        }


    }

    private fun gotoStoreSurvey() {
        startActivity(Intent(this, StoreSurveyItemActivity::class.java).apply {
            putExtra("sales_lat", latLngSales!!.latitude)
            putExtra("sales_long", latLngSales!!.longitude)
            putExtra("store", strStoreItem)
            putExtra("isInRadius", isInRadius)
        })
    }

    private fun observeData() {
        viewModel.storeItem.observe(this, Observer {
            it?.let { it1 -> showItem(it1) }
        })
    }

    private fun setupButtonBg() {
        val background = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }
        btnSubmit.background = background
    }

    private fun showItem(it: Store) {
        tvStoreName.text = it.nama
        setViewSpannable(it)
        tvStoreAddress.text = it.address
        latLngStore = LatLng(it.latitude?.toDouble()!!, it.longitude?.toDouble()!!)
        showLocationToMap(latLngStore!!, R.drawable.ic_store_position)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun createMarker(latLng: LatLng, iconMarker: Int) {
        mMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(iconMarker))
                .position(latLng))
    }

    private fun focusPointingToLocation(latLng: LatLng) {
        val position = CameraPosition.Builder()
                .target(latLng)
                .zoom(16f)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }


    private fun checkLocationPermission() {
        if (getAndroidSdkVersion() >= getSdkMVersion()) {
            askPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION) {
                onGranted {
                    getLocationLiveData()
                }
                onDenied {
                    toastUtil.showAToast(getString(R.string.label_permission_denied_location))
                }
                onShowRationale { request ->
                    request.retry()
                }
            }
        } else {
            getLocationLiveData()
        }
    }

    private fun showLocationToMap(latLng: LatLng, iconMarker: Int) {
        createMarker(latLng, iconMarker)
        focusPointingToLocation(latLng)
    }

    private fun showUserLocation(latLng: LatLng, iconMarker: Int) {
        mMarker = mMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(iconMarker))
                .position(latLng))
        focusPointingToLocation(latLng)
    }

    private fun setViewSpannable(data: Store) {
        val ssBuilder = SpannableStringBuilder(data.pic + "   " + data.telp)
        val myIcon = resources.getDrawable(R.drawable.bg_circle)
        myIcon.setBounds(0, 0, myIcon.intrinsicWidth, myIcon.intrinsicHeight)
        val imgSpan = ImageSpan(myIcon, ImageSpan.ALIGN_BASELINE)
        ssBuilder.setSpan(
                imgSpan,
                data.pic?.length!! + 1,
                data.pic?.length!! + 2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvStoreCustomer.text = ssBuilder
    }


    override fun onMapReady(map: GoogleMap) {
        mMap = map
        val position = CameraPosition.Builder()
                .target(LatLng(-6.107019, 107.317104))
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
        viewModel.showItemToView(strStoreItem)
    }

    private fun getLocationLiveData() {
        val locationLiveData = LocationLiveData.create(
                this,
                interval = 500,
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY,
                expirationTime = 5000,
                fastestInterval = 100,
                maxWaitTime = 1000,
                numUpdates = 10,
                smallestDisplacement = 10f,
                onErrorCallback = object : LocationLiveData.OnErrorCallback {
                    override fun onLocationSettingsException(e: ApiException) {
                        if (e is ResolvableApiException) {
                            try {
                                e.startResolutionForResult(this@StoreVisitActivity, 1)
                            } catch (sendEx: IntentSender.SendIntentException) {
                            }
                        }
                    }

                    override fun onPermissionsMissing() {
                    }
                }
        )
        locationLiveData.observe(this, Observer {
            if(it!=null) {
                latLngSales = LatLng(it?.latitude!!, it.longitude)
                if (mMarker != null) mMarker?.remove()
                showUserLocation(latLngSales!!, R.drawable.ic_sales_position)
            }
        })

    }


    private fun showDialogForceCheckIn() {
        val bgPositive = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }

        BasicTitleDialog(this, R.style.Dialog)
                .setTitleVisibility(false)
                .setMessage(getString(R.string.label_alert_store_visit_force_check_in))
                .setOnlyText()
                .setMessageGravity(Gravity.CENTER)
                .setPositiveButton(R.string.label_dialog_yes)
                .setPositiveButtonColor(ContextCompat.getColor(this, R.color.color_white))
                .setPositiveButtonBackground(bgPositive)
                .setNegativeButtonBackground(ContextCompat.getDrawable(this, R.drawable.bg_outline_btn))
                .setNegativeButtonColor(ContextCompat.getColor(this, R.color.color_blue))
                .setNegativeButton(R.string.label_dialog_no)
                .setButtonPositiveClickListener {
                    it.dismiss()
                    gotoStoreSurvey()
                }
                .setButtonNegativeClickListener {
                    it.dismiss()
                }
                .show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        if (isChange) {
            setResult(Constants.KEY_RESULT_ACTIVITY)
        }
        finish()
    }
}
