package pogumedia.panasonic.sales.ui.actvity.store.item

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.City
import pogumedia.panasonic.sales.db.entity.Product
import pogumedia.panasonic.sales.db.entity.Province
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.ToastUtil
import pogumedia.panasonic.sales.helper.util.setColorDrawable
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import kotlinx.android.synthetic.main.activity_store_item.*
import javax.inject.Inject
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.helper.dialog.BasicTitleDialog
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.helper.util.DateUtil
import pogumedia.panasonic.sales.ui.actvity.store.location.StoreLocationActivity
import pogumedia.panasonic.sales.ui.actvity.store.survey.item.StoreSurveyItemActivity
import pogumedia.panasonic.sales.ui.actvity.store.visit.StoreVisitActivity


class StoreItemActivity : BaseDaggerActivity(), AdapterView.OnItemSelectedListener, OnMapReadyCallback {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var loadingDialog: LoadingDialog

    private lateinit var viewModel: StoreItemViewModel

    var provinceSelected = Province()
    lateinit var adapterProvince: ArrayAdapter<*>
    var provinces = ArrayList<Province>()
    var citySelected = City()
    lateinit var adapterCity: ArrayAdapter<*>
    var citys = ArrayList<City>()
    var productSelected = Product()
    lateinit var adapterProduct: ArrayAdapter<*>
    var products = ArrayList<Product>()
    var latitude: Double = 0.0
    var longitude: Double = 0.0


    private lateinit var googleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_item)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoreItemViewModel::class.java)

        setToolbarTitle(getString(R.string.label_store_item_title), "")

        setupMap()
        setupButtonBg()
        setupProvince()
        setupCity()
        setupProduct()
        observeData()
        tilProduct.visibility = View.GONE
        viewModel.loadProvinces()
        viewModel.loadProducts()

        ivAddLocation.setOnClickListener {
            gotoStoreLocation()
        }

        tvAddLocation.setOnClickListener {
            gotoStoreLocation()
        }


        btnSubmit.setOnClickListener {
            val store = Store()

            val strProduct = etProductOptional.text.toString()

            store.nama = etStoreName.text.toString()
            store.address = etStoreAddress.text.toString()
            store.pic = etStorePic.text.toString()
            store.telp = etStorePhoneNumber.text.toString()
            store.idProvinsi = provinceSelected.id
            store.namaProvinsi = productSelected.name
            store.idKabupaten = citySelected.id
            store.namaKabupaten = citySelected.namaKabupaten
            store.latitude = latitude.toString()
            store.longitude = longitude.toString()
            store.namaProduct = strProduct
            store.createdAt = DateUtil.getCurrDate(DateUtil.FORMAT_ONE)

            if (viewModel.isValidationValid(store)) {
                viewModel.submitStore(store)
            } else {
                toastUtil.showAToast(getString(R.string.label_empty_form))
            }
        }

    }

    private fun gotoStoreLocation() {
        startActivityForResult(
                Intent(this, StoreLocationActivity::class.java),
                Constants.KEY_REQUEST_ACTIVITY)
    }

    private fun observeData() {
        viewModel.provinces.observe(this, Observer {
            it?.let { it1 -> provinces.addAll(it1) }
            adapterProvince.notifyDataSetChanged()
        })
        viewModel.citys.observe(this, Observer {
            it?.let { it1 -> citys.addAll(it1) }
            adapterCity.notifyDataSetChanged()
        })
        viewModel.products.observe(this, Observer {
            it?.let { it1 -> products.addAll(it1) }
            adapterProduct.notifyDataSetChanged()
        })
        viewModel.loadingDialogVisibility.observe(this, Observer {
            if (it == true) {
                loadingDialog.show(supportFragmentManager)
            } else {
                loadingDialog.dismiss()
            }
        })
        viewModel.error.observe(this, Observer {
            it?.message?.let { it1 -> toastUtil.showAToast(it1) }
        })
        viewModel.store.observe(this, Observer {
            startActivity(Intent(this, StoreVisitActivity::class.java).apply {
                putExtra("isChange", true)
                putExtra("store", gson.toJson(it))
            })
            finish()
        })
    }

    private fun setupProvince() {
        adapterProvince = ArrayAdapter(this, R.layout.spinner_item, provinces)
        adapterProvince.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spProvince.adapter = adapterProvince
        spProvince.onItemSelectedListener = this
        addPlaceHolderProvince(getString(R.string.label_store_item_select_province))
    }

    private fun setupCity() {
        adapterCity = ArrayAdapter(this, R.layout.spinner_item, citys)
        adapterCity.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spCity.adapter = adapterCity
        spCity.onItemSelectedListener = this
        addPlaceHolderCity(getString(R.string.label_store_item_select_city))
    }

    private fun setupProduct() {
        adapterProduct = ArrayAdapter(this, R.layout.spinner_item, products)
        adapterProduct.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spStoreProduct.adapter = adapterProduct
        spStoreProduct.onItemSelectedListener = this
        addPlaceHolderProduct(getString(R.string.label_store_item_select_product))
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun setupButtonBg() {
        val background = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }
        btnSubmit.background = background
    }

    private fun addPlaceHolderProvince(message: String) {
        val item = Province(0, message)
        provinces.add(item)
        adapterProvince.notifyDataSetChanged()
    }

    private fun addPlaceHolderCity(message: String) {
        val item = City(0, 0, message)
        citys.add(item)
        adapterCity.notifyDataSetChanged()
    }

    private fun addPlaceHolderProduct(message: String) {
        val item = Product(0, message)
        products.add(item)
        adapterProduct.notifyDataSetChanged()
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spProvince -> {
                spProvince.setSelection(position)
                provinceSelected = provinces[position]
                citys.clear()
                addPlaceHolderCity(getString(R.string.label_store_item_select_city))
                if (provinceSelected.id != 0) {
                    provinceSelected.id.let { viewModel.loadCitys(it) }
                }
            }
            R.id.spCity -> {
                spCity.setSelection(position)
                citySelected = citys[position]
            }
            R.id.spStoreProduct -> {
                spStoreProduct.setSelection(position)
                productSelected = products[position]
                if (productSelected.name.equals(Constants.ANOTHER_OPTION)) {
                    tilProduct.visibility = View.VISIBLE
                    etProductOptional.setText("")
                } else {
                    tilProduct.visibility = View.GONE
                    etProductOptional.setText(productSelected.name)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.setOnMapClickListener {
            gotoStoreLocation()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Constants.KEY_RESULT_ACTIVITY) {
            llAddLocation.visibility = View.GONE
            flMapLocation.visibility = View.VISIBLE

            latitude = data?.getDoubleExtra("latitude", 0.toDouble())!!
            longitude = data.getDoubleExtra("longitude", 0.toDouble())


            val marker = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                    .position(LatLng(latitude, longitude))
            val position = CameraPosition.Builder()
                    .target(LatLng(latitude, longitude))
                    .zoom(16F)
                    .build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
            googleMap.addMarker(marker)
        }
    }
}
