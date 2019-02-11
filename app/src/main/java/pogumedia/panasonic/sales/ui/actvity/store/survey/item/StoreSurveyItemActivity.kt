package pogumedia.panasonic.sales.ui.actvity.store.survey.item

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.*
import pogumedia.panasonic.sales.helper.dialog.BasicTitleDialog
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.image.ImageHelper
import pogumedia.panasonic.sales.helper.image.ImageHelper.CAMERA_REQUEST_CODE
import pogumedia.panasonic.sales.helper.image.ImageHelper.CAMERA_RESULT_CODE
import pogumedia.panasonic.sales.helper.image.PicassoHelper
import pogumedia.panasonic.sales.helper.image.advancedConfig
import pogumedia.panasonic.sales.helper.image.handleCrop
import pogumedia.panasonic.sales.helper.util.*
import pogumedia.panasonic.sales.ui.actvity.main.MainActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_store_survey_item.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import javax.inject.Inject

class StoreSurveyItemActivity : BaseDaggerActivity(), AdapterView.OnItemSelectedListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var loadingDialog: LoadingDialog
    @Inject
    lateinit var checkNetworkConnection: CheckNetworkConnection


    private lateinit var viewModel: StoreSurveyItemViewModel

    var projectBanner = ProjectBanner()
    lateinit var adapterProjectBanner: ArrayAdapter<*>
    var projectBanners = ArrayList<ProjectBanner>()

    var signBoard = SignBoard()
    lateinit var adapterSignBoard: ArrayAdapter<*>
    var signBoards = ArrayList<SignBoard>()

    var typeRequest = TypeRequest()
    lateinit var adapterTypeRequest: ArrayAdapter<*>
    var typeRequests = ArrayList<TypeRequest>()

    var productSelected = Product()
    lateinit var adapterProduct: ArrayAdapter<*>
    var products = ArrayList<Product>()

    var locLatitude: Double = 0.0
    var locLongitude: Double = 0.0
    var store = ""
    var isFromHistory = false
    var fileCropped: File? = null
    lateinit var photoFileCache: File
    var isInRadius = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_survey_item)


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoreSurveyItemViewModel::class.java)
        isFromHistory = intent.getBooleanExtra("isFromHistory", false)
        observeData(isFromHistory)

        if (isFromHistory) {
            setToolbarTitle(getString(R.string.label_store_survey_history_title), "")
            val strSurveyItem = intent.getStringExtra("survey_item")
            viewModel.showItemToView(strSurveyItem)
            tilTaskStatus.visibility = View.VISIBLE
        } else {
            setToolbarTitle(getString(R.string.label_store_survey_title), "")
            store = intent.getStringExtra("store")
            locLatitude = intent.getDoubleExtra("sales_lat", 0.0)
            locLongitude = intent.getDoubleExtra("sales_long", 0.0)
            tilTaskStatus.visibility = View.GONE
            viewModel.setupStore(store)
        }
        isInRadius = intent.getBooleanExtra("isInRadius", true)

        tilSignBoard.visibility = View.GONE
        tilProduct.visibility = View.GONE
        tilProjectBanner.visibility = View.GONE
        tilTypeRequest.visibility = View.GONE




        setupProjectBanner()
        setupSignBoard()
        setupTypeRequest()
        setupProduct()
        setupButtonBg()

        btnSubmit.setOnClickListener {
            val strDimenLength = etDimensionLength.text.toString()
            val strDimenWidth = etDimensionWidth.text.toString()
            val strDimenSide = etDimensionSide.text.toString()
            val strNote = etNote.text.toString()

            val strProjectBanner = etProjectBannerOptional.text.toString()
            val strProduct = etProductOptional.text.toString()
            val strSignBoard = etSignBoardOptional.text.toString()
            val strTypeRequest = etTypeRequestOptional.text.toString()


            val storeSurvey = StoreSurvey()
            storeSurvey.note = strNote
            storeSurvey.fotoAktual = if (fileCropped != null) Uri.fromFile(fileCropped).toString() else ""
            storeSurvey.locLatitude = locLatitude.toString()
            storeSurvey.locLongitude = locLongitude.toString()

            storeSurvey.signBoard = strSignBoard
            storeSurvey.product = strProduct
            storeSurvey.banner = strProjectBanner
            storeSurvey.typeRequest = strTypeRequest
            storeSurvey.inRadius = isInRadius
            storeSurvey.createdAt = DateUtil.getCurrDate(DateUtil.FORMAT_ONE)

            if (viewModel.isValidationValid(storeSurvey, strDimenLength, strDimenWidth, strDimenSide)) {
                showDialogSubmit(storeSurvey, strDimenLength, strDimenWidth, strDimenSide)

            } else {
                toastUtil.showAToast(getString(R.string.label_empty_form))
            }
        }

        ivItem.setOnClickListener {
            checkImagePermission()
        }


    }

    private fun setupProjectBanner() {
        adapterProjectBanner = ArrayAdapter(this, R.layout.spinner_item, projectBanners)
        adapterProjectBanner.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spProjectBanner.adapter = adapterProjectBanner
        spProjectBanner.onItemSelectedListener = this
        addPlaceHolderProjectBanner(getString(R.string.label_survey_history_select_project_banner))
    }

    private fun setupSignBoard() {
        adapterSignBoard = ArrayAdapter(this, R.layout.spinner_item, signBoards)
        adapterSignBoard.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spSignBoard.adapter = adapterSignBoard
        spSignBoard.onItemSelectedListener = this
        addPlaceHolderSignBoard(getString(R.string.label_survey_history_select_sign_board))
    }

    private fun setupTypeRequest() {
        adapterTypeRequest = ArrayAdapter(this, R.layout.spinner_item, typeRequests)
        adapterTypeRequest.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spTypeRequest.adapter = adapterTypeRequest
        spTypeRequest.onItemSelectedListener = this
        addPlaceHolderTypeRequest(getString(R.string.label_survey_history_select_type_request))
    }

    private fun setupProduct() {
        adapterProduct = ArrayAdapter(this, R.layout.spinner_item, products)
        adapterProduct.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spProduct.adapter = adapterProduct
        spProduct.onItemSelectedListener = this
        addPlaceHolderProduct(getString(R.string.label_store_item_select_product))
    }

    private fun addPlaceHolderProjectBanner(message: String) {
        val item = ProjectBanner(0, message)
        projectBanners.add(item)
        adapterProjectBanner.notifyDataSetChanged()
    }

    private fun addPlaceHolderSignBoard(message: String) {
        val item = SignBoard(0, message)
        signBoards.add(item)
        adapterSignBoard.notifyDataSetChanged()
    }

    private fun addPlaceHolderTypeRequest(message: String) {
        val item = TypeRequest(0, message)
        typeRequests.add(item)
        adapterTypeRequest.notifyDataSetChanged()
    }

    private fun addPlaceHolderProduct(message: String) {
        val item = Product(0, message)
        products.add(item)
        adapterProduct.notifyDataSetChanged()
    }

    private fun setupButtonBg() {
        val background = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }
        btnSubmit.background = background
    }

    private fun checkImagePermission() {
        if (getAndroidSdkVersion() >= getSdkMVersion()) {
            askPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
                onGranted {
                    EasyImage.openCamera(this@StoreSurveyItemActivity, 0)
                }
                onDenied {
                    toastUtil.showAToast(getString(R.string.label_permission_denied_location))
                }
            }
        } else {
            EasyImage.openCamera(this@StoreSurveyItemActivity, 0)
        }
    }

    private fun observeData(isFromHistory: Boolean) {
        viewModel.products.observe(this, Observer {
            it?.let { it1 -> products.addAll(it1) }
            adapterProduct.notifyDataSetChanged()
        })
        viewModel.projectBanners.observe(this, Observer {
            it?.let { it1 -> projectBanners.addAll(it1) }
            adapterProjectBanner.notifyDataSetChanged()
        })
        viewModel.signBoards.observe(this, Observer {
            it?.let { it1 -> signBoards.addAll(it1) }
            adapterSignBoard.notifyDataSetChanged()
        })
        viewModel.typeRequests.observe(this, Observer {
            it?.let { it1 -> typeRequests.addAll(it1) }
            adapterTypeRequest.notifyDataSetChanged()
        })
        viewModel.success.observe(this, Observer {
            loadingDialog.dismiss()
            val menuHome = Intent(this@StoreSurveyItemActivity, MainActivity::class.java)
            menuHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(menuHome)
            if (checkNetworkConnection.isConnectingToInternet)
                toastUtil.showAToast(getString(R.string.label_alert_store_survey_submitted), Toast.LENGTH_LONG)
            else
                toastUtil.showAToast(getString(R.string.label_alert_store_survey_submitted_draft), Toast.LENGTH_LONG)
        })
        viewModel.error.observe(this, Observer {
            loadingDialog.dismiss()
            it?.message?.let { it1 -> toastUtil.showAToast(it1) }
        })


        if (!isFromHistory) {
            viewModel.loadProducts()
            viewModel.loadProjectBanner()
            viewModel.loadSignBoard()
            viewModel.loadTypeRequest()
        } else {
            viewModel.storeSurvey.observe(this, Observer {
                it?.let { it1 -> showItemToView(it1) }
            })
        }
    }

    private fun splitDimension(dimension: String) {
        val separated = dimension.split("x".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in 0 until separated.size) {
            if (i == 0) etDimensionLength.setText(separated[0])
            if (i == 1) etDimensionWidth.setText(separated[1])
            if (i == 2) etDimensionSide.setText(separated[2])
        }
    }

    private fun showItemToView(item: StoreSurvey) {
        btnSubmit.visibility = View.GONE
        etNoteHistory.setText(item.note)
        tvHintSurveyItem.visibility = View.GONE
        tilDate.visibility = View.VISIBLE
        setViewTransparentBackground()
        enableDisableViewGroup(llContent, false)
        showPhotoPreview(item.fotoAktual!!, true)

        val date = DateUtil.stringtoDate(item.createdAt, DateUtil.FORMAT_ONE)
        etDate.setText(DateUtil.dateToString(date, "dd MMMM yyyy"))
        item.ukuranBanner?.let { splitDimension(it) }

        lllUploadFile.visibility = View.GONE
        signBoards.clear()
        products.clear()
        projectBanners.clear()
        typeRequests.clear()
        item.signBoard?.let { addPlaceHolderSignBoard(it) }
        item.product?.let { addPlaceHolderProduct(it) }
        item.banner?.let { addPlaceHolderProjectBanner(it) }
        item.typeRequest?.let { addPlaceHolderTypeRequest(it) }

        etTaskStatus.setText(item.statusSurvey)
        etTaskStatus.setTextColor(Color.parseColor(item.warna))

    }

    private fun showPhotoPreview(file: Any, isUrl: Boolean) {
        if (isUrl)
            PicassoHelper.loadImageFromUrl(this, file as String, ivItem)
        else
            PicassoHelper.loadImageFromFile(this, file as File, ivItem)

        ivCamera.setColorFilter(ContextCompat.getColor(this, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY)
        tvDescription.setTextColor(ContextCompat.getColor(this, R.color.color_white))
    }

    private fun setViewTransparentBackground() {
        etDate.background = null
        spProduct.background = null
        spTypeRequest.background = null
        spSignBoard.background = null
        spProjectBanner.background = null
        etDimensionLength.background = null
        etDimensionWidth.background = null
        etDimensionSide.background = null
        etNote.visibility = View.GONE
        etNoteHistory.setPadding(0, 16, 16, 16)
        etNoteHistory.visibility = View.VISIBLE
    }

    private fun showDialogSubmit(storeSurvey: StoreSurvey, strDimenLength: String,
                                 strDimenWidth: String, strDimenSide: String) {
        val bgPositive = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }

        BasicTitleDialog(this, R.style.Dialog)
                .setTitleVisibility(false)
                .setMessage(getString(R.string.label_alert_store_survey_submit))
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
                    loadingDialog.show(supportFragmentManager)
                    viewModel.submitSurvey(storeSurvey, strDimenLength, strDimenWidth, strDimenSide)
                }
                .setButtonNegativeClickListener {
                    it.dismiss()
                }
                .show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spProjectBanner -> {
                spProjectBanner.setSelection(position)
                projectBanner = projectBanners[position]
                if (projectBanner.namaProject.equals(Constants.ANOTHER_OPTION)) {
                    tilProjectBanner.visibility = View.VISIBLE
                    etProjectBannerOptional.setText("")
                } else {
                    tilProjectBanner.visibility = View.GONE
                    etProjectBannerOptional.setText(projectBanner.namaProject)
                }
            }
            R.id.spSignBoard -> {
                spSignBoard.setSelection(position)
                signBoard = signBoards[position]

                if (signBoard.namaSignBoard.equals(Constants.ANOTHER_OPTION)) {
                    tilSignBoard.visibility = View.VISIBLE
                    etSignBoardOptional.setText("")
                } else {
                    tilSignBoard.visibility = View.GONE
                    etSignBoardOptional.setText(signBoard.namaSignBoard)
                }

            }
            R.id.spTypeRequest -> {
                spTypeRequest.setSelection(position)
                typeRequest = typeRequests[position]

                if (typeRequest.namaRequest.equals(Constants.ANOTHER_OPTION)) {
                    tilTypeRequest.visibility = View.VISIBLE
                    etTypeRequestOptional.setText("")
                } else {
                    tilTypeRequest.visibility = View.GONE
                    etTypeRequestOptional.setText(typeRequest.namaRequest)
                }
            }
            R.id.spProduct -> {
                spProduct.setSelection(position)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == UCrop.REQUEST_CROP) and (data != null)) {
            photoFileCache.delete()
            fileCropped = handleCrop(data!!)
            if (fileCropped != null)
                showPhotoPreview(fileCropped!!, false)
            else
                Toast.makeText(this, "Can't retrieve image", Toast.LENGTH_SHORT).show()
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
                override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {

                }

                override fun onImagesPicked(imagesFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                    photoFileCache = imagesFiles[0].absoluteFile

                    val str_uri = Uri.fromFile(photoFileCache)
                    val destination = Uri.fromFile(File(cacheDir, imagesFiles[0].name))

                    var uCrop = UCrop.of(str_uri, destination)
                    uCrop = advancedConfig(uCrop, this@StoreSurveyItemActivity)
                    uCrop.start(this@StoreSurveyItemActivity)
                }

                override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                    if (source === EasyImage.ImageSource.CAMERA) {
                        val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@StoreSurveyItemActivity)
                        photoFile?.delete()
                    }
                }
            })
        }
    }

}
