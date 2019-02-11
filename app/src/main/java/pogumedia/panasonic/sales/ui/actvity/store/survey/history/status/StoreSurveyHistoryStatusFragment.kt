package pogumedia.panasonic.sales.ui.actvity.store.survey.history.status


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.metalab.asyncawait.async
import com.google.gson.Gson
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter

import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.SessionManager
import pogumedia.panasonic.sales.db.dao.StoreDao
import pogumedia.panasonic.sales.db.dao.StoreSurveyDao
import pogumedia.panasonic.sales.db.entity.StoreSurvey
import pogumedia.panasonic.sales.helper.dialog.LoadingDialog
import pogumedia.panasonic.sales.helper.util.*
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.receiver.SyncSurveyDraft
import pogumedia.panasonic.sales.service.api.ApiInterface
import pogumedia.panasonic.sales.ui.actvity.store.survey.item.StoreSurveyItemActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerFragment
import kotlinx.android.synthetic.main.fragment_store_survey_history_status.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import java.util.*
import javax.inject.Inject

class StoreSurveyHistoryStatusFragment : BaseDaggerFragment(), RecyclerArrayAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    private val ARG_PARAM1 = "isDraft"

    private var isDraft: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var checkNetworkConnection: CheckNetworkConnection
    @Inject
    lateinit var toastUtil: ToastUtil
    @Inject
    lateinit var apiInterface: ApiInterface
    @Inject
    lateinit var storeDao: StoreDao
    @Inject
    lateinit var surveyDao: StoreSurveyDao
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var loadingDialog: LoadingDialog


    private lateinit var viewModel: StoreSurveyHistoryStatusViewModel
    lateinit var adapter: RecyclerArrayAdapter<StoreSurvey>
    var currentOffset = 0
    var isLoaded = false
    lateinit var changeOutboxReceiver: BroadcastReceiver
    lateinit var syncReceiver: BroadcastReceiver
    lateinit var intentFilter: IntentFilter
    var timer: Timer? = null


    companion object {
        @JvmStatic
        fun newInstance(isDraft: Boolean) =
                StoreSurveyHistoryStatusFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_PARAM1, isDraft)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isDraft = it.getBoolean(ARG_PARAM1)
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoreSurveyHistoryStatusViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_store_survey_history_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (!isLoaded) {
            initAdapter()
            viewModel.getSurveysRepo(currentOffset, isDraft)
            observeData()
        } else {
            val items = adapter.allData
            initAdapter()
            adapter.addAll(items)
            if (items.isEmpty()) {
                onItemsEmpty()
            }
        }
        setupButtonBg()

        hideVisibleBtnUpload()

        btnUpload.setOnClickListener {
            if (checkNetworkConnection.isConnectingToInternet) {
                loadingDialog.show(activity?.supportFragmentManager)
                hitReceiver()
            } else
                toastUtil.showAToast(getString(R.string.label_alert_network_error_no_connection))
        }

        intentFilter = IntentFilter("Change")
        changeOutboxReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                async {
                    var isSyncDone = false
                    await {
                        if (storeDao.getAllOffline().isEmpty() && surveyDao.getAllOffline().isEmpty()) {
                            isSyncDone = true
                        }
                        if (isSyncDone) {
                            if (loadingDialog.isVisible) {
                                loadingDialog.dismiss()
                                rvItem.setRefreshing(true, true)
                                activity?.unregisterReceiver(syncReceiver)
                                timer?.cancel()
                            }
                        }
                    }
                }

            }
        }
    }

    private fun checkIsSyncCompleted() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (storeDao.getAllOffline().isEmpty() && surveyDao.getAllOffline().isEmpty()) {
                    if (loadingDialog.isVisible) {
                        loadingDialog.dismiss()
                        rvItem.setRefreshing(true, true)
                        activity?.unregisterReceiver(syncReceiver)
                    }

                    timer?.cancel()
                }
            }

        }, 1000, 2000)
    }

    private fun hitReceiver() {
        val filter = IntentFilter("")
        syncReceiver = SyncSurveyDraft(apiInterface, storeDao, surveyDao, sessionManager)
        activity?.registerReceiver(syncReceiver, filter)
        val intent = Intent("")
        intent.putExtra(Constants.KEY_SYNC, true)
        activity?.sendBroadcast(intent)
        checkIsSyncCompleted()
    }

    private fun setupButtonBg() {
        val background = ContextCompat.getDrawable(activity!!, R.drawable.bg_btn_store)?.let {
            setColorDrawable(activity!!,
                    it, R.color.color_blue)
        }
        btnUpload.background = background
    }

    private fun initAdapter() {
        adapter = object : RecyclerArrayAdapter<StoreSurvey>(activity) {
            override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
                return StoreSurveyHistoryStatusViewHolder(parent)
            }
        }
        adapter.setOnItemClickListener {
            startActivityForResult(Intent(activity, StoreSurveyItemActivity::class.java).apply {
                putExtra("isFromHistory", true)
                putExtra("survey_item", gson.toJson(adapter.getItem(it)))
            }, 99)
        }

        if (checkNetworkConnection.isConnectingToInternet)
            adapter.setMore(R.layout.view_more, this)

        adapter.setNoMore(R.layout.view_nomore)
        adapter.setError(R.layout.view_error)

        val gridLayoutManager = LinearLayoutManager(activity)
        rvItem.setLayoutManager(gridLayoutManager)
        rvItem.setAdapterWithProgress(adapter)
        rvItem.setRefreshListener(this)


    }

    private fun observeData() {
        viewModel.error.observe(this, Observer { it ->
            if (it != null) {
                onError(it)
            }
        })

        viewModel.itemResults.observe(this, Observer {
            if (it != null) {
                if (currentOffset == 0) {
                    adapter.clear()
                }
                if (currentOffset == 0 && it.size < Constants.LIMIT) {
                    if (it.isEmpty()) {
                        onItemsEmpty()
                    } else {
                        adapter.clear()
                        adapter.addAll(it)
                        adapter.stopMore()
                        hideVisibleBtnUpload()
                    }
                } else {
                    adapter.addAll(it)
                    hideVisibleBtnUpload()
                }
                if (adapter.allData.isNotEmpty())
                    currentOffset += Constants.LIMIT
                isLoaded = true
            }
        })
    }

    private fun hideVisibleBtnUpload() {
        if (isDraft && adapter.allData.size != 0) {
            btnUpload.visibility = View.VISIBLE
        } else {
            btnUpload.visibility = View.GONE
        }
    }

    private fun onError(errorData: ErrorData) {
        rvItem.showError()
        tvDescriptionError.text = errorData.message
        btnRetryError.setOnClickListener {
            viewModel.getSurveysRepo(currentOffset, isDraft)
        }
    }

    private fun onItemsEmpty() {
        rvItem.showEmpty()
        ivArrowUp.visibility = View.INVISIBLE
        btnUpload.visibility = View.GONE

        tvTitleMessageEmpty.text = getString(R.string.label_survey_history_empty_title)
        tvDescriptionEmpty.text = getString(R.string.label_survey_history_empty_desc)
        ivStateEmpty.setImageResource(R.drawable.ic_no_survey_history)
    }


    override fun onLoadMore() {
        if (!isDraft) {
            viewModel.getSurveysRepo(currentOffset, isDraft)
        } else {
            adapter.stopMore()
        }
    }

    override fun onRefresh() {
        currentOffset = 0
        viewModel.getSurveysRepo(currentOffset, isDraft)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Constants.KEY_RESULT_ACTIVITY) {
            currentOffset = 0
            viewModel.getSurveysRepo(currentOffset, isDraft)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.registerReceiver(changeOutboxReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(changeOutboxReceiver)
    }

}
