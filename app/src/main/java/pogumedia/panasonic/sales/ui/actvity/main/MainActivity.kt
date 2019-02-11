package pogumedia.panasonic.sales.ui.actvity.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.widget.Toast
import co.metalab.asyncawait.async
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.StoreDao
import pogumedia.panasonic.sales.db.dao.StoreSurveyDao
import pogumedia.panasonic.sales.db.dao.TypeRequestDao
import pogumedia.panasonic.sales.db.dao.UserDao
import pogumedia.panasonic.sales.db.entity.User
import pogumedia.panasonic.sales.helper.backstacks.FragNavController
import pogumedia.panasonic.sales.helper.backstacks.FragmentHistory
import pogumedia.panasonic.sales.helper.dialog.BasicTitleDialog
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection
import pogumedia.panasonic.sales.helper.util.EndDrawerToggle
import pogumedia.panasonic.sales.helper.util.ToastUtil
import pogumedia.panasonic.sales.helper.util.setColorDrawable
import pogumedia.panasonic.sales.ui.actvity.home.HomeFragment
import pogumedia.panasonic.sales.ui.actvity.login.LoginActivity
import pogumedia.panasonic.sales.ui.actvity.profile.ProfileFragment
import pogumedia.panasonic.sales.ui.actvity.store.survey.history.StoreSurveyHistoryFragment
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject
import pogumedia.panasonic.sales.service.api.MasterApi
import java.util.*


class MainActivity : BaseDaggerActivity(), FragNavController.TransactionListener, FragNavController.RootFragmentListener {


    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var loadingDialog: LoadingDialog
    @Inject
    lateinit var checkNetworkConnection: CheckNetworkConnection
    @Inject
    lateinit var userDao: UserDao
    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var masterApi: MasterApi
    @Inject
    lateinit var storeDao: StoreDao
    @Inject
    lateinit var surveyDao: StoreSurveyDao
    @Inject
    lateinit var storeSurveyDao: StoreSurveyDao

    private lateinit var drawerToggle: EndDrawerToggle
    private lateinit var adapter: MainAdapter
    private var menuItems = ArrayList<String>()

    private lateinit var mNavController: FragNavController
    private lateinit var fragmentHistory: FragmentHistory
    private var doubleBackToExitPressedOnce = false
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkSession(sessionManager)
        setupToolbar(0)
        setupMenuItems()
        setupNavigationMenu()

        masterApi.initFirstCheckDB()



