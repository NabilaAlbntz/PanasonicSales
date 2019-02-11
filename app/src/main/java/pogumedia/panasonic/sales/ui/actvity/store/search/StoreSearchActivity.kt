package pogumedia.panasonic.sales.ui.actvity.store.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.gson.Gson
import com.jude.easyrecyclerview.adapter.BaseViewHolder
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.db.entity.Store
import pogumedia.panasonic.sales.helper.util.Constants
import pogumedia.panasonic.sales.helper.util.hideKeyboard
import pogumedia.panasonic.sales.model.ErrorData
import pogumedia.panasonic.sales.ui.actvity.home.HomeViewHolder
import pogumedia.panasonic.sales.ui.actvity.store.visit.StoreVisitActivity
import pogumedia.panasonic.sales.ui.base.BaseDaggerActivity
import kotlinx.android.synthetic.main.activity_store_search.*
import kotlinx.android.synthetic.main.view_empty.*
import kotlinx.android.synthetic.main.view_error.*
import javax.inject.Inject

class StoreSearchActivity : BaseDaggerActivity(), RecyclerArrayAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var gson: Gson

    private lateinit var viewModel: StoreSearchViewModel
    lateinit var adapter: RecyclerArrayAdapter<Store>
    var currentOffset = 0
    var strQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_search)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StoreSearchViewModel::class.java)
        initAdapter()
        observeData()
        setupETSearch()
        rvItem.progressView.visibility = View.INVISIBLE

        tvCancel.setOnClickListener { finish() }
        tvResult.text = getString(R.string.label_store_search_result, 0)
    }


    private fun setupETSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                strQuery = s.toString()
            }
        })

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                tvResult.text = getString(R.string.label_store_search_result, 0)
                currentOffset = 0
                rvItem.progressView.visibility = View.VISIBLE
                rvItem.showProgress()
                hideKeyboard(this)
                viewModel.getStoresRepo(strQuery, currentOffset)
                true
            } else false
        }
    }

    private fun initAdapter() {
        adapter = object : RecyclerArrayAdapter<Store>(this) {
            override fun OnCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
                return HomeViewHolder(parent)
            }
        }
        adapter.setOnItemClickListener {
            startActivityForResult(Intent(this, StoreVisitActivity::class.java).apply {
                putExtra("store", gson.toJson(adapter.getItem(it)))
            }, 99)
        }



        adapter.setMore(R.layout.view_more, this)
        adapter.setNoMore(R.layout.view_nomore)

        val gridLayoutManager = LinearLayoutManager(this)
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
                tvResult.text = getString(R.string.label_store_search_result, adapter.allData.size)
                currentOffset += Constants.LIMIT
            }
        })
    }

    private fun onError(errorData: ErrorData) {
        rvItem.showError()
        tvDescriptionError.text = errorData.message
        btnRetryError.setOnClickListener {
            viewModel.getStoresRepo(strQuery, currentOffset)
        }
    }

    private fun onItemsEmpty() {
        rvItem.showEmpty()
        tvTitleMessageEmpty.text = getString(R.string.label_store_empty_title)
        tvDescriptionEmpty.text = getString(R.string.label_store_empty_desc_2)
        ivStateEmpty.setImageResource(R.drawable.ic_no_search_result)
    }


    override fun onLoadMore() {
        viewModel.getStoresRepo(strQuery, currentOffset)
    }

    override fun onRefresh() {
        currentOffset = 0
        viewModel.getStoresRepo(strQuery, currentOffset)
    }


}
