package pogumedia.panasonic.sales.ui.actvity.store.location

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentSender
import android.graphics.Point
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import kotlinx.android.synthetic.main.activity_store_location.*
import javax.inject.Inject
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import pogumedia.panasonic.sales.helper.util.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import pogumedia.panasonic.sales.helper.glocation.LocationLiveData
import pogumedia.panasonic.sales.helper.gplace.CustomAutoCompleteAdapter


class StoreLocationActivity : BaseDaggerActivity(), OnMapReadyCallback, AdapterView.OnItemClickListener, pogumedia.panasonic.sales.helper.gplace.Listener {


    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: StoreLocationViewModel
    lateinit var mMap: GoogleMap
    var mMapIsTouched = false
    var isCalled = false
    var isFirstInit = true
    var latLng = LatLng(0.toDouble(), 0.toDouble())
    lateinit var adapterLocation: CustomAutoCompleteAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_location)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoreLocationViewModel::class.java)
        setupMap()
        checkLocationPermission()
        tvBack.setOnClickListener { finish() }

        btnSetLocation.setOnClickListener {
            val intent = Intent().apply {
                putExtra("latitude", latLng.latitude)
                putExtra("longitude", latLng.longitude)
            }
            setResult(Constants.KEY_RESULT_ACTIVITY, intent)
            finish()
        }




        adapterLocation = CustomAutoCompleteAdapter(this)
        adapterLocation.setListener(this)
        acLocation.setAdapter(adapterLocation)
        acLocation.onItemClickListener = this


    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val placeId = adapterLocation.dataList[position].placeId
        adapterLocation.getPlaceFromPlaceID(placeId)
    }


    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onPlaceSuccess(place: Place) {
        isFirstInit = true
        showLocationToMap(place.latLng.latitude, place.latLng.longitude)
    }

    private fun createMarker(latLng: LatLng, iconMarker: Int) {
        mMap.addMarker(MarkerOptions()
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

    private fun showLocationToMap(lat: Double, lng: Double) {
        val latLng = LatLng(lat, lng)
        //createMarker(latLng, R.drawable.ic_store_position)
        focusPointingToLocation(latLng)
    }


    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap.setOnCameraIdleListener {
            latLng = mMap.cameraPosition.target
            if (!isFirstInit) {
                animateMarker(50F)
            } else {
                isFirstInit = false
            }
            isCalled = false
        }

        map.setOnCameraMoveListener {
            if (mMapIsTouched) {
                if (!isCalled) {
                    animateMarker(-50F)
                    isCalled = true
                }
            }
        }
        val position = CameraPosition.Builder()
                .target(LatLng(-6.107019, 107.317104))
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> if (!mMapIsTouched) {
                mMapIsTouched = true
            }
            MotionEvent.ACTION_UP -> mMapIsTouched = false
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun animateMarker(value: Float) {
        val point = Point()
        window.windowManager.defaultDisplay.getSize(point)
        llMarkerLocation.animate().translationYBy(value).start()
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
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                e.startResolutionForResult(this@StoreLocationActivity, 1)
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }
                        }
                    }

                    override fun onPermissionsMissing() {
                    }
                }
        )
        locationLiveData.observe(this, Observer {
            val latLng = LatLng(it?.latitude!!, it.longitude)
            isFirstInit = true
            focusPointingToLocation(latLng)
        })

    }

}
