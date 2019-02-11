package pogumedia.panasonic.sales.ui.actvity.home


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter

import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.ui.actvity.store.search.StoreSearchActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import javax.inject.Inject
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.gson.Gson
import pogumedia.panasonic.sales.helper.util.CheckNetworkConnection
import pogumedia.panasonic.sales.helper.util.Log
import pogumedia.panasonic.sales.ui.actvity.store.item.StoreItemActivity
import pogumedia.panasonic.sales.ui.actvity.store.visit.StoreVisitActivity


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : BaseDaggerFragment(), RecyclerArrayAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var checkNetworkConnection: CheckNetworkConnection
    private lateinit var viewModel: HomeViewModel
    lateinit var adapter: RecyclerArrayAdapter<Store>
    var currentOffset = 0
    var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (!isLoaded) {
            initAdapter()
            observeData()
            viewModel.getStoresRepo(currentOffset)
        } else {
            val items = adapter.allData
            initAdapter()
            adapter.addAll(items)
            if (items.isEmpty()) onItemsEmpty()
        }


        etSearch.setOnClickListener {
            startActivity(Intent(activity, StoreSearchActivity::class.java))
        }
        btnAddStore.setOnClickListener {
            startActivityForResult(Intent(activity, StoreItemActivity::class.java).apply {
                putExtra(Constants.KEY_JSON_ITEM, gson.toJson(Store()))
            }, Constants.KEY_REQUEST_ACTIVITY)
        }

    }


    private fun initAdapter() {
        adapter = object : RecyclerArrayAdapter<Store>(activity) {
            override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
                return HomeViewHolder(parent)
            }
        }
        adapter.setOnItemClickListener {
            startActivityForResult(Intent(activity, StoreVisitActivity::class.java).apply {
                putExtra("store", gson.toJson(adapter.getItem(it)))
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
                    }
                } else {
                    adapter.addAll(it)
                }
                if (it.isNotEmpty())
                    currentOffset += Constants.LIMIT

                isLoaded = true
            }
        })
    }

    private fun onError(errorData: ErrorData) {
        rvItem.showError()
        tvDescriptionError.text = errorData.message
        btnRetryError.setOnClickListener {
            viewModel.getStoresRepo(currentOffset)
        }
    }

    private fun onItemsEmpty() {
        rvItem.showEmpty()
        ivArrowUp.visibility = View.INVISIBLE

        tvTitleMessageEmpty.text = getString(R.string.label_store_empty_title)
        tvDescriptionEmpty.text = getString(R.string.label_store_empty_desc)
        ivStateEmpty.setImageResource(R.drawable.ic_no_store)

    }


    override fun onLoadMore() {
        viewModel.getStoresRepo(currentOffset)
    }

    override fun onRefresh() {
        currentOffset = 0
        viewModel.getStoresRepo(currentOffset)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Constants.KEY_RESULT_ACTIVITY) {
            currentOffset = 0
            viewModel.getStoresRepo(currentOffset)
        }
    }
}