        fragmentHistory = FragmentHistory()
        mNavController = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.flContent)
                .transactionListener(this)
                .rootFragmentListener(this, 4)
                .build()
        mNavController.switchTab(FragNavController.TAB1)

        ivCloseDrawer.setOnClickListener {
            drawerToggle.toggle()
        }
        tvLogout.setOnClickListener {
            dlContent.closeDrawers()
            async {
                var isSyncCompleted = false
                await {
                    if (storeDao.getAllOffline().isEmpty() && surveyDao.getAllOffline().isEmpty()) {
                        isSyncCompleted = true
                    }
                }
                if (isSyncCompleted) showDialogLogout() else showDialogSync()
            }
        }


    }

    private fun setupToolbar(position: Int) {
        getAb().setDisplayHomeAsUpEnabled(false)
        if (position == 0) {
            toolbar.background = null
            setToolbarTitle("", "")
        } else if (position == 1) {
            toolbar.background = null
            setToolbarTitle(getString(R.string.label_survey_history_title), "")
        } else {
            toolbar.background = ContextCompat.getDrawable(this, R.drawable.bg_toolbar_gradient)
            setToolbarTitle(getString(R.string.label_profile_change_password_title), "")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.outlineProvider = null
        }
    }


    private fun checkSession(sessionManager: SessionManager) {
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            var user = User()
            async {
                await {
                    user = userDao.getUser(sessionManager.getId())
                }
                tvName.text = user.userNama
                tvEmail.text = user.userEmail
            }

        }
    }

    private fun setupNavigationMenu() {
        drawerToggle = EndDrawerToggle(this,
                dlContent,
                toolbar,
                R.drawable.ic_menu_drawer,
                R.string.drawer_open,
                R.string.drawer_close)

        dlContent.addDrawerListener(drawerToggle)
    }

    private fun createMenuItems() {
        menuItems.add(getString(R.string.label_home_title))
        menuItems.add(getString(R.string.label_survey_history_title))
        menuItems.add(getString(R.string.label_profile_change_password_title))
        menuItems.add(getString(R.string.label_refresh_data_title))
    }

    private fun setupMenuItems() {
        createMenuItems()
        adapter = MainAdapter(menuItems) {
            if (it == 3) {
                showDialogRefreshData()
                dlContent.closeDrawers()
            } else {
                fragmentHistory.push(it)
                switchFragment(it)
            }
        }
        rvItem.layoutManager = LinearLayoutManager(this)
        rvItem.adapter = adapter
    }

    private fun switchFragment(position: Int) {
        setupToolbar(position)
        mNavController.switchTab(position)
        currentPosition = position
        adapter.setSelectedPosition = position
        dlContent.closeDrawers()
        adapter.notifyDataSetChanged()
    }


    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            FragNavController.TAB1 -> return HomeFragment()
            FragNavController.TAB2 -> return StoreSurveyHistoryFragment()
            FragNavController.TAB3 -> return ProfileFragment()
        }
        throw IllegalStateException("Need to send an index that we know")
    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {

    }

    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType?) {

    }

    override fun onBackPressed() {
        if (dlContent.isDrawerOpen(Gravity.END)) {
            dlContent.closeDrawers()
        } else {
            if (fragmentHistory.isEmpty) {
                closeAppWhenOpen()
            } else {
                if (fragmentHistory.stackSize > 1) {
                    val position = fragmentHistory.popPrevious()
                    switchFragment(position)
                } else {
                    switchFragment(FragNavController.TAB1)
                    fragmentHistory.emptyStack()
                }
            }
        }

    }

    private fun showDialogLogout() {
        val bgPositive = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }

        BasicTitleDialog(this, R.style.Dialog)
                .setTitle(R.string.label_dialog_title)
                .setMessage(getString(R.string.label_alert_account_logout))
                .setOnlyText()
                .setMessageGravity(Gravity.CENTER)
                .setPositiveButton(R.string.label_dialog_yes)
                .setPositiveButtonColor(ContextCompat.getColor(this, R.color.color_white))
                .setPositiveButtonBackground(bgPositive)
                .setNegativeButtonBackground(ContextCompat.getDrawable(this, R.drawable.bg_outline_btn))
                .setNegativeButtonColor(ContextCompat.getColor(this, R.color.color_blue))
                .setNegativeButton(R.string.label_dialog_no)
                .setButtonPositiveClickListener {
                    async {
                        await {
                            sessionManager.createLoginSession(0, false)
                            userDao.deleteAll()
                            storeDao.deleteAll()
                            storeSurveyDao.deleteAll()
                            masterApi.deleteAll()
                        }
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                        it.dismiss()
                    }


                }
                .setButtonNegativeClickListener {
                    it.dismiss()
                }
                .show()


    }


    private fun showDialogSync() {
        val bgPositive = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }

        BasicTitleDialog(this, R.style.Dialog)
                .setTitleVisibility(false)
                .setMessage(getString(R.string.label_alert_sync_required))
                .setOnlyText()
                .setSingleButton()
                .setMessageGravity(Gravity.CENTER)
                .setPositiveButton(R.string.label_dialog_ok)
                .setPositiveButtonColor(ContextCompat.getColor(this, R.color.color_white))
                .setPositiveButtonBackground(bgPositive)
                .setButtonPositiveClickListener {
                    it.dismiss()
                }
                .show()
    }

    private fun showDialogRefreshData() {
        val bgPositive = ContextCompat.getDrawable(this, R.drawable.bg_btn_store)?.let {
            setColorDrawable(this,
                    it, R.color.color_blue)
        }

        BasicTitleDialog(this, R.style.Dialog)
                .setTitle(R.string.label_dialog_title)
                .setMessage(getString(R.string.label_alert_refresh_data))
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
                    masterApi.syncDB()
                    checkIsSyncCompleted()
                }
                .setButtonNegativeClickListener {
                    it.dismiss()
                }
                .show()
    }

    private fun checkIsSyncCompleted() {
        if (checkNetworkConnection.isConnectingToInternet) {
            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (masterApi.isSyncCompleted()) {
                        loadingDialog.dismiss()
                        runOnUiThread {
                            toastUtil.showAToast(getString(R.string.label_refresh_data_success_title), Toast.LENGTH_LONG)
                        }

                        timer.cancel()
                    }
                }

            }, 1000, 2000)
        } else {
            loadingDialog.dismiss()
            toastUtil.showAToast(getString(R.string.label_alert_network_error_no_connection))
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mNavController.onSaveInstanceState(outState)
    }

    private fun closeAppWhenOpen() {
        if (doubleBackToExitPressedOnce) {
            toastUtil.cancelToast()
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        toastUtil.showAToast(getString(R.string.alert_close_app))

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onDestroy() {
        if (!masterApi.compositeDisposable.isDisposed)
            masterApi.compositeDisposable.dispose()
        super.onDestroy()
    }
}
